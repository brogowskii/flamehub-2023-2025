package io.github.flamehub.commons.network.message;

import io.github.flamehub.commons.messenger.packet.Packet;

import java.util.List;

public final class NetworkMessage implements Packet {

    private final List<String> messages;
    private final NetworkMessageFilter filter;
    private final NetworkMessageType type;

    public NetworkMessage(List<String> messages, NetworkMessageFilter filter, NetworkMessageType type) {
        this.messages = messages;
        this.filter = filter;
        this.type = type;
    }

    public List<String> getMessages() {
        return messages;
    }

    public NetworkMessageFilter getFilter() {
        return filter;
    }

    public NetworkMessageType getType() {
        return type;
    }
}
