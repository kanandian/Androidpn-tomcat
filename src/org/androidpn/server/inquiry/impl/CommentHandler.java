package org.androidpn.server.inquiry.impl;

import org.androidpn.server.inquiry.AdminHandler;
import org.androidpn.server.model.Comment;
import org.androidpn.server.service.BussinessService;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.util.TimeUtil;

public class CommentHandler implements AdminHandler {

    private String bussinessId;
    private String fromUserName;

    private BussinessService bussinessService;

    public CommentHandler(String bussinessId, String fromUserName) {
        this.bussinessId = bussinessId;
        this.fromUserName = fromUserName;

        bussinessService = ServiceLocator.getBussinessService();
    }

    @Override
    public void handle(String title, String content) {
        Comment comment = new Comment();
        comment.setUserName(fromUserName);
        comment.setContent(content);
        comment.setCreateTime(TimeUtil.getCurrentDateStr());

        bussinessService.addComment(bussinessId, comment);
    }
}
