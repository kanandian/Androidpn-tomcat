package org.androidpn.server.xmpp.handler;

import gnu.inet.encoding.StringprepException;
import org.androidpn.server.model.Payment;
import org.androidpn.server.service.*;
import org.androidpn.server.util.ResultModel;
import org.androidpn.server.xmpp.UnauthorizedException;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.Session;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;

import java.util.Date;

public class IQPaymentHandler extends IQHandler {

    private static final String NAMESPACE = "androidpn:iq:payment";

    private UserService userService;

    private PaymentService paymentService;

    private Element probeResponse;

    public IQPaymentHandler() {
        userService = ServiceLocator.getUserService();
        paymentService = ServiceLocator.getPaymentService();
    }

    @Override
    public IQ handleIQ(IQ packet) throws UnauthorizedException {
        probeResponse = DocumentHelper.createElement(QName.get("payment",
                NAMESPACE));
        IQ reply = null;

        ClientSession session = sessionManager.getSession(packet.getFrom());
        if (session == null) {
            log.error("Session not found for key " + packet.getFrom());
            reply = IQ.createResultIQ(packet);
            reply.setChildElement(packet.getChildElement().createCopy());
            reply.setError(PacketError.Condition.internal_server_error);
            System.out.println("qzf session == null");
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
            try {
                Element query = packet.getChildElement();
                if (query.element("remove") != null) {
                    if (session.getStatus() == Session.STATUS_AUTHENTICATED) {
                        // TODO
                    } else {
                        throw new UnauthorizedException();
                    }
                } else {
                    reply = IQ.createResultIQ(packet);

                    String fromUserName = query.elementText("fromusername");
                    String toUserName = query.elementText("tousername");
                    String bussinessName = query.elementText("bussinessname");
                    long bussinessId = Long.parseLong(query.elementText("bussinessid"));
                    double price = Double.parseDouble(query.elementText("price"));

                    Payment payment = new Payment();
                    payment.setFromUserName(fromUserName);
                    payment.setToUserName(toUserName);
                    payment.setBussinessId(bussinessId);
                    payment.setPrice(price);
                    payment.setBussinessName(bussinessName);
                    payment.setCreateTime(new Date().getTime());

                    paymentService.savePayment(payment);

                    ResultModel resultModel = userService.payment(fromUserName, toUserName, price);

                    probeResponse.addElement("errcode").setText(String.valueOf(resultModel.getErrcode()));
                    probeResponse.addElement("errmessage").setText(resultModel.getErrMessage());

                    reply.setChildElement(probeResponse);

                }
            } catch (Exception ex) {
                log.error(ex);
                reply = IQ.createResultIQ(packet);
                reply.setChildElement(packet.getChildElement().createCopy());
                System.out.println("qzf exception");
                if (ex instanceof UserExistsException) {
                    reply.setError(PacketError.Condition.conflict);
                } else if (ex instanceof UserNotFoundException) {
                    reply.setError(PacketError.Condition.bad_request);
                } else if (ex instanceof StringprepException) {
                    reply.setError(PacketError.Condition.jid_malformed);
                } else if (ex instanceof IllegalArgumentException) {
                    reply.setError(PacketError.Condition.not_acceptable);
                } else {
                    reply.setError(PacketError.Condition.internal_server_error);
                }
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
