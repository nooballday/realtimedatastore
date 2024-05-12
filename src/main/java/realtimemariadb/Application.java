package realtimemariadb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.BinaryLogClient.EventListener;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.QueryEventData;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;

public class Application {

    public static void main(String[] args) throws IOException, InterruptedException {
        
        BinaryLogClient client = new BinaryLogClient("localhost", 3306, "admin", "test");
        EventDeserializer eventDeserializer = new EventDeserializer();
        eventDeserializer.setCompatibilityMode(
                EventDeserializer.CompatibilityMode.DATE_AND_TIME_AS_LONG,
                EventDeserializer.CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY);
        client.setEventDeserializer(eventDeserializer);
        client.registerEventListener(new EventListener() {
            @Override
            public void onEvent(Event event) {
                if (event.getHeader().getEventType() == EventType.QUERY) {
                    QueryEventData queryEventData = event.getData();

                    //parse the query from event
                    Statement statement = null;

                    try {
                        statement = CCJSqlParserUtil.parse(queryEventData.getSql());
                    } catch (JSQLParserException e) {
                        // TODO Auto-generated catch block
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

                    Map e = new HashMap();
                    e.put("queryType", queryType);
                    e.put("tableName", tableName);
                    System.out.println(e.toString());
                }
            }
        });
        client.connect();
    }
}