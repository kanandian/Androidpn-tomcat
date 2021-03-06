package org.androidpn.server.xmpp.handler;

import org.androidpn.server.inquiry.InquiryHandler;
import org.androidpn.server.inquiry.impl.ActivityInquiryHandler;
import org.androidpn.server.inquiry.impl.BussinessInquiryHandler;
import org.androidpn.server.inquiry.impl.InfoInquiryHandler;
import org.androidpn.server.inquiry.impl.SearchInquiryHandler;
import org.androidpn.server.model.ChatMessage;
import org.androidpn.server.model.User;
import org.androidpn.server.service.ChatMessageService;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.service.UserService;
import org.androidpn.server.xmpp.UnauthenticatedException;
import org.androidpn.server.xmpp.UnauthorizedException;
import org.androidpn.server.xmpp.auth.AuthManager;
import org.androidpn.server.xmpp.auth.AuthToken;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.Session;
import org.androidpn.server.xmpp.session.SessionManager;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.PacketError;

import java.util.List;

public class IQLoginHandler extends IQHandler {

    private static final String NAMESPACE = "androidpn:iq:login";

    private UserService userService;
    private ChatMessageService chatMessageService;

    private Element probeResponse;

    public IQLoginHandler() {
        userService = ServiceLocator.getUserService();
        chatMessageService = ServiceLocator.getChatMessageService();
    }

    @Override
    public IQ handleIQ(IQ packet) throws UnauthorizedException {
        probeResponse = DocumentHelper.createElement(QName.get("login",
                NAMESPACE));
        IQ reply = null;
        System.out.println();
        System.out.println("my received" + packet.toXML());
        System.out.println();


        ClientSession session = sessionManager.getSession(packet.getFrom());
        if (session == null) {
            log.error("Session not found for key " + packet.getFrom());
            reply = IQ.createResultIQ(packet);
            reply.setChildElement(packet.getChildElement().createCopy());
            reply.setError(PacketError.Condition.internal_server_error);
            return reply;
        }

        if (IQ.Type.get.equals(packet.getType())) {
            reply = IQ.createResultIQ(packet);
            if (session.getStatus() == Session.STATUS_AUTHENTICATED) {
                // TODO
                Element query = packet.getChildElement();
                String userName = query.elementText("userName");
                String password = query.elementText("password");

                try {
                    User user = userService.getUserByUsername(userName);

                    if (password == null || !password.equals(user.getPassword())) {
                        probeResponse.addElement("message").setText("密码不正确");
                    } else {
                        createResultElement(probeResponse, user);
                        JID from = session.getAddress();
                        JID newFrom = new JID(userName, from.getDomain(), from.getResource());
                        changeUserInfo(session, newFrom, password);

//                        SessionManager.getInstance().addUserSessino(session);
                        List<ChatMessage> chatMessageList = chatMessageService.getMessagesByUserName(userName);
                        if (chatMessageList != null && !chatMessageList.isEmpty()) {
                            for (ChatMessage chatMessage : chatMessageList) {
                                Message message = new Message();
                                message.setBody(chatMessage.getContent());
                                message.setFrom(chatMessage.getFromUserJID());
                                message.setTo(session.getAddress());

                                session.deliver(message);

                                chatMessageService.removeMessage(chatMessage.getMessageId());
                            }
                        }
                    }

                } catch (UserNotFoundException e) {
                    e.printStackTrace();

                    probeResponse.addElement("message").setText("该用户不存在");
                } catch (UnauthenticatedException e) {
                    e.printStackTrace();
                }

                reply.setChildElement(probeResponse);

//            else {
//                reply.setTo((JID) null);
//                reply.setChildElement(probeResponse.createCopy());
//            }
            }
        }
        // Send the response directly to the session
        if (reply != null) {
            session.process(reply);
            System.out.println();
            System.out.println("my response" + reply.toXML());
            System.out.println();
        }
        return null;
    }

    private void changeUserInfo(ClientSession session, JID newFrom, String password) throws UnauthenticatedException {

        JID from = session.getAddress();

        SessionManager.getInstance().changeUserName(from.toString(), newFrom.toString());

        AuthToken authToken = AuthManager.authenticate(newFrom.getNode(), password);
        session.setAuthToken(authToken);
        session.setAddress(newFrom);
    }

    private void createResultElement(Element probeResponse, User user) {
        String imageURL = user.getImageURL();
        probeResponse.addElement("userId").setText(String.valueOf(user.getId()));
        probeResponse.addElement("userName").setText(user.getUsername());
        probeResponse.addElement("name").setText(user.getName());
        probeResponse.addElement("mobile").setText(user.getMobile());
        probeResponse.addElement("usertype").setText(String.valueOf(user.getUserType()));
        if (imageURL != null) {
            probeResponse.addElement("imageURL").setText(imageURL);
        }
        if (user.getRealUser()) {
            probeResponse.addElement("person").setText("1");
        } else {
            probeResponse.addElement("person").setText("0");
        }
    }



    @Override
    public String getNamespace() {
        return NAMESPACE;
    }
}
