package com.ins.sharesdk.dialog;

import android.content.Context;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by Administrator on 2017/1/4.
 */

public class ShareHelper {

    public static void showShareWeixin(Context context, String title, String url, String content, String img) {
        ShareSDK.initSDK(context);
        showShare(context, ShareSDK.getPlatform(Wechat.NAME).getName(), title, url, content, img);
    }

    public static void showSharePengyouquan(Context context, String title, String url, String content, String img) {
        ShareSDK.initSDK(context);
        showShare(context, ShareSDK.getPlatform(WechatMoments.NAME).getName(), title, url, content, img);
    }

    public static void showShareQQ(Context context, String title, String url, String content, String img) {
        ShareSDK.initSDK(context);
        showShare(context, ShareSDK.getPlatform(QQ.NAME).getName(), title, url, content, img);
    }

    public static void showShareSina(Context context, String title, String url, String content, String img) {
        ShareSDK.initSDK(context);
        showShare(context, ShareSDK.getPlatform(SinaWeibo.NAME).getName(), title, url, content, img);
    }

    public static void showShare(Context context, String platformToShare, String title, String url, String content, String img) {

        OnekeyShare oks = new OnekeyShare();
        if (platformToShare != null) {
            oks.setPlatform(platformToShare);
        }

        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath(img);//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);
        oks.setImageUrl(img);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        //oks.setSite(context.getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(url);


//        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
//        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
//        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//        oks.setTitle(title);
//        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl("http://sharesdk.cn");
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText("我是分享文本");
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
//        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl("http://sharesdk.cn");
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
//        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite("site");
//        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl("http://sharesdk.cn");
//        oks.setImageUrl("https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=3956038193,2397454070&fm=58&s=0614EE22C7E05D030C5498D40000C0B3");

        // 启动分享GUI
        oks.show(context);
    }
}
