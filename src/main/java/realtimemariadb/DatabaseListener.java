package realtimemariadb;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.QueryEventData;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import jakarta.inject.Singleton;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Singleton
public class DatabaseListener implements ApplicationEventListener<ServerStartupEvent> {


    private final MainChannel mainChannel;

    public DatabaseListener(MainChannel mainChannel) {
        this.mainChannel = mainChannel;
    }

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseListener.class);

    @Override
    public void onApplicationEvent(ServerStartupEvent event) {
        listen();
    }

    public void listen() {
        BinaryLogClient client = new BinaryLogClient("localhost", 3306, "test", "test");
        EventDeserializer eventDeserializer = new EventDeserializer();
        eventDeserializer.setCompatibilityMode(
                EventDeserializer.CompatibilityMode.DATE_AND_TIME_AS_LONG,
                EventDeserializer.CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY);
        client.setEventDeserializer(eventDeserializer);
        client.registerEventListener(new BinaryLogClient.EventListener() {
            @Override
            public void onEvent(Event event) {
                if (event.getHeader().getEventType() == EventType.QUERY) {
                    QueryEventData queryEventData = event.getData();

                    //parse the query from event
                    Statement statement = null;

                    try {
                        statement = CCJSqlParserUtil.parse(queryEventData.getSql());
                    } catch (JSQLParserException e) {
                        LOG.error(String.format("Error when parsing query event data : %s", e.getMessage()));
                        e.printStackTrace();
                    }

                    //get query type so we can cast statement to the appropriate class
                    String queryType = statement.getClass().getSimpleName().toUpperCase();
                    String tableName = null;

                    switch (queryType) {
                        case "INSERT":
                            Insert insertStatement = (Insert) statement;
                            tableName = insertStatement.getTable().getName();
                            break;
                        case "DELETE":
                            Delete deleteStatement = (Delete) statement;
                            tableName = deleteStatement.getTable().getName();
                            break;
                        case "UPDATE":
                            Update updateStatement = (Update) statement;
                            tableName = updateStatement.getTable().getName();
                            break;
                        default:
                            System.out.println(String.format("unsupported query to watch %s", queryType));
                    }

                    //broadcast event
                    DataEvent dataEvent = new DataEvent(tableName, null);
                    System.out.println(String.format("Data changes on table %s", tableName));
                    mainChannel.broadcast(dataEvent);
                }
            }
        });

        try {
            client.connect();
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }
}
