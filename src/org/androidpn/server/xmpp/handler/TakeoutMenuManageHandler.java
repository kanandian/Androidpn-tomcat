package org.androidpn.server.xmpp.handler;

import com.sun.org.apache.xalan.internal.xsltc.dom.SimpleResultTreeImpl;
import gnu.inet.encoding.StringprepException;
import org.androidpn.server.inquiry.InquiryHandler;
import org.androidpn.server.inquiry.impl.FoodMenuInquiryHandler;
import org.androidpn.server.model.FoodMenu;
import org.androidpn.server.service.FoodMenuService;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserExistsException;
import org.androidpn.server.service.UserNotFoundException;
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
import sun.util.resources.ga.LocaleNames_ga;

public class TakeoutMenuManageHandler extends IQHandler {

    private static final String NAMESPACE = "androidpn:manage:takeoutitem";

    private Element probeResponse;
    private FoodMenuService foodMenuService;


    public TakeoutMenuManageHandler() {
        foodMenuService = ServiceLocator.getFoodMenuService();
        probeResponse = DocumentHelper.createElement(QName.get("manage",
                NAMESPACE));
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

                    String target = query.elementText("target");
                    String bussinessId = query.elementText("bussinessid");
                    if ("delete".equals(target)) {
                        String itemid = query.elementText("itemid");
                        foodMenuService.deleteFoodMenuByMenuId(itemid);
                    } else if ("add".equals(target)) {
                        String foodName = query.elementText("foodname");
                        String price = query.elementText("price");

                        FoodMenu foodMenu = new FoodMenu();
                        foodMenu.setBussinessId(Long.parseLong(bussinessId));
                        foodMenu.setFoodName(foodName);
                        foodMenu.setPrice(Double.parseDouble(price));

                        foodMenuService.addFoodMenu(foodMenu);
                    }


                    InquiryHandler foodMenuHandler = new FoodMenuInquiryHandler();
                    reply = foodMenuHandler.handle(reply, bussinessId);

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
