package realtimemariadb;

import io.micronaut.websocket.WebSocketSession;

import java.util.List;
import java.util.Map;

public class Subscription {

    private WebSocketSession websocketSession;
    private String tableName;
    private String columName;
    private List<Map<String, Object>> conditions;

    public Subscription(WebSocketSession websocketSession, String tableName) {
        this.websocketSession = websocketSession;
        this.tableName = tableName;
    }

    public String getSessionId() {
        return websocketSession.getId();
    }

    public WebSocketSession getWebsocketSession() {
        return websocketSession;
    }

    public void setWebsocketSession(WebSocketSession websocketSession) {
        this.websocketSession = websocketSession;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumName() {
        return columName;
    }

    public void setColumName(String columName) {
        this.columName = columName;
    }

    public List<Map<String, Object>> getConditions() {
        return conditions;
    }

    public void setConditions(List<Map<String, Object>> conditions) {
        this.conditions = conditions;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Subscription) {
            Subscription s = (Subscription) obj;
            return websocketSession.getId() == s.getSessionId() && tableName == s.getTableName();
        }
        return false;
    }
}
