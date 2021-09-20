package me.cl.lingxi.module.mine;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import me.cl.library.base.BaseActivity;
import me.cl.library.loadmore.LoadMoreAdapter;
import me.cl.library.loadmore.OnLoadMoreListener;
import me.cl.library.util.ToolbarUtil;
import me.cl.library.view.LoadingDialog;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.FeedAdapter;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.databinding.MyPostsBinding;
import me.cl.lingxi.entity.Feed;
import me.cl.lingxi.entity.PageInfo;
import okhttp3.Call;

public class PostFragmentActivity extends BaseActivity  {
    private MyPostsBinding mActivityBinding;

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private FeedAdapter mAdapter;
    private LoadingDialog loadingProgress;
    private String saveId;
    private final List<Feed> mPostsList = new ArrayList<>();
    private LoadMoreAdapter mLoadMoreAdapter = new LoadMoreAdapter();

    private int mPage = 1;
    private int mCount = 10;
    private final int MOD_REFRESH = 1;
    private final int MOD_LOADING = 2;
    private int RefreshMODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = MyPostsBinding.inflate(getLayoutInflater());
        setContentView(mActivityBinding.getRoot());
        init();
    }

    private void init() {
        mToolbar = mActivityBinding.includeToolbar.toolbar;
        mRecyclerView = mActivityBinding.recyclerView;
        mSwipeRefreshLayout=mActivityBinding.swipeRefreshLayout;
        boolean isMine = false;
        String title = "我的帖子";

        ToolbarUtil.init(mToolbar, this)
                .setTitle(title)
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .build();

        saveId = SPUtil.build().getString(Constants.SP_USER_ID);
        loadingProgress = new LoadingDialog(this, R.string.dialog_loading);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new FeedAdapter(mPostsList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemListener(new FeedAdapter.OnItemListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onItemClick(View view, Feed feed, int position) {
                switch (view.getId()) {
                    case R.id.user_img:
//                        goToUser(feed.getAvatar());
                        break;
                    case R.id.feed_card:
                    case R.id.feed_comment_layout:
                 //       gotoMood(feed);
                        break;
                    case R.id.feed_like_layout:
                        if (feed.isLike()) {
                           // postUnLike(feed, position);
                        }
                        // 未点赞点赞
                        else {
                       //     postAddLike(feed, position);
                        }
                        break;
                }
            }

            @Override
            public void onPhotoClick(ArrayList<String> photos, int position) {

            }
        });
        mRecyclerView.addOnScrollListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                if (mAdapter.getItemCount() < 4) return;

                RefreshMODE = MOD_LOADING;
                mLoadMoreAdapter.loading();

                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getMyPostList(mPage, mCount);
                    }
                },1000);
            }
        });
        getMyPostList(mPage,mCount);
    }


    // 获取动态列表
    //                .addParam("userId", uid)
    //                .addParam("pageNum", pageNum)
    //                .addParam("pageSize", pageSize)
    private void getMyPostList(int pageNum, int pageSize) {
        if (!mSwipeRefreshLayout.isRefreshing() && RefreshMODE == MOD_REFRESH) mSwipeRefreshLayout.setRefreshing(true);
        String uid = SPUtil.build().getString(Constants.SP_USER_ID);
        OkUtil.post()//+"?pageNum="+pageNum+"&pageSize="+pageSize
                .url(Api.myPostList)
                .addParam("username",saveId)
                .addParam("pageNum",pageNum)
                .addParam("pageSize",pageSize)
                .execute(new ResultCallback<Result<PageInfo<Feed>>>() {
                    @Override
                    public void onSuccess(Result<PageInfo<Feed>> response) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        String code = response.getCode();
                        if (!"00000".equals(code)) {
                            mLoadMoreAdapter.loadNone();
                            showToast(R.string.toast_get_feed_error);
                            return;
                        }
                        PageInfo<Feed> page = response.getData();
                        Integer size = page.getSize();
                        if (size == 0) {
                            mLoadMoreAdapter.loadNone();
                            return;
                        }
                        mPage++;
                        List<Feed> list = page.getList();
                        for (Feed post:list
                        ) {
                            post.setLike(post.getLikesList().contains(saveId));
                        }

                        if (RefreshMODE == MOD_LOADING) {
                            updateData(list);
                        } else {
                            setData(list);
                        }
                        mLoadMoreAdapter.loadEnd();
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mLoadMoreAdapter.loadNone();
                        showToast(R.string.toast_get_feed_error);
                    }
                });
    }
    // 设置数据
    private void setData(List<Feed> data){
        mAdapter.setData(data);
    }

    // 更新数据
    public void updateData(List<Feed> data) {
        mAdapter.addData(data);
    }

    // 刷新数据
    private void onRefresh(){
        RefreshMODE = MOD_REFRESH;
        mPage = 1;
        getMyPostList(mPage, mCount);
    }


    @Override
    protected void onDestroy() {
        OkUtil.newInstance().cancelAll();
        super.onDestroy();
    }
    /**
     * 前往用户页面
     */
//    private void goToUser(String useravatar){
//        Intent intent = new Intent(, UserActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(Constants.PASSED_USER_INFO, useravatar);
//        intent.putExtras(bundle);
//        startActivity(intent);
//    }
}
