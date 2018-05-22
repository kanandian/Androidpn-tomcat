package org.androidpn.server.service.impl;

import org.androidpn.server.dao.ChatMessageDao;
import org.androidpn.server.model.ChatMessage;
import org.androidpn.server.service.ChatMessageService;
import sun.plugin2.message.Message;

import java.util.List;

public class ChatMessageServiceImpl implements ChatMessageService {

    private ChatMessageDao chatMessageDao;

    public ChatMessageServiceImpl() {
    }

    @Override
    public void saveMessage(ChatMessage message) {
        chatMessageDao.saveMessage(message);
    }

    @Override
    public void removeMessage(long messageId) {
        chatMessageDao.removeMessage(messageId);
    }

    @Override
    public List<ChatMessage> getMessagesByUserName(String userName) {
        return chatMessageDao.getMessagesByUserName(userName);
    }

    public ChatMessageDao getChatMessageDao() {
        return chatMessageDao;
    }

    public void setChatMessageDao(ChatMessageDao chatMessageDao) {
        this.chatMessageDao = chatMessageDao;
    }
}
