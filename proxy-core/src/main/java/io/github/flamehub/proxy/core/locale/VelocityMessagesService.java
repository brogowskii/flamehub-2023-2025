package io.github.flamehub.proxy.core.locale;

import io.github.flamehub.commons.message.MessagesService;
import io.github.flamehub.proxy.core.text.TextBuilder;

import java.util.List;

public class VelocityMessagesService extends MessagesService {

    public TextBuilder getAsText(String path) {
        List<String> messages = this.getMessages(path);
        return TextBuilder.builder().text(messages);
    }

}
