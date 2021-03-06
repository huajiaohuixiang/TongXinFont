package me.cl.lingxi.module.setting;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import me.cl.library.base.BaseActivity;
import me.cl.library.util.ToolbarUtil;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.common.result.ResultConstant;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.databinding.AboutActivityBinding;
import me.cl.lingxi.module.webview.WebActivity;
import okhttp3.Call;

public class AboutActivity extends BaseActivity implements View.OnClickListener {

    private AboutActivityBinding mActivityBinding;

    private Toolbar mToolbar;
    private TextView mVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = AboutActivityBinding.inflate(getLayoutInflater());
        setContentView(mActivityBinding.getRoot());
        init();
    }

    private void init() {
        mToolbar = mActivityBinding.includeToolbar.toolbar;
        mVersion = mActivityBinding.version;

        mActivityBinding.appUpdate.setOnClickListener(this);
        mActivityBinding.feedback.setOnClickListener(this);
        mActivityBinding.publicLicense.setOnClickListener(this);
        mActivityBinding.learnMore.setOnClickListener(this);

        ToolbarUtil.init(mToolbar, this)
                .setTitle(R.string.title_bar_about)
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .build();




        String versionName = "V " + Utils.getAppVersionName(this);
        mVersion.setText(versionName);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.app_update:
                getAppVersion();
                break;
            case R.id.feedback:
                boolean isWpa = Utils.wpaQQ(this, "1751415583");
                if (!isWpa) {
                    showToast("????????????Q???????????????????????????");
                }
                break;
            case R.id.public_license:
                gotoPublicLicense();
                break;
            case R.id.learn_more:
                gotoWeb("????????????", "file:///android_asset/about.html");
                break;
        }
    }

    // ??????????????????
    public void getAppVersion() {
        OkUtil.get()
                .url(Api.latestVersion)
                .execute(new ResultCallback<Result<String>>() {
                    @Override
                    public void onSuccess(Result<String> response) {
                        String code = response.getCode();
                        String data=response.getData();
                        if (ResultConstant.CODE_SUCCESS.equals(code) && data != null) {
                            int versionCode = Utils.getAppVersionCode(AboutActivity.this);
                            if (versionCode >= Integer.valueOf(data)) {
                                showToast( "???????????????");
                            } else {
                                showUpdate();
                            }
                        } else {
                            showToast("????????????????????????");
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        showToast("????????????????????????");
                    }
                });

    }

    private void gotoPublicLicense() {
        Intent intent = new Intent(this, PublicLicenseActivity.class);
        startActivity(intent);
    }

    // ??????????????????
    private void showUpdate() {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        mDialog.setTitle("???????????????");

        mDialog.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gotoDownload(Api.latestApp);
            }
        }).setCancelable(false).create().show();
    }

    // ?????????????????????
    private void gotoDownload(String url){
        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        intent.setAction(Intent.ACTION_VIEW);
        startActivity(intent);
    }

    // ??????web???
    private void gotoWeb(String tittle, String url) {
        Intent intent = new Intent(this, WebActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("tittle",tittle);
        bundle.putString("url", url);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
