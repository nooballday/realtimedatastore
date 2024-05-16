package realtimemariadb;

import io.micronaut.serde.annotation.Serdeable;

import java.util.List;
import java.util.Map;

@Serdeable
public class DataEvent {

    long timestamp;

    String tableName;

    List<Map<String, Object>> data;

    public DataEvent(String tableName, List<Map<String, Object>> data) {
        this.timestamp = System.currentTimeMillis();
        this.tableName = tableName;
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTableName() {
        return tableName.replaceAll("`", "");
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }
}
