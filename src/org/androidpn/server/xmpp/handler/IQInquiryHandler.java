package org.androidpn.server.xmpp.handler;

import org.androidpn.server.inquiry.InquiryHandler;
import org.androidpn.server.inquiry.impl.ActivityInquiryHandler;
import org.androidpn.server.inquiry.impl.BussinessInquiryHandler;
import org.androidpn.server.inquiry.impl.InfoInquiryHandler;
import org.androidpn.server.inquiry.impl.SearchInquiryHandler;
import org.androidpn.server.model.Bussiness;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserService;
import org.androidpn.server.xmpp.UnauthorizedException;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.Session;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;

public class IQInquiryHandler extends IQHandler {

    private static final String NAMESPACE = "androidpn:iq:inquiry";

    private UserService userService;

    public IQInquiryHandler () {
        userService = ServiceLocator.getUserService();
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
                String target = query.elementText("target");
                String title = query.elementText("title");

                if("activity".equals(target)) {
                    InquiryHandler activityInquiryHandler = new ActivityInquiryHandler();
                    reply = activityInquiryHandler.handle(reply, title);
                } else if("bussiness".equals(target)) {
                    InquiryHandler bussinessInquiryHandler = new BussinessInquiryHandler();
                    reply = bussinessInquiryHandler.handle(reply, title);
                } else if("search".equals(target)) {
                    InquiryHandler searchInquiryHandler = new SearchInquiryHandler();
                    reply = searchInquiryHandler.handle(reply, title);
                } else if("info".equals(target)) {
                    InquiryHandler infoInquiryHandler = new InfoInquiryHandler();
                    reply = infoInquiryHandler.handle(reply, title);
                }
            }
//            else {
//                reply.setTo((JID) null);
//                reply.setChildElement(probeResponse.createCopy());
//            }
        }
//        else if (IQ.Type.set.equals(packet.getType())) {
//            try {
//                Element query = packet.getChildElement();
//                if (query.element("remove") != null) {
//                    if (session.getStatus() == Session.STATUS_AUTHENTICATED) {
//                        // TODO
//                    } else {
//                        throw new UnauthorizedException();
//                    }
//                } else {
//                    String username = query.elementText("username");
//                    String password = query.elementText("password");
//                    String email = query.elementText("email");
//                    String name = query.elementText("name");
//
//                    // Verify the username
//                    if (username != null) {
//                        Stringprep.nodeprep(username);
//                    }
//
//                    // Deny registration of users with no password
//                    if (password == null || password.trim().length() == 0) {
//                        reply = IQ.createResultIQ(packet);
//                        reply.setChildElement(packet.getChildElement()
//                                .createCopy());
//                        reply.setError(PacketError.Condition.not_acceptable);
//                        return reply;
//                    }
//
//                    if (email != null && email.matches("\\s*")) {
//                        email = null;
//                    }
//
//                    if (name != null && name.matches("\\s*")) {
//                        name = null;
//                    }
//
//                    User user;
//                    if (session.getStatus() == Session.STATUS_AUTHENTICATED) {
//                        user = userService.getUser(session.getUsername());
//                    } else {
//                        user = new User();
//                    }
//                    user.setUsername(username);
//                    user.setPassword(password);
//                    user.setEmail(email);
//                    user.setName(name);
//                    userService.saveUser(user);
//
//                    reply = IQ.createResultIQ(packet);
//                }
//            } catch (Exception ex) {
//                log.error(ex);
//                reply = IQ.createResultIQ(packet);
//                reply.setChildElement(packet.getChildElement().createCopy());
//                if (ex instanceof UserExistsException) {
//                    reply.setError(PacketError.Condition.conflict);
//                } else if (ex instanceof UserNotFoundException) {
//                    reply.setError(PacketError.Condition.bad_request);
//                } else if (ex instanceof StringprepException) {
//                    reply.setError(PacketError.Condition.jid_malformed);
//                } else if (ex instanceof IllegalArgumentException) {
//                    reply.setError(PacketError.Condition.not_acceptable);
//                } else {
//                    reply.setError(PacketError.Condition.internal_server_error);
//                }
//            }
//        }

        // Send the response directly to the session
        if (reply != null) {
            session.process(reply);
            System.out.println();
            System.out.println("my response" + reply.toXML());
            System.out.println();
        }
        return null;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }
}
