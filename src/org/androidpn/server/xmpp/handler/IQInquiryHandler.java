package org.androidpn.server.xmpp.handler;

import org.androidpn.server.inquiry.AdminHandler;
import org.androidpn.server.inquiry.InquiryHandler;
import org.androidpn.server.inquiry.impl.*;
import org.androidpn.server.model.Bussiness;
import org.androidpn.server.service.BussinessService;
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
                } else if ("comment".equals(target)) {
                    InquiryHandler commentInquiryHandler = new CommentInquiryHandler();
                    reply = commentInquiryHandler.handle(reply, title);
                } else if ("holder".equals(target)) {
                    InquiryHandler holderHandler = new HolderInquiryHandler();
                    reply = holderHandler.handle(reply, title);
                } else if ("menu_food".equals(target)) {
                    InquiryHandler foodMenuHandler = new FoodMenuInquiryHandler();
                    reply = foodMenuHandler.handle(reply, title);
                } else if ("takeout".equals(target)) {
                    InquiryHandler takeoutHandler = new TakeoutOrderInquiryHandler();
                    reply = takeoutHandler.handle(reply, title);
                }
            }
        } else if (IQ.Type.set.equals(packet.getType())) {
            reply = IQ.createResultIQ(packet);
            if (session.getStatus() == Session.STATUS_AUTHENTICATED) {
                // TODO
                Element query = packet.getChildElement();
                String target = query.elementText("target");
                String title = query.elementText("title");

                String userName = query.elementText("username");
                String content = query.elementText("content");

                if ("comment".equals(target)) {
                    AdminHandler commentHandler = new CommentHandler(title, userName);
                    commentHandler.handle(title, content);
                }
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
