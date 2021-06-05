package me.cl.lingxi.module.member;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.cl.library.base.BaseActivity;
import me.cl.library.util.ToolbarUtil;
import me.cl.library.view.LoadingDialog;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.databinding.RegisteActivityBinding;
import me.cl.lingxi.entity.UserInfo;
import okhttp3.Call;

/**
 * 用户注册
 */
public class RegisterActivity extends BaseActivity {

    private RegisteActivityBinding mActivityBinding;

    private Toolbar mToolbar;
    private EditText mUsername;
    private EditText nickname;
    private EditText mPassword;
    private EditText mDoPassword;
    private EditText mStuId;
    private EditText identityCode;
    private LoadingDialog registerProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = RegisteActivityBinding.inflate(getLayoutInflater());
        setContentView(mActivityBinding.getRoot());
        init();
    }

    private void init() {
        mToolbar = mActivityBinding.includeToolbar.toolbar;
        mUsername = mActivityBinding.username;
        mPassword = mActivityBinding.password;
        mDoPassword = mActivityBinding.doPassword;
        mStuId = mActivityBinding.hintStuId;
        identityCode=mActivityBinding.hintIdentityCode;
        nickname =mActivityBinding.nickname;
        ToolbarUtil.init(mToolbar, this)
                .setTitle(R.string.title_bar_reg)
                .setBack()
                .setTitleCenter()
                .build();

        registerProgress = new LoadingDialog(this, R.string.dialog_loading_reg);
    }
    public void getIdentityCode(View view){

        String uName = mUsername.getText().toString().trim();
        String uSutId= mStuId.getText().toString().trim();


        OkUtil.post().
                url(Api.userIdentityCode)
                .addParam("username",uName)
                .addParam("stuid",uSutId)
                .setProgressDialog(registerProgress)

                .execute(new ResultCallback<Result<String>>() {

            @Override
            public void onSuccess(Result<String> response) {
                String code = response.getCode();
                switch (code) {
                    case "00000":
                        showToast(R.string.codeinfo);
                        break;
                    case "00001":
                        showToast(response.getData());
                        break;
                }
            }

            @Override
            public void onError(Call call, Exception e) {
                showToast("发送验证码失败！");
            }
        });
    }
    public void goRegister(View view) {
        String uName = mUsername.getText().toString().trim();
        String uPwd = mPassword.getText().toString().trim();
        String uDoPwd = mDoPassword.getText().toString().trim();
        String uStuId = mStuId.getText().toString().trim();
        String uNickname= nickname.getText().toString().trim();
        String code=identityCode.getText().toString().trim();
        if (TextUtils.isEmpty(uName) || TextUtils.isEmpty(uPwd) || TextUtils.isEmpty(uDoPwd) || TextUtils.isEmpty(uStuId)||TextUtils.isEmpty(uNickname)||TextUtils.isEmpty(code)) {
            showToast(R.string.toast_reg_null);
            return;
        }
        if (!uPwd.equals(uDoPwd)) {
            showToast(R.string.toast_again_error);
            return;
        }
        if (uStuId.length() != 7) {
            showToast(R.string.toast_phone_format_error);
            return;
        }

        postRegister(uName, uPwd,uNickname, uStuId,code);
    }

    /**
     * 验证手机
     */
    public static boolean isMobileNum(String mobiles) {
        Pattern p = Pattern.compile("1[3-9]\\d{9}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 注册请求
     */
    public void postRegister(String userName, String userPwd, String nickname,String stuid,String code) {
        OkUtil.post()
                .url(Api.userRegister)
                .addParam("username", userName)
                .addParam("password", userPwd)
                .addParam("nickname", nickname)
                .addParam("stuid",stuid)
                .addParam("code",code)
                .setProgressDialog(registerProgress)
                .execute(new ResultCallback<Result<UserInfo>>() {

                    @Override
                    public void onSuccess(Result<UserInfo> response) {
                        String code = response.getCode();
                        switch (code) {
                            case "00000":
                                showToast(R.string.toast_reg_success);
                                UserInfo user = response.getData();
                                SPUtil.build().putString(Constants.SP_USER_NAME, user.getUsername());
                                onBackPressed();
                                break;
                            case "00105":
                                showToast("验证码错误");
                                break;
                            case "00106":
                                showToast(R.string.toast_username_being);
                                break;
                            default:
                                showToast(R.string.toast_reg_error);
                                break;
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        System.out.println("22222");
                        showToast(R.string.toast_reg_error);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (registerProgress.isShowing()) {
            registerProgress.dismiss();
        }
        super.onDestroy();
    }
}
