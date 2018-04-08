package org.androidpn.server.inquiry.impl;

import org.androidpn.server.inquiry.InquiryHandler;
import org.androidpn.server.model.Bussiness;
import org.androidpn.server.model.Comment;
import org.androidpn.server.service.BussinessService;
import org.androidpn.server.service.ServiceLocator;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;

import java.util.List;

public class CommentInquiryHandler implements InquiryHandler {

    private static final String NAMESPACE = "androidpn:iq:comment";

    private BussinessService bussinessService;
    private Element probeResponse;

    public CommentInquiryHandler() {
        bussinessService = ServiceLocator.getBussinessService();
    }

    @Override
    public IQ handle(IQ reply, String title) {
        probeResponse = DocumentHelper.createElement(QName.get("comment",
                NAMESPACE));

        Bussiness bussiness = bussinessService.getBussiness(title);
        List<Comment> commentList = bussiness.getCommentList();

        for (Comment comment : commentList) {
            addItem(comment);
        }

        reply.setChildElement(probeResponse);

        return reply;
    }

    public void addItem(Comment comment) {
        Element item = DocumentHelper.createElement(QName.get("item",
                NAMESPACE));
        item.addAttribute("id", String.valueOf(comment.getCommentId()));
        item.addAttribute("username", comment.getUserName());
        item.addAttribute("content", comment.getContent());
        probeResponse.add(item);
    }
}
