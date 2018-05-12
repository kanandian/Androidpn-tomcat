package org.androidpn.server.inquiry.impl;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import org.androidpn.server.inquiry.AdminHandler;
import org.androidpn.server.util.ResultModel;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SendMessageHandler implements AdminHandler {

    private static final int appid = 1400091675;
    private static final String appkey = "17410b9aa087b555f9b90f301475267a";


    @Override
    public ResultModel handle(String title, String content) {
        SmsSingleSenderResult result = null;
        ArrayList<String> params = new ArrayList<String>();
        params.add(content);

        try {
            SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
             result = ssender.sendWithParam("86", title, 120565, params, "", "", "");
            System.out.print(result);
        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
        } catch (IOException e) {
            // 网络IO错误
            e.printStackTrace();
        }
        ResultModel resultModel = new ResultModel();
        if (result == null || result.result != 0) {
            resultModel.setErrcode(0);
            resultModel.setErrMessage("短信发送异常");
        }
        return resultModel;
    }
}
