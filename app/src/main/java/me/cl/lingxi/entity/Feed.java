package me.cl.lingxi.entity;

import java.io.Serializable;
import java.util.List;

// 动态
public class Feed implements Serializable {

    private String id;
    private String username;
    private String nickname;
    private String avatar;
    private String content;


    // 用户信息
    //private User user;
    //private String feedInfo;
    private Integer views;
    // 评论数
    private Integer comments;
    // 当前用户是否点赞
    private boolean like;
    //点赞数
    private Integer likes;
    // 点赞列表
    private List<String> likesList;
    // 相册
    private List<String> photoesList;
    private String createdTime;
    private String updatedTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
//
//    public String getFeedInfo() {
//        return feedInfo;
//    }
//
//    public void setFeedInfo(String feedInfo) {
//        this.feedInfo = feedInfo;
//    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getCommentNum() {
        return comments;
    }

    public void setCommentNum(Integer commentNum) {
        this.comments = commentNum;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public List<String> getLikesList() {
        return likesList;
    }

    public void setLikesList(List<String> likesList) {
        this.likesList = likesList;
    }

    public List<String> getPhotoesList() {
        return photoesList;
    }

    public void setPhotoesList(List<String> photoesList) {
        this.photoesList = photoesList;
    }

    public String getCreateTime() {
        return createdTime;
    }

    public void setCreateTime(String createTime) {
        this.createdTime = createTime;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    public String toString() {
        return "Feed{" +
                "id='" + id + '\'' +
                ", user=" + username +
                ", content='" + content + '\'' +
                ", viewNum=" + views +
                ", commentNum=" + comments +
                ", like=" + like +
                ", likeList=" + likesList +
                ", photoList=" + photoesList +
                ", createTime=" + createdTime +
                ", updateTime=" + updatedTime +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
