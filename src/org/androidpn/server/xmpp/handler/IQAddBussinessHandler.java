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

public class IQAddBussinessHandler extends IQHandler {
    private static final String NAMESPACE = "androidpn:add:bussiness";

    BussinessService bussinessService;
    Element probeResponse;

    public IQAddBussinessHandler() {
        bussinessService = ServiceLocator.getBussinessService();
    }

    @Override
    public IQ handleIQ(IQ packet) throws UnauthorizedException {
        IQ reply = null;

        probeResponse = DocumentHelper.createElement(QName.get("payment",
                "androidpn:iq:payment"));

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
                String imageURL = query.elementText("imageURL");
                String bussinessName = query.elementText("businessName");
                String classification = query.elementText("classification");
                String tag = query.elementText("tag");
                String location = query.elementText("location");
                String mobile = query.elementText("mobile");
                String des = query.elementText("des");
                String holder = query.elementText("holder");

                Bussiness bussiness = new Bussiness();

                bussiness.setImageURL(imageURL);
                bussiness.setBusinessName(bussinessName);
                bussiness.setClassification(classification);
                bussiness.setTag(tag);
                bussiness.setLocation(location);
                bussiness.setMobile(mobile);
                bussiness.setDes(des);
                bussiness.setHolder(holder);
                bussiness.setImageURL("http://localhost:8080/bussinessimage/bussinessdefault.jpg");

                bussinessService.saveBussiness(bussiness);

                probeResponse.addElement("errcode").setText("0");
                probeResponse.addElement("errmessage").setText("成功");

                reply.setChildElement(probeResponse);
//                String title = query.elementText("title");
//
//                String userName = query.elementText("username");
//                String content = query.elementText("content");
//
//                if ("comment".equals(target)) {
//                    AdminHandler commentHandler = new CommentHandler(title, userName);
//                    commentHandler.handle(title, content);
//                }
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
