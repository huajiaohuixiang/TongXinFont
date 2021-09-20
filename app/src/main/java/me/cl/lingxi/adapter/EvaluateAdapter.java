package me.cl.lingxi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.common.util.ContentUtil;
import me.cl.lingxi.databinding.FeedEvaluateRecycleItemBinding;
import me.cl.lingxi.entity.Comment;
import me.cl.lingxi.entity.Reply;
import okhttp3.Call;

/**
 * Evaluate Adapter
 */
public class EvaluateAdapter extends RecyclerView.Adapter<EvaluateAdapter.EvaluateViewHolder> {

    private List<Comment> mList;
    private  String avatar;
    private OnItemListener mOnItemListener;

    public interface OnItemListener {
        void onItemClick(View view, Comment comment);

        void onItemChildClick(View view, String eid, Reply reply);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public EvaluateAdapter(List<Comment> list) {
        this.mList = list;
    }

    @NonNull
    @Override
    public EvaluateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FeedEvaluateRecycleItemBinding binding = FeedEvaluateRecycleItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new EvaluateViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EvaluateViewHolder holder, int position) {
        holder.bindItem(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setDate(List<Comment> data) {
        mList = data;
        notifyDataSetChanged();
    }

    public void updateData(List<Comment> data) {
        mList.addAll(data);
        notifyDataSetChanged();
    }

    class EvaluateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mUserImg;
        private final TextView mUserName;
        private final TextView mEvaluateTime;
        private final AppCompatTextView mEvaluateInfo;
        private final RecyclerView mRecyclerView;
        private Comment mComment;

        public EvaluateViewHolder(FeedEvaluateRecycleItemBinding binding) {
            super(binding.getRoot());
            RelativeLayout evaluateBody = binding.evaluateBody;
            mUserImg = binding.userImg;
            mUserName = binding.userNickname;
            mEvaluateTime = binding.evaluateTime;
            mEvaluateInfo = binding.evaluateInfo;
            mRecyclerView = binding.recyclerView;

            mUserImg.setOnClickListener(this);
            evaluateBody.setOnClickListener(this);

            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(), RecyclerView.VERTICAL, false);
            mRecyclerView.setLayoutManager(layoutManager);
        }

        public void bindItem(Comment comment) {
            mComment = comment;

            ContentUtil.loadUserAvatar(mUserImg,comment.getAvatar());

            mUserName.setText(comment.getNickname());
            mEvaluateTime.setText(comment.getComTime());
            mEvaluateInfo.setText(comment.getComment());
            ReplyAdapter adapter = new ReplyAdapter(comment.getReplyList());
            mRecyclerView.setAdapter(adapter);

//            final String eid = comment.get_id();
//            adapter.setOnItemListener(new ReplyAdapter.OnItemListener() {
//                @Override
//                public void onItemClick(View view, Reply reply) {
//                    if (mOnItemListener != null) mOnItemListener.onItemChildClick(view, eid, reply);
//                }
//            });
        }

        private void getUserAvatar(String username){
            OkUtil.post()
                    .url(Api.getUserAvatar)
                    .addParam("username",username)
                    .execute(new ResultCallback<Result<String>>() {
                        @Override
                        public void onSuccess(Result<String> response) {
                            String code = response.getCode();
                            if (!"00000".equals(code)) {

                                avatar ="";
                            }
                            avatar=response.getData();
                        }

                        @Override
                        public void onError(Call call, Exception e) {
                            avatar="";
                        }
                    });


        }



        @Override
        public void onClick(View view) {
            if (mOnItemListener != null) mOnItemListener.onItemClick(view, mComment);
        }
    }
}
