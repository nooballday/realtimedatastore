package realtimemariadb;

public interface Channel {
    void subscribe(Subscription subscription);
    void broadcast(DataEvent dataEvent);
    void unsubscribe(String userId);
}
