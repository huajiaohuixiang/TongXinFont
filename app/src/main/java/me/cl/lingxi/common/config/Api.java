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
    static {

        String flavor = BuildConfig.FLAVOR;
        switch (flavor) {
            case "alpha":
                baseUrl = "http://120.79.157.21:8080/lingxi";
                rssUrl = "http://120.79.157.21:8080/rss/lingxi-test";
                break;
            case "local":
                baseUrl = "http://127.0.0.1:5000/api";
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
     * 动态评论列表 baseUrl + "/feed/comment/page"
     */
    public static String pageComment = myBaseUrl+"/Comment/getPostComment";



    /**
     * 发布动态
     */
    public static String saveFeed = baseUrl + "/feed/save";
    /**
     * 查看动态
     */
    public static String viewFeed = baseUrl + "/feed/view";



    /**
     * 重置密码
     */
    public static String resetPassword = baseUrl + "/user/reset";
    /**
     * 更新用户信息
     */
    public static String updateUser = baseUrl + "/user/update";

    /**
     * 精准查询用户信息
     */
    public static String searchUser = baseUrl + "/user/search";
    /**
     * 查询用户信息
     */
    public static String queryUser = baseUrl + "/user/query";
    /**
     * 融云用户列表
     */
    public static String listRcUser = baseUrl + "/user/rc/list";

    /**
     * 与我相关
     */
    public static String relevant = baseUrl + "/feed/relevant";
    /**
     * 我的评论
     */
    public static String mineReply = baseUrl + "/feed/mine/reply";
    /**
     * 新增动态操作,如点赞
     */
    public static String saveAction = baseUrl + "/feed/action/save";
    /**
     * 移除动态操作,如取消赞
     */
    public static String removeAction = baseUrl + "/feed/action/remove";

    /**
     * 新增动态评论
     */
    public static String saveComment = baseUrl + "/feed/comment/save";
    /**
     * 获取最新app版本
     */
    public static String latestVersion = baseUrl + "/app/version/latest";
    /**
     * 上传用户图片
     */
    public static String uploadUserImage = baseUrl + "/rss/upload/user/image";
    /**
     * 上传动态图片
     */
    public static String uploadFeedImage = baseUrl + "/rss/upload/feed/image";
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
     * 动态删除
     */
    public static String removeFeed = baseUrl + "/feed/remove";
    /**
     * 话题查询
     */
    public static String queryTopic = baseUrl + "/feed/topic/query";
}