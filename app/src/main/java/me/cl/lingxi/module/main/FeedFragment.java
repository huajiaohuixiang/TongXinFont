package me.cl.lingxi.module.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import me.cl.library.base.BaseFragment;
import me.cl.library.loadmore.LoadMoreAdapter;
import me.cl.library.loadmore.OnLoadMoreListener;
import me.cl.library.photo.PhotoBrowser;
import me.cl.library.recycle.ItemAnimator;
import me.cl.library.recycle.ItemDecoration;
import me.cl.library.util.ToolbarUtil;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.FeedAdapter;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.databinding.FeedFragmentBinding;
import me.cl.lingxi.entity.Feed;
import me.cl.lingxi.entity.Like;
import me.cl.lingxi.entity.PageInfo;
import me.cl.lingxi.entity.User;
import me.cl.lingxi.module.feed.FeedActivity;
import me.cl.lingxi.module.feed.PublishActivity;
import me.cl.lingxi.module.member.UserActivity;
import okhttp3.Call;

/**
 * 圈子动态
 */
public class FeedFragment extends BaseFragment {

    private static final String FEED_TYPE = "feed_type";

    private FeedFragmentBinding mFragmentBinding;

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private String saveUid;
    private String saveUName;

    private List<Feed> mList = new ArrayList<>();
    private ConcatAdapter mConcatAdapter;
    private FeedAdapter mFeedAdapter;
    private LoadMoreAdapter mLoadMoreAdapter = new LoadMoreAdapter();

    private int mPage = 1;
    private int mCount = 10;
    private final int MOD_REFRESH = 1;
    private final int MOD_LOADING = 2;
    private int RefreshMODE = 0;

    public FeedFragment() {

    }

    public static FeedFragment newInstance(String feedType) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putString(FEED_TYPE, feedType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mType = getArguments().getString(FEED_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentBinding = FeedFragmentBinding.inflate(inflater, container, false);
        init();
        return mFragmentBinding.getRoot();
    }

    private void init() {
        mToolbar = mFragmentBinding.includeToolbar.toolbar;
        mRecyclerView = mFragmentBinding.recyclerView;
        mSwipeRefreshLayout = mFragmentBinding.swipeRefreshLayout;

        ToolbarUtil.init(mToolbar, getActivity())
                .setTitle(R.string.nav_camera)
                .setTitleCenter()
                .setMenu(R.menu.publish_menu, new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.action_share) {
                            gotoPublish();
                        }
                        return false;
                    }
                })
                .build();

        saveUid = SPUtil.build().getString(Constants.SP_USER_ID);
        saveUName = SPUtil.build().getString(Constants.SP_USER_NAME);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new ItemAnimator());
        ItemDecoration itemDecoration = new ItemDecoration(ItemDecoration.VERTICAL, 10, Color.parseColor("#f2f2f2"));
        // 隐藏最后一个item的分割线
        itemDecoration.setGoneLast(true);
        // mRecyclerView.addItemDecoration(itemDecoration);
        mFeedAdapter = new FeedAdapter(mList);
        mConcatAdapter = new ConcatAdapter(mFeedAdapter, mLoadMoreAdapter);
        mRecyclerView.setAdapter(mConcatAdapter);

        initEvent();

        getFeedList(mPage, mCount);
    }

    //初始化事件
    private void initEvent() {
        //刷新
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshMODE = MOD_REFRESH;
                mPage = 1;
                getFeedList(mPage, mCount);
            }
        });

        //item点击
        mFeedAdapter.setOnItemListener(new FeedAdapter.OnItemListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onItemClick(View view, Feed feed, int position) {
                switch (view.getId()) {
                    case R.id.user_img:
                        goToUser(feed.getUser());
                        break;
                    case R.id.feed_card:
                    case R.id.feed_comment_layout:
                        gotoMood(feed);
                        break;
                    case R.id.feed_like_layout:
                        if (feed.isLike()) return;
                        // 未点赞点赞
                        postAddLike(feed, position);
                        break;
                }
            }

            @Override
            public void onPhotoClick(ArrayList<String> photos, int position) {
                PhotoBrowser.builder()
                        .setPhotos(photos)
                        .setCurrentItem(position)
                        .start(requireActivity());
            }
        });

        //滑动监听
        mRecyclerView.addOnScrollListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                if (mFeedAdapter.getItemCount() < 4) return;

                RefreshMODE = MOD_LOADING;
                mLoadMoreAdapter.loading();

                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getFeedList(mPage, mCount);
                    }
                },1000);
            }
        });
    }

    // 前往动态发布
    private void gotoPublish() {
        Intent intent = new Intent(getActivity(), PublishActivity.class);
        startActivityForResult(intent, Constants.ACTIVITY_PUBLISH);
    }

    // 前往动态详情
    private void gotoMood(Feed feed) {
        Intent intent = new Intent(getActivity(), FeedActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("feed", feed);
        intent.putExtras(bundle);
        startActivityForResult(intent, Constants.ACTIVITY_MOOD);
    }

    // 点赞
    private void postAddLike(final Feed feed, final int position) {
        OkUtil.post()
                .url(Api.saveAction)
                .addParam("feedId", feed.getId())
                .addParam("userId", saveUid)
                .addParam("type", "0")
                .execute(new ResultCallback<Result>() {
                    @Override
                    public void onSuccess(Result response) {
                        String code = response.getCode();
                        if (!"00000".equals(code)) {
                            showToast("点赞失败");
                            return;
                        }
                        List<Like> likeList = new ArrayList<>(feed.getLikeList());
                        Like like = new Like();
                        like.setUserId(saveUid);
                        like.setUsername(saveUName);
                        likeList.add(like);
                        feed.setLikeList(likeList);
                        feed.setLike(true);
                        mFeedAdapter.updateItem(feed, position);
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        showToast("点赞失败");
                    }
                });
    }

    // 获取动态列表
    //                .addParam("userId", uid)
    //                .addParam("pageNum", pageNum)
    //                .addParam("pageSize", pageSize)
    private void getFeedList(int pageNum, int pageSize) {
        if (!mSwipeRefreshLayout.isRefreshing() && RefreshMODE == MOD_REFRESH) mSwipeRefreshLayout.setRefreshing(true);
        String uid = SPUtil.build().getString(Constants.SP_USER_ID);
        OkUtil.get()
                .url(Api.pageFeed+"?pageNum="+pageNum+"&pageSize="+pageSize)

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
        mFeedAdapter.setData(data);
    }

    // 更新数据
    public void updateData(List<Feed> data) {
        mFeedAdapter.addData(data);
    }

    // 刷新数据
    private void onRefresh(){
        RefreshMODE = MOD_REFRESH;
        mPage = 1;
        getFeedList(mPage, mCount);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 发布动态回退则掉调起刷新
        if (resultCode == Constants.ACTIVITY_PUBLISH) {
            onRefresh();
        }
    }

    /**
     * 前往用户页面
     */
    private void goToUser(User user){
        Intent intent = new Intent(getActivity(), UserActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.PASSED_USER_INFO, user);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
