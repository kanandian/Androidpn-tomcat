package org.androidpn.server.service;

import org.androidpn.server.model.ChatMessage;
import sun.plugin2.message.Message;

import java.util.List;

public interface ChatMessageService {

    public void saveMessage(ChatMessage message);
    public void removeMessage(long messageId);

    public List<ChatMessage> getMessagesByUserName(String userName);

}
