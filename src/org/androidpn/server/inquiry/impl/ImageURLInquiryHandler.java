package org.androidpn.server.inquiry.impl;

import org.androidpn.server.inquiry.InquiryHandler;
import org.androidpn.server.model.User;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.service.UserService;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;

public class ImageURLInquiryHandler implements InquiryHandler {

    private static final String NAMESPACE = "androidpn:iq:inquiry";

    private UserService userService;

    private Element probeResponse;

    public ImageURLInquiryHandler() {
        userService = ServiceLocator.getUserService();

    }

    @Override
    public IQ handle(IQ reply, String title) {
        probeResponse = DocumentHelper.createElement(QName.get("image",
                NAMESPACE));

        String imageURL = null;
        User user = null;

        try {
            user = userService.getUserByUsername(title);
            imageURL = user.getImageURL();
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        if (imageURL != null) {
            probeResponse.addElement("imageurl").setText(imageURL);
            probeResponse.addElement("username").setText(user.getUsername());
        }

        reply.setChildElement(probeResponse);

        return reply;
    }
}
