package org.androidpn.server.xmpp.handler;

import org.androidpn.server.model.Bussiness;
import org.androidpn.server.service.BussinessService;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.xmpp.UnauthorizedException;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.Session;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;

public class IQUpdateBussinessHandler extends IQHandler {

    private static final String NAMESPACE = "androidpn:update:bussiness";

    BussinessService bussinessService;
    Element probeResponse;

    public IQUpdateBussinessHandler() {
        bussinessService = ServiceLocator.getBussinessService();
    }

    @Override
    public IQ handleIQ(IQ packet) throws UnauthorizedException {
        IQ reply = null;

        probeResponse = DocumentHelper.createElement(QName.get("admin",
                "androidpn:admin:operation"));

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
                long bussinessId = Long.parseLong(query.elementText("bussinessId"));
//                String bussinessName = query.elementText("businessName");
//                String location = query.elementText("location");
                String mobile = query.elementText("mobile");
                String des = query.elementText("des");
//                String holder = query.elementText("holder");
                String fromTime = query.elementText("fromtime");
                String toTime = query.elementText("totime");
                String feature = query.elementText("feature");

                Bussiness bussiness = new Bussiness();
                bussiness.setBussinessId(bussinessId);
                bussiness.setMobile(mobile);
                bussiness.setDes(des);
                bussiness.setStartTime(fromTime);
                bussiness.setEndTime(toTime);
                bussiness.setFeature(feature);

                bussinessService.updateBussiness(bussiness);

                probeResponse.addElement("errcode").setText("0");
                probeResponse.addElement("errmessage").setText("成功");

                reply.setChildElement(probeResponse);
            }


        }

        // Send the response directly to the session
        if (reply != null) {
            System.out.println("my response : "+reply.toXML());
            session.process(reply);
        }

        return null;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }
}
