package realtimemariadb;

import io.micronaut.websocket.WebSocketBroadcaster;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class MainChannel implements Channel {

    //in memory store that record active subscription
    //Map<TableName, Set<Subscriptions>
    private final Map<String, Set<Subscription>> subscriptions = new HashMap<>();


    public MainChannel() {}

    @Override
    public void subscribe(Subscription subscription) {
        //register active subscription
        Set<Subscription> ss = subscriptions.get(subscription.getTableName());
        if (ss != null) {
            ss.add(subscription);
        } else {
            //TODO: need to check if table exists
            subscriptions.put(subscription.getTableName(), Set.of(subscription));
        }
    }

    //it only handles tableName for now
    @Override
    public void broadcast(DataEvent dataEvent) {
        if (dataEvent == null) {
            return;
        }
        //broadcast changes to subscribed users
        Set<Subscription> subscribed = subscriptions.get(dataEvent.getTableName());
        if (subscribed != null) {
            subscribed.forEach(s -> {
                //send to each client
                s.getWebsocketSession().sendSync(dataEvent);
            });
        }
    }

    @Override
    public void unsubscribe(String sessionId) {
        //when user disconnect, delete all entry from subscription
        subscriptions.forEach((key, value) -> {
            //remove session from subscription
            Set<Subscription> mValue = value.stream().filter( v -> v.getSessionId() != sessionId).collect(Collectors.toSet());
            subscriptions.put(key, mValue);
        });
    }
}
