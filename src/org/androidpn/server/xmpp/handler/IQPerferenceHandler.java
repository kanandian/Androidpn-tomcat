package org.androidpn.server.xmpp.handler;

import org.androidpn.server.inquiry.InquiryHandler;
import org.androidpn.server.inquiry.impl.ActivityInquiryHandler;
import org.androidpn.server.inquiry.impl.BussinessInquiryHandler;
import org.androidpn.server.inquiry.impl.InfoInquiryHandler;
import org.androidpn.server.inquiry.impl.SearchInquiryHandler;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserService;
import org.androidpn.server.xmpp.UnauthorizedException;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.Session;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;

public class IQPerferenceHandler extends IQHandler {

    private static final String NAMESPACE = "androidpn:iq:perference";

    private UserService userService;

    public IQPerferenceHandler() {
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

        if (IQ.Type.set.equals(packet.getType())) {
            reply = IQ.createResultIQ(packet);
            if (session.getStatus() == Session.STATUS_AUTHENTICATED) {
                // TODO
                Element query = packet.getChildElement();
                String userName = query.elementText("username");
                String tag = query.elementText("tag");

                userService.addPerferences(userName, tag);
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

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }
}
