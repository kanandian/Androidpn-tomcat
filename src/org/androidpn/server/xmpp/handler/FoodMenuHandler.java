package org.androidpn.server.xmpp.handler;

import org.androidpn.server.inquiry.InquiryHandler;
import org.androidpn.server.model.Bussiness;
import org.androidpn.server.model.FoodMenu;
import org.androidpn.server.service.FoodMenuService;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.xmpp.UnauthorizedException;
import org.androidpn.server.xmpp.handler.IQHandler;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.Session;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;

public class FoodMenuHandler extends IQHandler {

    private static final String NAMESPACE = "androidpn:iq:food_menu";

    private FoodMenuService foodMenuService;

    private Element probeResponse;

    public FoodMenuHandler() {
        foodMenuService = ServiceLocator.getFoodMenuService();
        probeResponse = DocumentHelper.createElement(QName.get("bussiness",
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
            } else {
                reply.setTo((JID) null);
                reply.setChildElement(probeResponse.createCopy());
            }
        } else if (IQ.Type.set.equals(packet.getType())) {
            reply = IQ.createResultIQ(packet);
            if (session.getStatus() == Session.STATUS_AUTHENTICATED) {
                // TODO
                Element query = packet.getChildElement();
                String foodName = query.elementText("foodName");
                String priceStr = query.elementText("price");
                String bussinessId = query.elementText("bussinessId");

                Bussiness bussiness = new Bussiness();


                reply.setChildElement(probeResponse);
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
