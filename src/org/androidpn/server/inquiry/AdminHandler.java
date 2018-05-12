package org.androidpn.server.inquiry;

import org.androidpn.server.util.ResultModel;

public interface AdminHandler {

    public ResultModel handle(String title, String content);

}
