package me.cl.lingxi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.cl.lingxi.common.util.ContentUtil;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.databinding.MyCommentItemBinding;
import me.cl.lingxi.entity.Comment;
import me.cl.lingxi.entity.CommentPost;
import me.cl.lingxi.entity.Feed;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyCommentViewHolder> {
    private List<CommentPost> mList;

    private CommentAdapter.OnItemListener mOnItemListener;

    public interface OnItemListener {
        void onItemClick(View view, CommentPost relevant);
    }

    public void setOnItemListener(CommentAdapter.OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public CommentAdapter(List<CommentPost> list) {
        this.mList = list;
    }

    @NonNull
    @Override
    public CommentAdapter.MyCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyCommentItemBinding binding = MyCommentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CommentAdapter.MyCommentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.MyCommentViewHolder holder, int position) {
        holder.bindItem(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateData(List<CommentPost> data) {
        mList.addAll(data);
        notifyDataSetChanged();
    }



    class MyCommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mUserImg;
        private final TextView mUserName;
        private final TextView mCommentTime;
        private final AppCompatTextView mCommentInfo;
        private final AppCompatTextView mFeedInfo;
        private final LinearLayout mFeedBody;

        private CommentPost commentPost;

        public MyCommentViewHolder(MyCommentItemBinding binding) {
            super(binding.getRoot());

            mUserImg = binding.userImg;
            mUserName = binding.userNickname;
            mCommentTime = binding.commentTime;
            mCommentInfo = binding.CommentInfo;
            mFeedInfo = binding.feedInfo;
            mFeedBody = binding.feedBody;

            mUserImg.setOnClickListener(this);
            mFeedBody.setOnClickListener(this);
        }

        public void bindItem(CommentPost commentPost) {
            commentPost = commentPost;
            Comment comment = commentPost.getComment();
            Feed feed = commentPost.getPost();


            ContentUtil.loadUserAvatar(mUserImg,comment.getAvatar());

            mUserName.setText(feed.getNickname());
            mCommentTime.setText(commentPost.getComment().getComTime());

            // mCommentTime.setText(DateUtil.showTime(commentPost.getComment().getComTime()));

            mCommentInfo.setText(Utils.colorFormat(commentPost.getComment().getComment().toString()));

            String feedInfo = "{" + feed.getNickname() + "}：" + feed.getContent();
            mFeedInfo.setText(Utils.colorFormat(feedInfo));
        }


        @Override
        public void onClick(View view) {
            if (mOnItemListener != null) mOnItemListener.onItemClick(view, commentPost);
        }
    }
}
