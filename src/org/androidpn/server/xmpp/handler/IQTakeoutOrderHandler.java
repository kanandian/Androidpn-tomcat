package org.androidpn.server.xmpp.handler;

import gnu.inet.encoding.StringprepException;
import org.androidpn.server.model.TakeoutOrder;
import org.androidpn.server.model.TakeoutOrderItem;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class IQTakeoutOrderHandler extends IQHandler {

    private static String NAMESPACE = "androidpn:takeout:order";

    private UserService userService;
    private TakeoutOrderService takeoutOrderService;
    private Element probeResponse;

    public IQTakeoutOrderHandler() {
        userService = ServiceLocator.getUserService();
        takeoutOrderService = ServiceLocator.getTakeoutOrderService();
        probeResponse = DocumentHelper.createElement(QName.get("payment",
                "androidpn:iq:payment"));
    }

    @Override
    public IQ handleIQ(IQ packet) throws UnauthorizedException {
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

                    TakeoutOrder takeoutOrder = new TakeoutOrder();
                    List<TakeoutOrderItem> takeoutOrderItemList = new ArrayList<TakeoutOrderItem>();

                    long orderId = new Date().getTime();

                    takeoutOrder.setOrderId(orderId);

                    for (Iterator<Element> iterator = query.elementIterator();iterator.hasNext();) {
                        Element element = iterator.next();

                        if ("orderitem".equals(element.getName())) {
//                            for (int i=0;i<element.attributeCount();i++) {
//
//                            }
                            TakeoutOrderItem takeoutOrderItem = new TakeoutOrderItem();
                            takeoutOrderItem.setBussinessId(Long.parseLong(element.attributeValue("bussinessId")));
                            takeoutOrderItem.setCount(Integer.parseInt(element.attributeValue("count")));
                            takeoutOrderItem.setFoodName(element.attributeValue("foodName"));
                            takeoutOrderItem.setPrice(Double.parseDouble(element.attributeValue("price")));

                            takeoutOrderItem.setOrderId(orderId);

                            takeoutOrderItemList.add(takeoutOrderItem);
                        } else if ("address".equals(element.getName())) {
                            takeoutOrder.setAddress(element.getText());
                        } else if ("note".equals(element.getName())) {
                            takeoutOrder.setNote(element.getText());
                        } else if ("mobile".equals(element.getName())) {
                            takeoutOrder.setMobile(element.getText());
                        } else if ("totalprice".equals(element.getName())) {
                            takeoutOrder.setTotalPrice(Double.parseDouble(element.getText()));
                        } else if ("fromusername".equals(element.getName())) {
                            takeoutOrder.setFromUserName(element.getText());
                        } else if ("tousername".equals(element.getName())) {
                            takeoutOrder.setToUserName(element.getText());
                        } else if ("bussinessid".equals(element.getName())) {
                            takeoutOrder.setBussinessId(Long.parseLong(element.getText()));
                        } else if ("bussinessname".equals(element.getName())) {
                            takeoutOrder.setBussinessName(element.getText());
                        }
                    }

                    takeoutOrder.setTakeoutOrderItemList(takeoutOrderItemList);

                    String fromUserName = takeoutOrder.getFromUserName();
                    String toUserName = takeoutOrder.getToUserName();
                    double price = takeoutOrder.getTotalPrice();

                    ResultModel resultModel = userService.payment(fromUserName, toUserName, price);

                    if (resultModel.getErrcode() == 0) {
                        takeoutOrderService.saveTakeoutOrder(takeoutOrder);
                    }

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
