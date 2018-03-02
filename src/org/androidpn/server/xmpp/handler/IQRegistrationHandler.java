package org.androidpn.server.xmpp.handler;

import gnu.inet.encoding.Stringprep;
import gnu.inet.encoding.StringprepException;
import org.androidpn.server.model.User;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserExistsException;
import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.service.UserService;
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
        probeResponse = DocumentHelper.createElement(QName.get("registeration",
                NAMESPACE));
        probeResponse.addElement("username");
        probeResponse.addElement("password");
        probeResponse.addElement("email");
        probeResponse.addElement("name");
        probeResponse.addElement("mobile");
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
//                    String username = query.elementText("username");
//                    String password = query.elementText("password");
//                    String email = query.elementText("email");
//                    String name = query.elementText("name");

//                    String localName = query.elementText("localName");
//                    String localPassword = query.elementText("localPassword");

                    // Deny registration of users with no password
                    if (password == null || password.trim().length() == 0) {
                        reply = IQ.createResultIQ(packet);
                        reply.setChildElement(packet.getChildElement()
                                .createCopy());
                        reply.setError(PacketError.Condition.not_acceptable);
                        System.out.println("qzf password is no use");
                        return reply;
                    }

                    if (email != null && email.matches("\\s*")) {
                        email = null;
                    }

                    if (name != null && name.matches("\\s*")) {
                        name = null;
                    }

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
                    userService.saveUser(user);

                    JID from = session.getAddress();
                    JID newFrom = new JID(userName, from.getDomain(), from.getResource());
                    changeUserInfo(session, newFrom, password);

                    reply = createResultIQ(packet, newFrom, user);

//                    reply = IQ.createResultIQ(packet);
//
//                    Element resp = DocumentHelper.createElement(QName.get("registeration",
//                            NAMESPACE));
//
//                    reply.setChildElement(resp);
//                    reply.setTo(newFrom);


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
        IQ reply = IQ.createResultIQ(packet);;

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
