package org.androidpn.server.xmpp.handler;

import org.androidpn.server.inquiry.InquiryHandler;
import org.androidpn.server.inquiry.impl.ActivityInquiryHandler;
import org.androidpn.server.inquiry.impl.BussinessInquiryHandler;
import org.androidpn.server.inquiry.impl.InfoInquiryHandler;
import org.androidpn.server.inquiry.impl.SearchInquiryHandler;
import org.androidpn.server.model.User;
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
import org.xmpp.packet.PacketError;

public class IQLoginHandler extends IQHandler {

    private static final String NAMESPACE = "androidpn:iq:login";

    private UserService userService;

    private Element probeResponse;

    public IQLoginHandler() {
        userService = ServiceLocator.getUserService();
        probeResponse = DocumentHelper.createElement(QName.get("login",
                NAMESPACE));
    }

    @Override
    public IQ handleIQ(IQ packet) throws UnauthorizedException {
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
        probeResponse.addElement("userName").setText(user.getUsername());
        probeResponse.addElement("password").setText(user.getPassword());
        probeResponse.addElement("name").setText(user.getName());
        probeResponse.addElement("mobile").setText(user.getMobile());
    }



    @Override
    public String getNamespace() {
        return NAMESPACE;
    }
}
