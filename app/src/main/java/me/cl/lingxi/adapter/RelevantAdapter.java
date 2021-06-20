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

import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.common.util.ContentUtil;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.databinding.RelevantRecycleItemBinding;
import me.cl.lingxi.entity.Comment;
import me.cl.lingxi.entity.Feed;
import me.cl.lingxi.entity.Relevant;
import me.cl.lingxi.entity.Reply;
import me.cl.lingxi.entity.User;
import okhttp3.Call;

/**
 * Reply Adapter
 */
public class RelevantAdapter extends RecyclerView.Adapter<RelevantAdapter.RelevantViewHolder> {

    private final List<Relevant> mList;

    private OnItemListener mOnItemListener;

    public interface OnItemListener {
        void onItemClick(View view, Relevant relevant);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public RelevantAdapter(List<Relevant> list) {
        this.mList = list;
    }

    @NonNull
    @Override
    public RelevantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RelevantRecycleItemBinding binding = RelevantRecycleItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RelevantViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RelevantViewHolder holder, int position) {
        holder.bindItem(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateData(List<Relevant> data) {
        mList.addAll(data);
        notifyDataSetChanged();
    }

    class RelevantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mUserImg;
        private final TextView mUserName;
        private final TextView mRelevantTime;
        private final AppCompatTextView mRelevantInfo;
        private final AppCompatTextView mFeedInfo;
        private final LinearLayout mFeedBody;

        private Relevant mRelevant;

        public RelevantViewHolder(RelevantRecycleItemBinding binding) {
            super(binding.getRoot());

            mUserImg = binding.userImg;
            mUserName = binding.userNickname;
            mRelevantTime = binding.relevantTime;
            mRelevantInfo = binding.relevantInfo;
            mFeedInfo = binding.feedInfo;
            mFeedBody = binding.feedBody;

            mUserImg.setOnClickListener(this);
            mFeedBody.setOnClickListener(this);
        }

        public void bindItem(Relevant relevant) {
            mRelevant = relevant;
            Comment comment = relevant.getComment();
          //  User user = comment.getUser();
            Integer replyNum = relevant.getReplyNum();
            StringBuilder relevantInfo = new StringBuilder();
            String timeStr = "";
            if (replyNum > 0) {
                List<Reply> replyList = relevant.getReplyList();
                for (int i = 0, size = replyList.size(); i < size; i++) {
                    Reply reply = replyList.get(i);
                    if (i == 0) {
                        //user = reply.getUser();
                        timeStr = reply.getCreateTime();
                        relevantInfo.append(reply.getCommentInfo());
                    } else {
                        relevantInfo.append("//{@").append(reply.getUser().getUsername()).append("}:")
                                .append(reply.getCommentInfo());
                    }

                }
                relevantInfo.append("//{@").append(comment.getUsername()).append("}:")
                        .append(comment.getComment());
            } else {
                timeStr = comment.getComTime();
                relevantInfo.append(comment.getComment());
            }

            ContentUtil.loadUserAvatar(mUserImg,getUserAvatar(comment.getUsername()));

            mUserName.setText(comment.getUsername());
            mRelevantTime.setText(timeStr);

            mRelevantInfo.setText(Utils.colorFormat(relevantInfo.toString()));

            Feed feed = relevant.getFeed();
            String feedInfo = "{" + feed.getNickname() + "}：" + feed.getContent();
            mFeedInfo.setText(Utils.colorFormat(feedInfo));
        }


        private String getUserAvatar(String username){
            final String[] result = new String[1];
            OkUtil.post()
                    .url(Api.getUserAvatar)
                    .addParam("username",username)
                    .execute(new ResultCallback<Result<String>>() {
                        @Override
                        public void onSuccess(Result<String> response) {
                            String code = response.getCode();
                            if (!"00000".equals(code)) {

                                result[0] ="";
                            }
                            result[0]=response.getData();
                        }

                        @Override
                        public void onError(Call call, Exception e) {

                        }
                    });
            return  result[0];
        }

        @Override
        public void onClick(View view) {
            if (mOnItemListener != null) mOnItemListener.onItemClick(view, mRelevant);
        }
    }
}
