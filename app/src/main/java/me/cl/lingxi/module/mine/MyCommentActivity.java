package me.cl.lingxi.module.mine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.cl.library.base.BaseActivity;
import me.cl.library.util.ToolbarUtil;
import me.cl.library.view.LoadingDialog;
import me.cl.library.view.MoeToast;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.CommentAdapter;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.databinding.MyCommentBinding;
import me.cl.lingxi.entity.CommentPost;
import me.cl.lingxi.entity.Feed;
import me.cl.lingxi.entity.PageInfo;
import me.cl.lingxi.module.feed.FeedActivity;
import okhttp3.Call;

public class MyCommentActivity extends BaseActivity {

    private MyCommentBinding mActivityBinding;

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;

    private CommentAdapter mAdapter;
    private LoadingDialog loadingProgress;
    private String saveId;
    private final List<CommentPost> mRelevantList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = MyCommentBinding.inflate(getLayoutInflater());
        setContentView(mActivityBinding.getRoot());
        init();
    }

    private void init() {
        mToolbar = mActivityBinding.includeToolbar.toolbar;
        mRecyclerView = mActivityBinding.recyclerView;

        Intent intent = getIntent();
        boolean isMine = false;
        String title = "我的评论";


        ToolbarUtil.init(mToolbar, this)
                .setTitle(title)
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .build();

        saveId = SPUtil.build().getString(Constants.SP_USER_ID);
        loadingProgress = new LoadingDialog(this, R.string.dialog_loading);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new CommentAdapter(mRelevantList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemListener(new CommentAdapter.OnItemListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onItemClick(View view, CommentPost commentPost) {
                switch (view.getId()) {
                    case R.id.user_img:
                        break;
                    case R.id.feed_body:
                        gotoFeed(commentPost.getPost());
                        break;
                }
            }
        });

        getMyCommentList();
        int x = (int) (Math.random() * 4) + 1;
        if (x == 1) {
            MoeToast.makeText(this, R.string.egg_can_you_find);
        }
    }

    /**
     * 更新未读条数
     */
    public void updateUnread() {
        String userId = SPUtil.build().getString(Constants.SP_USER_ID);
        OkUtil.post()
                .url(Api.updateUnread)
                .addParam("userId", userId)
                .execute(new ResultCallback<Result<Integer>>() {
                    @Override
                    public void onSuccess(Result<Integer> response) {
                        Constants.isRead = true;
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });
    }

    // 请求我的评论
    public void getMyCommentList() {
        Integer pageNum = 1;
        Integer pageSize = 20;
        OkUtil.post()
                .url(Api.getMyComment)
                .addParam("username", saveId)
                .addParam("pageNum", pageNum)
                .addParam("pageSize", pageSize)
                .setLoadDelay()
                .setProgressDialog(loadingProgress)
                .execute(new ResultCallback<Result<PageInfo<CommentPost>>>() {
                    @Override
                    public void onSuccess(Result<PageInfo<CommentPost>> response) {
                        String code = response.getCode();
                        if (!"00000".equals(code)) {
                            showToast("加载失败，下拉重新加载");
                            return;
                        }
                        updateData(response.getData().getList());
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        showToast("加载失败，下拉重新加载");
                    }
                });
    }

    private void updateData(List<CommentPost> relevantList) {
        mAdapter.updateData(relevantList);
    }

    //前往详情页
    private void gotoFeed(Feed feed) {
        Intent intent = new Intent(MyCommentActivity.this, FeedActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("feed", feed);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        OkUtil.newInstance().cancelAll();
        super.onDestroy();
    }
}
