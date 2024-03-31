package io.github.flamehub.commons.network.message;

import io.github.flamehub.commons.messenger.RedisMessenger;

import java.util.Collections;
import java.util.List;

public final class NetworkMessageService {

    private final RedisMessenger redisMessenger;
    private final String channel;

    public NetworkMessageService(RedisMessenger redisMessenger, String channel) {
        this.redisMessenger = redisMessenger;
        this.channel = channel;
    }

    public void send(String message, NetworkMessageFilter filter, NetworkMessageType type) {
        NetworkMessage networkMessage = new NetworkMessage(Collections.singletonList(message), filter, type);
        this.redisMessenger.publish(channel, networkMessage);
    }

    public void send(List<String> messages, NetworkMessageFilter filter, NetworkMessageType type) {
        NetworkMessage networkMessage = new NetworkMessage(messages, filter, type);
        this.redisMessenger.publish(channel, networkMessage);
    }

    public void send(String message, NetworkMessageType type) {
        send(message, NetworkMessageFilter.builder().build(), type);
    }

    public void send(List<String> messages, NetworkMessageType type) {
        send(messages, NetworkMessageFilter.builder().build(), type);
    }


}
