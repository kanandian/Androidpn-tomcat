package org.androidpn.server.xmpp.message;

import org.androidpn.server.model.ChatMessage;
import org.androidpn.server.service.ChatMessageService;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.util.ResultModel;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.SessionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

import java.util.Date;

public class MessageHandler {

    protected final Log log = LogFactory.getLog(getClass());

    protected SessionManager sessionManager;
    protected ChatMessageService chatMessageService;

    public MessageHandler() {
        sessionManager = SessionManager.getInstance();
        chatMessageService = ServiceLocator.getChatMessageService();
    }

    public void process(Packet packet) {
        Message message = (Message) packet;

        handleMessage(message);
    }

    public ResultModel handleMessage(Message message) {
        ResultModel resultModel = new ResultModel();

        JID toUser = message.getTo();
        String toUserName = toUser.getDomain();
        JID sender = message.getFrom();
        String content = message.getBody();

        ClientSession session = sessionManager.getSession(toUserName);

        if (session == null) {
            System.out.println("user don't online");

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setToUserName(toUserName);
            chatMessage.setFromUserName(sender.getNode());
            chatMessage.setFromUserJID(sender.toString());
            chatMessage.setContent(content);
            chatMessage.setCreateTime(new Date().getTime());

            chatMessageService.saveMessage(chatMessage);
        } else {
            session.deliver(message);
        }



        return resultModel;
    }
}
