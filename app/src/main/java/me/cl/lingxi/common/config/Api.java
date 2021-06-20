package me.cl.lingxi.common.config;

import me.cl.lingxi.BuildConfig;

/**
 * api manage
 * Created by bafsj on 17/3/1.
 */
public class Api {

    /**
     * token
     */
    //"X-App-Token"
    public static final String X_APP_TOKEN = "Authorization";

    /**
     * 收束gradle的flavor控制，将url变量在此接管
     */
    private static String baseUrl = "http://120.79.157.21:8080/lingxi";
    public static String rssUrl = "http://120.79.157.21:8080/lingxi/rss/lingxi";
    private static String myBaseUrl="http://120.79.157.21:5000/api";
    //
    static {

        String flavor = BuildConfig.FLAVOR;
        switch (flavor) {
            case "local":
                baseUrl = "http://120.79.157.21:8080/lingxi";
                rssUrl = "http://120.79.157.21:8080/rss/lingxi-test";
                break;
            case "alpha":
                baseUrl = "http://192.168.1.108:65406/api";
                rssUrl = "http://127.0.0.1:5000/rss/lingxi-test";
                break;
            case "online":
                baseUrl = "http://120.79.157.21:8080/lingxi";
                rssUrl = "http://42.192.229.55/rss/lingxi";
                break;
        }
    }


    public static String getUser = myBaseUrl + "/UserAdmin/getAllUser";
    /**
     * 获取验证码
     */
    public  static  String userIdentityCode =  myBaseUrl +"/UserAdmin/GetCode";

    /**
     * 用户注册myBaseUrl
     */
    public static String userRegister = myBaseUrl + "/UserAdmin/Register";
    /**
     * 用户登录"http://192.168.1.108:5000/api"
     */
    public static String userLogin = myBaseUrl + "/UserAdmin/Login";
    /**
     * 获取用户信息
     */
    public static String userInfo = myBaseUrl + "/UserAdmin/getUser";

    /**
     * 动态列表
     */
    public static String pageFeed = myBaseUrl + "/Post/getAllUserPostPage";

    /**
     * 获取我的动态
     */
    public static String myPostList = myBaseUrl + "/Post/getUserPost";

    /**
     * 获取我喜欢的帖子
     */
    public static String myLikePostList = myBaseUrl + "/Post/GetUserLike";
    /**
     * 获取我的评论
     */
    public static String myLikeComment = myBaseUrl + "/Post/getUserComment";

    /**
     * 动态评论列表 baseUrl + "/feed/comment/page"
     */
    public static String pageComment = myBaseUrl+"/Comment/getPostComment";

    /**
     * 查看动态
     */
    public static String viewFeed = myBaseUrl + "/Post/addView";

    /**
     * 新增动态操作,如点赞
     */
    public static String saveAction = myBaseUrl + "/Post/UserAddLike";

    /**
     * 移除动态操作,如取消赞
     */

    public static String UnsaveAction = myBaseUrl + "/Post/UserUnLike";

    /**
     * 发布动态
     */
    public static String saveFeed = myBaseUrl + "/Post/CreatePost";

    /**
     * 上传动态图片
     */
    public static String uploadFeedImage = myBaseUrl + "/Resource/uploadImage";

    /**
     * 上传用户图片
     */
    public static String uploadUserImage = myBaseUrl + "/Resource/uploadAvatar";

    /**
     * 获取用户头像链接
     */
    public static String getUserAvatar = myBaseUrl + "/UserAdmin/getUserAvatar";
    /**
     * 更新用户头像
     */
    public static String updateUserAvatar = myBaseUrl + "/UserAdmin/UpdateUserAvatar";
    /**
     * 新增动态评论
     */
    public static String saveComment = myBaseUrl + "/Comment/Create";


    /**
     * 与我相关
     */
    public static String getMyComment = myBaseUrl + "/Comment/getUserComment";



    /**
     * 是否关注
     */
    public static String isFollow = myBaseUrl + "/UserFollow/isFollow";



    /**
     * 关注
     */
    public static String Follow = myBaseUrl + "/UserFollow/Follow";

    /**
     * 取消关注
     */
    public static String unFollow = myBaseUrl + "/UserFollow/UnFollow";


    /**
     * 获取最新app版本
     */
    public static String latestVersion = myBaseUrl + "/Version/Latest";

    /**
     * 获取最新app
     */
    public static String latestApp = "http://120.79.157.21" + "/app/lastest.apk";

    /**
     * 重置密码
     */
    public static String resetPassword = myBaseUrl + "/UserAdmin/ResetPass";

    /**
     * 精准查询用户信息
     */
    public static String searchUser = baseUrl + "/user/search";

    /**
     * 动态删除
     */
    public static String removeFeed = baseUrl + "/feed/remove";


    /**
     * 更新用户信息
     */
    public static String updateUser = baseUrl + "/user/update";


    /**
     * 查询用户信息
     */
    public static String queryUser = baseUrl + "/user/query";
    /**
     * 融云用户列表
     */
    public static String listRcUser = baseUrl + "/user/rc/list";

    /**
     * 我的评论
     */
    public static String mineReply = baseUrl + "/feed/mine/reply";




    /**
     * 未读条数
     */
    public static String unreadComment = baseUrl + "/feed/comment/unread";
    /**
     * 更新未读为已读
     */
    public static String updateUnread = baseUrl + "/feed/comment/unread/update";
    /**
     * 保存写给未来
     */
    public static String saveFuture = baseUrl + "/future/save";
    /**
     * 未来日记
     */
    public static String pageFuture = baseUrl + "/future/page";

    /**
     * 话题查询
     */
    public static String queryTopic = baseUrl + "/feed/topic/query";
}
