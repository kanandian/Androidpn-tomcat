package org.androidpn.server.dao;

import org.androidpn.server.model.ChatMessage;
import sun.plugin2.message.Message;

import java.util.List;

public interface ChatMessageDao {

    public void saveMessage(ChatMessage message);
    public void removeMessage(long messageId);

    public List<ChatMessage> getMessagesByUserName(String userName);

}
