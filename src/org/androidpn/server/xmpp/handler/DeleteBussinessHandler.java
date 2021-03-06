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

public class DeleteBussinessHandler extends IQHandler {

    private static final String NAMESPACE = "androidpn:delete:bussiness";
    private BussinessService bussinessService;
    private Element probeResponse;

    public DeleteBussinessHandler() {
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
                String bussinessId = query.elementText("bussinessid");

                bussinessService.deleteBussiness(bussinessId);

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
