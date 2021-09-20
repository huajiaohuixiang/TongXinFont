package me.cl.lingxi.module.main;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import me.cl.library.base.BaseFragment;
import me.cl.library.util.ToolbarUtil;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.common.util.ContentUtil;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.databinding.MineFragmentBinding;
import me.cl.lingxi.dialog.LogoutDialog;
import me.cl.lingxi.entity.UserInfo;
import me.cl.lingxi.module.member.LoginActivity;
import me.cl.lingxi.module.mine.LikeFragmentActivity;
import me.cl.lingxi.module.mine.MyCommentActivity;
import me.cl.lingxi.module.mine.PersonalInfoActivity;
import me.cl.lingxi.module.mine.PostFragmentActivity;
import me.cl.lingxi.module.setting.AboutActivity;
import me.cl.lingxi.module.setting.SettingsActivity;
import okhttp3.Call;

/**
 * 我的界面
 */
public class MineFragment extends BaseFragment implements View.OnClickListener {

    private MineFragmentBinding mFragmentBinding;

    private Toolbar mToolbar;
    private ImageView mUserImg;
    private TextView mUserName;
    private TextView mMineRelevant;
    private  TextView mStuID;
    private String mUserId;
    private OperateBroadcastReceiver receiver;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentBinding = MineFragmentBinding.inflate(inflater, container, false);
        init();
        initReceiver();
        return mFragmentBinding.getRoot();
    }

    private void init() {
        mToolbar = mFragmentBinding.includeToolbar.toolbar;
        mUserImg = mFragmentBinding.userImg;
        mUserName = mFragmentBinding.userNickname;
        mMineRelevant = mFragmentBinding.actionRelevant;
        mStuID=mFragmentBinding.userStuId;
        ToolbarUtil.init(mToolbar, getActivity())
                .setTitle(R.string.nav_mine)
                .setTitleCenter()
                .build();

        mFragmentBinding.userBody.setOnClickListener(this);
        //我的收藏
        mFragmentBinding.actionReply.setOnClickListener(this);
        //我的评论
        mFragmentBinding.actionRelevant.setOnClickListener(this);
        mFragmentBinding.actionSetting.setOnClickListener(this);
        mFragmentBinding.actionAbout.setOnClickListener(this);
        mFragmentBinding.actionSignOut.setOnClickListener(this);
        //我的帖子
        mFragmentBinding.actionMypost.setOnClickListener(this);

        mUserId = SPUtil.build().getString(Constants.SP_USER_ID);
        // 获取用户信息
        postUserInfo(mUserId);
    }


    /**
     * 广播接收者
     * 用于更新用户信息
     */
    private final class OperateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if (Constants.UPDATE_USER_IMG.equals(action)) {
                    postUserInfo(mUserId);
                }
            }
        }
    }

    private void initReceiver() {
        receiver = new OperateBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.UPDATE_USER_IMG);
        requireActivity().registerReceiver(receiver, filter);
    }
// .addHeader("Authorization","Bearer "+Api.X_APP_TOKEN)
    private void postUserInfo(String id) {
        OkUtil.get()
                .url(Api.userInfo+"?username="+mUserId)
                .execute(new ResultCallback<Result<UserInfo>>() {

                    @Override
                    public void onSuccess(Result<UserInfo> response) {
                        initUser(response.getData());
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        initUser(null);
                    }
                });
    }

    private void initUser(UserInfo userInfo) {
        String usernickname = "默认用户";
        String avatar = "";
        if (userInfo != null) {
            usernickname = userInfo.getNickname();
            avatar = userInfo.getAvatar();
        }
        SPUtil.build().putString("savenickname", userInfo.getNickname());
        mStuID.setText(userInfo.getStu_id());
        mUserName.setText(usernickname);
        ContentUtil.loadUserAvatar(mUserImg, avatar);
    }

    @Override
    public void onDestroy() {
        requireActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        ContentUtil.setMoreBadge(mMineRelevant);
        if (Constants.isRead) {
            ((MainActivity) requireActivity()).goneBadge();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_body:
                gotoPersonal();
                break;
            case R.id.action_reply:
                //我的收藏
                gotoMyLike(Constants.REPLY_MY);
                break;
            case R.id.action_relevant:
                //我的评论
                gotoRelevant();
                break;
            case R.id.action_mypost:
                gotoMyPost(Constants.REPLY_My_Post);
                break;
            case R.id.action_setting:
                gotoSettings();
                break;
            case R.id.action_about:
                gotoAbout();
                break;
            case R.id.action_sign_out:
                showLogoutDialog();
                break;
        }
    }

    /**
     * 展示登出Dialog
     */
    private void showLogoutDialog() {
        String tag = "logout";
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        // 清除已经存在的，同样的fragment
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            transaction.remove(fragment);
        }
        transaction.addToBackStack(null);
        // 展示dialog
        LogoutDialog logoutDialog = LogoutDialog.newInstance();
        logoutDialog.show(transaction, tag);
        logoutDialog.setLogoutListener(new LogoutDialog.LogoutListener() {
            @Override
            public void onLogout() {
                gotoLogin();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        // 解决Activity has leaked window that was originally added here
        // 如果dialog存在或显示，dismiss
    }

    // 前往信息修改
    private void gotoPersonal() {
        Intent goPerson = new Intent(getActivity(), PersonalInfoActivity.class);
        startActivity(goPerson);
    }

    // 前往关于
    private void gotoAbout() {
        Intent goAbout = new Intent(getActivity(), AboutActivity.class);
        startActivity(goAbout);
    }

    private void gotoSettings() {
        Intent goSettings = new Intent(getActivity(), SettingsActivity.class);
        startActivity(goSettings);
    }

    // 前往与我相关
    private void gotoRelevant() {
        Intent goRelevant = new Intent(getActivity(), MyCommentActivity.class);

        startActivity(goRelevant);
    }
    // 前往我的收藏
    private void gotoMyLike(String type) {
        Intent goMyLike = new Intent(getActivity(), LikeFragmentActivity.class);
        goMyLike.putExtra(Constants.REPLY_TYPE, type);
        startActivity(goMyLike);
    }
    // 前往我的帖子
    private void gotoMyPost(String type) {
        Intent goMyPost = new Intent(getActivity(), PostFragmentActivity.class);
        goMyPost.putExtra(Constants.REPLY_TYPE, type);
        startActivity(goMyPost);
    }
    // 前往登录
    private void gotoLogin() {
        MainActivity activity = (MainActivity) getActivity();
        SPUtil.build().putBoolean(Constants.SP_BEEN_LOGIN, false);
        Intent intent = new Intent(activity, LoginActivity.class);
        startActivity(intent);
        if (activity != null) {
            activity.finish();
        }
    }

}
