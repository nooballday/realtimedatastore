package realtimemariadb;


import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

@ServerWebSocket("/channel/{userName}")
public class WebSocketServer {

    private final MainChannel mainChannel;

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketServer.class);

    public WebSocketServer(MainChannel mainChannel) {
        this.mainChannel = mainChannel;
    }

    @OnOpen
    public void onOpen(String userName, WebSocketSession session) {
        String msg = String.format("User %s joined!", userName);
        LOG.info(msg);
    }

    @OnMessage
    public void onMessage(String userName, String tableName, WebSocketSession session) {
        //every message that comes will be assumed as subscription
        Subscription subscription = new Subscription(session, tableName);
        mainChannel.subscribe(subscription);
    }

    @OnClose
    public void onClose(String userName, WebSocketSession session)  {
        mainChannel.unsubscribe(session.getId());
    }

}
