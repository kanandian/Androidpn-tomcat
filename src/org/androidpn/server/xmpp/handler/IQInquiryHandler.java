package org.androidpn.server.xmpp.handler;

import org.androidpn.server.inquiry.AdminHandler;
import org.androidpn.server.inquiry.InquiryHandler;
import org.androidpn.server.inquiry.impl.*;
import org.androidpn.server.model.Bussiness;
import org.androidpn.server.service.BussinessService;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserService;
import org.androidpn.server.util.MahoutUtil;
import org.androidpn.server.util.ResultModel;
import org.androidpn.server.xmpp.UnauthorizedException;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.Session;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class IQInquiryHandler extends IQHandler {

    private static final String NAMESPACE = "androidpn:iq:inquiry";

    private UserService userService;

    public IQInquiryHandler () {
        userService = ServiceLocator.getUserService();
        MahoutUtil mahoutUtil = MahoutUtil.getInstance();
        mahoutUtil.createNewDataSet();
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
                    String userName = query.elementText("username");
                    InquiryHandler bussinessInquiryHandler = new BussinessInquiryHandler(userName);
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
                } else if ("orderdetail".equals(target)) {
                    InquiryHandler orderDetailInquiryHandler = new OrderDetailInquiryHandler();
                    reply = orderDetailInquiryHandler.handle(reply, title);
                } else if ("collection".equals(target)) {
                    InquiryHandler collectedBussinessInquiryHandler = new CollectedBussinessesInquiryHandler();
                    reply = collectedBussinessInquiryHandler.handle(reply, title);
                } else if ("image".equals(target)) {
                    InquiryHandler imageURLInquiryHandler = new ImageURLInquiryHandler();
                    reply = imageURLInquiryHandler.handle(reply, title);
                }
            }
        } else if (IQ.Type.set.equals(packet.getType())) {
            reply = IQ.createResultIQ(packet);
            Element probeResponse = DocumentHelper.createElement(QName.get("admin",
                    "androidpn:admin:operation"));
            if (session.getStatus() == Session.STATUS_AUTHENTICATED) {
                // TODO
                Element query = packet.getChildElement();
                String target = query.elementText("target");
                String title = query.elementText("title");

                String userName = query.elementText("username");
                String content = query.elementText("content");

                String status = query.elementText("status");

                if ("comment".equals(target)) {
                    AdminHandler commentHandler = new CommentHandler(title, userName);
                    commentHandler.handle(title, content);
                } else if ("collection".equals(target)) {
                    AdminHandler adminHandler = new CollectionAdminHandler(status);
                    adminHandler.handle(title, content);
                } else if ("orderstatus".equals(target)) {
                    AdminHandler orderStatusHandler = new OrderStatusHandler();
                    ResultModel resultModel = orderStatusHandler.handle(title, content);

                    if (resultModel.getErrcode() == 1) {
                        probeResponse.addElement("errcode").setText(String.valueOf(resultModel.getErrcode()));
                        probeResponse.addElement("errmessage").setText(resultModel.getErrMessage());
                        reply.setChildElement(probeResponse);
                    } else {
                        InquiryHandler inquiryHandler = new TakeoutOrderInquiryHandler();
                        reply = inquiryHandler.handle(reply, "bussiness:"+userName);
                    }
                } else if ("sendmessage".equals(target)) {
                    content = getRandNum(6);
                    session.addAttributes("vcode", content);

                    AdminHandler sendMessageHandler = new SendMessageHandler();
                    ResultModel resultModel = sendMessageHandler.handle(title, content);

                    probeResponse.addElement("errcode").setText(String.valueOf(resultModel.getErrcode()));
                    probeResponse.addElement("errmessage").setText(resultModel.getErrMessage());
                    reply.setChildElement(probeResponse);
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

    public String getRandNum(int charCount) {
        String charValue = "";
        for (int i = 0; i < charCount; i++) {
            char c = (char) (randomInt(0, 10) + '0');
            charValue += String.valueOf(c);
        }
        return charValue;
    }
    public int randomInt(int from, int to) {
        Random r = new Random();
        return from + r.nextInt(to - from);
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }
}
