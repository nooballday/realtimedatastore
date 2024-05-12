package realtimemariadb;

import com.github.shyiko.mysql.binlog.BinaryLogFileReader;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.QueryEventData;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Application {

    public static void main(String[] args) throws IOException, InterruptedException {

        String binlogFile = "mybinlog-bin.000003";
        String logDirectory = "C:\\Program Files\\MariaDB 11.0\\data";

        WatchService watchService = FileSystems.getDefault().newWatchService();

        Path path = Paths.get(logDirectory);

        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        WatchKey watchKey = watchService.take();
        while (true) {
            List<WatchEvent<?>> pollEvents = watchKey.pollEvents();
            for (WatchEvent event : pollEvents) {
                if (binlogFile.toUpperCase().equals(event.context().toString().toUpperCase())) {
                    List<Map> events = BinlogUtil.parseBinlogFile(logDirectory + "\\" + binlogFile);
                    System.out.println(events);
                    watchKey.reset();
                }
            }
        }
    }
}