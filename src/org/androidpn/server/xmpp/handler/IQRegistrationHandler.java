package org.androidpn.server.xmpp.handler;

import gnu.inet.encoding.Stringprep;
import gnu.inet.encoding.StringprepException;
import org.androidpn.server.model.User;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserExistsException;
import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.service.UserService;
import org.androidpn.server.util.ResultModel;
import org.androidpn.server.xmpp.UnauthenticatedException;
import org.androidpn.server.xmpp.UnauthorizedException;
import org.androidpn.server.xmpp.auth.AuthManager;
import org.androidpn.server.xmpp.auth.AuthToken;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.Session;
import org.androidpn.server.xmpp.session.SessionManager;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;

public class IQRegistrationHandler extends IQHandler {
    private static final String NAMESPACE = "androidpn:iq:registeration";

    private UserService userService;

    private Element probeResponse;

    /**
     * Constructor.
     */
    public IQRegistrationHandler() {
        userService = ServiceLocator.getUserService();
//        probeResponse.addElement("username");
//        probeResponse.addElement("password");
//        probeResponse.addElement("email");
//        probeResponse.addElement("name");
//        probeResponse.addElement("mobile");
    }

    /**
     * Handles the received IQ packet.
     *
     * @param packet the packet
     * @return the response to send back
     * @throws UnauthorizedException if the user is not authorized
     */
    public IQ handleIQ(IQ packet) throws UnauthorizedException {
        IQ reply = null;
        probeResponse = DocumentHelper.createElement(QName.get("admin",
                "androidpn:admin:operation"));

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
                    String userName = query.elementText("userName");
                    String password = query.elementText("password");
                    String localName = query.elementText("localName");
                    String localPassword = query.elementText("localPassword");
                    String name = query.elementText("name");
                    String email = query.elementText("email");
                    String mobile = query.elementText("mobile");
                    String vcode = query.elementText("vcode");
                    String userType = query.elementText("usertype");
//                    String username = query.elementText("username");
//                    String password = query.elementText("password");
//                    String email = query.elementText("email");
//                    String name = query.elementText("name");

//                    String localName = query.elementText("localName");
//                    String localPassword = query.elementText("localPassword");

                    ResultModel resultModel = new ResultModel();


                    if (userService.existUser(userName)) {
                        probeResponse.addElement("errcode").setText("1");
                        probeResponse.addElement("errmessage").setText("用户名已存在");
                        reply = IQ.createResultIQ(packet);
                        reply.setChildElement(probeResponse);
                    } else if (userService.existMobile(mobile)) {
                        probeResponse.addElement("errcode").setText("1");
                        probeResponse.addElement("errmessage").setText("该电话号码已注册过");
                        reply = IQ.createResultIQ(packet);
                        reply.setChildElement(probeResponse);
                    } else {

                        String currentVCode = (String) session.getAttributes("vcode");

                        if (currentVCode == null) {
                            resultModel.setErrcode(1);
                            resultModel.setErrMessage("请先获取验证码");
                        } else if (currentVCode.equals(vcode)){
                            User user;
                            if (session.getStatus() == Session.STATUS_AUTHENTICATED) {
                                user = userService.getUserByUsername(session.getUsername());
                                if (user.getRealUser()) {
                                    user = new User();
                                }
                            } else {
                                throw new UserNotFoundException("User '" + session.getUsername() + "' not found");
                            }
                            user.setUsername(userName);
                            user.setPassword(password);
                            user.setName(name);
                            user.setEmail(email);
                            user.setMobile(mobile);
                            user.setRealUser(true);
                            user.setImageURL("http://localhost:8080/bussinessimage/userdefault.jpg");
                            user.setUserType(Integer.parseInt(userType));
                            userService.saveUser(user);

                            JID from = session.getAddress();
                            JID newFrom = new JID(userName, from.getDomain(), from.getResource());
                            changeUserInfo(session, newFrom, password);

//                    reply = IQ.createResultIQ(packet);
//
//                    Element resp = DocumentHelper.createElement(QName.get("registeration",
//                            NAMESPACE));
//
//                    reply.setChildElement(resp);
//                    reply.setTo(newFrom);
                        } else {
                            resultModel.setErrcode(1);
                            resultModel.setErrMessage("验证码不正确");
                        }

                        reply = IQ.createResultIQ(packet);

                        probeResponse.addElement("errcode").setText(String.valueOf(resultModel.getErrcode()));
                        probeResponse.addElement("errmessage").setText(resultModel.getErrMessage());
                        probeResponse.addElement("action").setText("registration");

                        reply.setChildElement(probeResponse);
                    }


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

    private IQ createResultIQ(IQ packet, JID newFrom, User user) {
        IQ reply = IQ.createResultIQ(packet);

        Element resp = DocumentHelper.createElement(QName.get("registeration",
                NAMESPACE));

        resp.addElement("userName").setText(user.getUsername());
        resp.addElement("password").setText(user.getPassword());
        resp.addElement("name").setText(user.getName());
        resp.addElement("mobile").setText(user.getMobile());

        reply.setChildElement(resp);
        reply.setTo(newFrom);

        return reply;
    }

    private void changeUserInfo(ClientSession session, JID newFrom, String password) throws UnauthenticatedException {

        JID from = session.getAddress();

        SessionManager.getInstance().changeUserName(from.toString(), newFrom.toString());

        AuthToken authToken = AuthManager.authenticate(newFrom.getNode(), password);
        session.setAuthToken(authToken);
        session.setAddress(newFrom);
    }

    /**
     * Returns the namespace of the handler.
     *
     * @return the namespace
     */
    public String getNamespace() {
        return NAMESPACE;
    }
}
