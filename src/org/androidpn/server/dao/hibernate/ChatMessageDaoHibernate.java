package org.androidpn.server.dao.hibernate;

import org.androidpn.server.dao.ChatMessageDao;
import org.androidpn.server.model.ChatMessage;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import sun.plugin2.message.Message;

import java.util.List;

public class ChatMessageDaoHibernate extends HibernateDaoSupport implements ChatMessageDao {
    @Override
    public void saveMessage(ChatMessage message) {
        getHibernateTemplate().saveOrUpdate(message);
    }

    @Override
    public void removeMessage(long messageId) {
        ChatMessage message = new ChatMessage();
        message.setMessageId(messageId);
        getHibernateTemplate().delete(message);
    }

    @Override
    public List<ChatMessage> getMessagesByUserName(String userName) {
        return getHibernateTemplate().find("from ChatMessage m where m.toUserName = ?", userName);
    }
}
