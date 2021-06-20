package me.cl.lingxi.module.member;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import me.cl.library.base.BaseActivity;
import me.cl.library.util.ToolbarUtil;
import me.cl.library.view.LoadingDialog;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.databinding.ResetpwdActivityBinding;
import me.cl.lingxi.entity.UserInfo;
import okhttp3.Call;

/**
 * 忘记密码重置
 */
public class ResetPwdActivity extends BaseActivity {

    private ResetpwdActivityBinding mActivityBinding;

    private Toolbar mToolbar;
    private EditText mUsername;
    private EditText mStu_id;
    private EditText mPassword;
    private EditText mDoPassword;
    private EditText mIdentityCode;
    private LoadingDialog updateProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = ResetpwdActivityBinding.inflate(getLayoutInflater());
        setContentView(mActivityBinding.getRoot());
        init();
    }

    private void init() {
        mToolbar = mActivityBinding.includeToolbar.toolbar;
        mUsername = mActivityBinding.username;
        mPassword = mActivityBinding.password;
        mDoPassword = mActivityBinding.doPassword;
        mStu_id = mActivityBinding.hintStuId2;
        mIdentityCode=mActivityBinding.hintIdentityCode2;
        ToolbarUtil.init(mToolbar, this)
                .setTitle(R.string.title_bar_reset_pwd)
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .build();

        updateProgress = new LoadingDialog(this, R.string.dialog_loading_reset_wd);
    }

    public void goUpdatePwd(View view) {
        String uName = mUsername.getText().toString().trim();
        String uPhone = mStu_id.getText().toString().trim();
        String uPwd = mPassword.getText().toString().trim();
        String uDoPwd = mDoPassword.getText().toString().trim();
        String ucode=mIdentityCode.getText().toString().trim();
        if (TextUtils.isEmpty(uName) || TextUtils.isEmpty(uPwd) || TextUtils.isEmpty(uDoPwd) || TextUtils.isEmpty(uPhone)) {
            showToast(R.string.toast_reg_null);
        }

        if (!uPwd.equals(uDoPwd)) {
            showToast(R.string.toast_again_error);
            return;
        }
        postUpdatePwd(uName, uPwd, ucode);
    }
    public void getIdentityCode(View view){

        String uName = mUsername.getText().toString().trim();
        String uSutId= mStu_id.getText().toString().trim();


        OkUtil.post().
                url(Api.userIdentityCode)
                .addParam("username",uName)
                .addParam("stuid",uSutId)
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
    public void postUpdatePwd(String userName, String userPwd, String code) {
        OkUtil.post()
                .url(Api.resetPassword)
                .addParam("username", userName)
                .addParam("newPass", userPwd)
                .addParam("code", code)
                .setProgressDialog(updateProgress)
                .execute(new ResultCallback<Result<String>>() {

                    @Override
                    public void onSuccess(Result<String> response) {
                        String code = response.getCode();
                        switch (code) {
                            case "00000":
                                showToast(R.string.toast_reset_ped_success);
                                onBackPressed();
                                break;
                            case "00104":
                                showToast(R.string.toast_reset_pwd_user);
                                break;
                            default:
//                                R.string.toast_reset_pwd_error
                                showToast(response.getMsg());
                                break;
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        showToast(R.string.toast_reset_pwd_error);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (updateProgress.isShowing()) {
            updateProgress.dismiss();
        }
        super.onDestroy();
    }
}
