package realtimemariadb;

import com.github.shyiko.mysql.binlog.BinaryLogFileReader;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BinlogUtil {

    /**
     * @param file binfile path
     * @return a List containing Map that consist of queryType & tableName or return empty list
     * @throws IOException
     */
    static List<Map> parseBinlogFile(String file) throws IOException {
        BinaryLogFileReader reader = null;
        List<Map> events = new ArrayList<>();
        try {
            File binlogFile = new File(file);

            EventDeserializer eventDeserializer = new EventDeserializer();
            eventDeserializer.setCompatibilityMode(
                    EventDeserializer.CompatibilityMode.DATE_AND_TIME_AS_LONG,
                    EventDeserializer.CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY
            );
            reader = new BinaryLogFileReader(binlogFile, eventDeserializer);

            //read every event in binlog files
            for (Event event; (event = reader.readEvent()) != null; ) {

                //get event type, since it doesn't support all type from mariadb (https://github.com/osheroff/mysql-binlog-connector-java?tab=readme-ov-file#mariadb)
                //it will always return Query event type so we need to distinguish  the type ourself
                //by passing it to query parser
                if (event.getHeader().getEventType() == EventType.QUERY) {
                    QueryEventData queryEventData = event.getData();

                    //parse the query from event
                    Statement statement = CCJSqlParserUtil.parse(queryEventData.getSql());

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
                    events.add(e);
                }
            }
        } catch (JSQLParserException e) {
            System.out.println(String.format("Error when parsing query %s", e.getMessage()));
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return events;
    }
}
