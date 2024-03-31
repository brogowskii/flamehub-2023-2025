package io.github.flamehub.commons.messenger;

import io.github.flamehub.commons.messenger.packet.Packet;
import io.github.flamehub.commons.messenger.packet.PacketRequest;
import io.github.flamehub.commons.messenger.packet.PacketResponse;

import java.util.concurrent.CompletableFuture;

public interface Messenger {

    <T extends Packet> void publish(String channel, T message);

    <T extends Packet> void publishMany(String channel, T[] messages);

    void subscribe(String channel, Object listener);

    void subscribe(String[] channels, Object listener);

    void subscribeMany(String channel, Object[] listener);

    void subscribeMany(String[] channels, Object[] listener);

    void subscribeCallbacks(String channel);

    <T extends PacketResponse> CompletableFuture<T> publishFuture(String channel, PacketRequest request);

}
