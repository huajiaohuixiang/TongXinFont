package me.cl.lingxi.entity;

import java.io.Serializable;

public class CommentPost implements Serializable {
    private Feed post;
    private Comment comment;

    public Feed getPost() {
        return post;
    }

    public void setPost(Feed post) {
        this.post = post;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
