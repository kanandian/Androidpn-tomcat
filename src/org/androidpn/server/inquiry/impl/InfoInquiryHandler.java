package org.androidpn.server.inquiry.impl;

import org.androidpn.server.inquiry.InquiryHandler;
import org.androidpn.server.model.User;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserService;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;

public class InfoInquiryHandler implements InquiryHandler {

    private static final String NAMESPACE = "androidpn:iq:inquiry";

    private UserService userService;
    private Element probeResponse;

    public InfoInquiryHandler() {
        userService = ServiceLocator.getUserService();
    }


    @Override
    public IQ handle(IQ reply, String title) {
        probeResponse = DocumentHelper.createElement(QName.get("info",
                NAMESPACE));

        User user = userService.getUser(title);

        createResponse(user);

        reply.setChildElement(probeResponse);

        return reply;
    }

    public void createResponse(User user) {
        //创建元素
        Element id = DocumentHelper.createElement("id");
        Element idcard = DocumentHelper.createElement("idcard");
        Element username = DocumentHelper.createElement("username");
        Element name = DocumentHelper.createElement("name");
        Element sex = DocumentHelper.createElement("sex");
        Element mobile = DocumentHelper.createElement("mobile");
        Element email = DocumentHelper.createElement("email");

        //设置内容
        id.setText(String.valueOf(user.getId()));
        idcard.setText(user.getIdcard());
        username.setText(user.getUsername());
        name.setText(user.getName());
        sex.setText(user.getSex());
        mobile.setText(user.getMobile());
        email.setText(user.getEmail());

        //添加到probeResponse中
        probeResponse.add(id);
        probeResponse.add(idcard);
        probeResponse.add(username);
        probeResponse.add(name);
        probeResponse.add(sex);
        probeResponse.add(mobile);
        probeResponse.add(email);
    }

}
