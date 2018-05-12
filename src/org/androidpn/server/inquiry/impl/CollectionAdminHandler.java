package org.androidpn.server.inquiry.impl;

import org.androidpn.server.inquiry.AdminHandler;
import org.androidpn.server.model.User;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserService;
import org.androidpn.server.util.ResultModel;

public class CollectionAdminHandler implements AdminHandler {

    private UserService userService;
    private int status;

    public CollectionAdminHandler(String status) {
        this.status = Integer.parseInt(status);
        userService = ServiceLocator.getUserService();
    }

    @Override
    public ResultModel handle(String title, String content) {
        ResultModel resultModel = new ResultModel();
        if (status == 1) {
            userService.addCollection(title, content);
        } else if (status == 0){
            userService.removeCollection(title, content);
        }
        return resultModel;
    }
}
