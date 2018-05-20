package org.androidpn.server.xmpp.handler;

import gnu.inet.encoding.StringprepException;
import org.androidpn.server.model.Comment;
import org.androidpn.server.service.BussinessService;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserExistsException;
import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.util.ResultModel;
import org.androidpn.server.util.TimeUtil;
import org.androidpn.server.xmpp.UnauthorizedException;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.Session;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;

public class IQCommentHandler extends IQHandler {

    private static final String NAMESPACE = "androidpn:comment:admin";

    private BussinessService bussinessService;

    private Element probeResponse;

    public IQCommentHandler() {
        bussinessService = ServiceLocator.getBussinessService();
        probeResponse = DocumentHelper.createElement(QName.get("admin",
                "androidpn:admin:operation"));
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

                    String userId = query.elementText("userid");
                    String bussinessId = query.elementText("bussinessid");
                    String fromUserName = query.elementText("fromusername");
                    String content = query.elementText("content");
                    String star = query.elementText("star");
                    String amount = query.elementText("amount");
                    String imageURL = query.elementText("imageurl");

                    Comment comment = new Comment();
                    comment.setCreateTime(TimeUtil.getCurrentDateStr());
                    comment.setUserName(fromUserName);
                    comment.setContent(content);
                    comment.setStar((int) Double.parseDouble(star));
                    comment.setAmount(Double.parseDouble(amount));
                    comment.setImageURL(imageURL);
                    comment.setUserId(Long.parseLong(userId));

                    ResultModel resultModel = bussinessService.addComment(bussinessId, comment);


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
