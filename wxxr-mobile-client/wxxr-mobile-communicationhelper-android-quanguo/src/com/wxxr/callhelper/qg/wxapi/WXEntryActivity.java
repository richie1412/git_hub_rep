package com.wxxr.callhelper.qg.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.utils.WXManager;
import com.wxxr.mobile.core.log.api.Trace;

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {
    private WXManager wx ;
    private IWXAPI api;
    private static final Trace log = Trace.register(WXEntryActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        wx = new WXManager(WXEntryActivity.this);
        
        //注册微信
        api = wx.regToWx();

    }

    protected void onStart() {
        super.onStart();
        api.handleIntent(getIntent(), this);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String pageUrl = intent.getStringExtra("pageUrl");
        String imgUrl = intent.getStringExtra("imgUrl");
        wx.sendReqToWx(title, description, pageUrl, imgUrl);
        finish();
    }

    @Override
    public void onReq(BaseReq req) {
        if (log.isDebugEnabled()) {
            log.debug("wx onReq :" + req.toString());
        }
        Toast.makeText(this, "wx onReq :", 300).show();
    }

    //	@Override
    //    public void onReq(BaseReq req) {
    //        switch (req.getType()) {
    //        case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
    //            goToGetMsg();       
    //            break;
    //        case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
    //            goToShowMsg((ShowMessageFromWX.Req) req);
    //            break;
    //        default:
    //            break;
    //        }
    //    }
    @Override
    public void onResp(BaseResp resp) {
        if (log.isDebugEnabled()) {
            log.debug("wx onResp :" + resp.toString());
        }
        int result = 0;

        switch (resp.errCode) {
        case BaseResp.ErrCode.ERR_OK:
            result = R.string.errcode_success;
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            break;
        case BaseResp.ErrCode.ERR_USER_CANCEL:
            result = R.string.errcode_cancel;
            break;
        case BaseResp.ErrCode.ERR_AUTH_DENIED:
            result = R.string.errcode_deny;
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            break;
        default:
            result = R.string.errcode_unknown;
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            break;
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
