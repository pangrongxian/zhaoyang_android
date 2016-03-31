package com.doctor.sun.http.callback;

import android.app.Activity;
import android.content.Intent;

import com.doctor.sun.dto.WechatPayDTO;
import com.doctor.sun.entity.AppointMent;
import com.doctor.sun.entity.EmergencyCall;
import com.doctor.sun.ui.activity.patient.PayFailActivity;
import com.doctor.sun.ui.activity.patient.PaySuccessActivity;
import com.doctor.sun.util.PayCallback;
import com.doctor.sun.wxapi.WXPayEntryActivity;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by rick on 23/1/2016.
 */
public class WeChatPayCallback extends SimpleCallback<WechatPayDTO> {
    private final PayCallback mCallback;
    private Activity activity;

    public WeChatPayCallback(final Activity activity, final AppointMent data) {
        this.activity = activity;
        mCallback = new PayCallback() {

            @Override
            public void onPaySuccess() {
                Intent intent = PaySuccessActivity.makeIntent(activity, data);
                activity.startActivity(intent);
            }

            @Override
            public void onPayFail() {
                Intent intent = PayFailActivity.makeIntent(activity, data, true);
                activity.startActivity(intent);
            }
        };
        WXPayEntryActivity.setCallback(mCallback);
    }

    public WeChatPayCallback(final Activity activity, final EmergencyCall data) {
        this.activity = activity;
        mCallback = new PayCallback() {

            @Override
            public void onPaySuccess() {
                Intent intent = PaySuccessActivity.makeIntent(activity, data);
                activity.startActivity(intent);
            }

            @Override
            public void onPayFail() {
                Intent intent = PayFailActivity.makeIntent(activity, data, true);
                activity.startActivity(intent);
            }
        };
        WXPayEntryActivity.setCallback(mCallback);
    }

    public WeChatPayCallback(final Activity activity, final int money){
        this.activity = activity;
        mCallback = new PayCallback() {
            @Override
            public void onPaySuccess() {
                Intent intent = PaySuccessActivity.makeIntent(activity);
                activity.startActivity(intent);
            }

            @Override
            public void onPayFail() {
                Intent intent = PayFailActivity.makeIntent(activity,money,"wechat");
                activity.startActivity(intent);
            }
        };
        WXPayEntryActivity.setCallback(mCallback);
    }

    @Override
    protected void handleResponse(WechatPayDTO response) {
        PayReq req = new PayReq();

        req.appId = response.getAppid();
        req.partnerId = response.getPartnerid();
        req.prepayId = response.getPrepayid();
        req.packageValue = response.getPackageX();
        req.nonceStr = response.getNoncestr();
        req.timeStamp = response.getTimestamp();
        req.sign = response.getSign();

        IWXAPI msgApi = WXAPIFactory.createWXAPI(activity, response.getAppid());
        msgApi.registerApp(response.getAppid());
        msgApi.sendReq(req);
    }
}
