package com.ins.middle.common;


import com.ins.middle.entity.User;
import com.sobey.common.utils.ApplicationHelp;
import com.sobey.common.utils.PreferenceUtil;

/**
 * 该类封装了app中所有静态数据和持久化数据的读写操作
 * 所有持久化数据都保存在preferences文件中，包括简单数据类型，和复杂数据类型
 * PreferenceUtil提供了保存复杂对象的方法，复杂数据类型需实现Serializable接口
 *
 * @author Administrator
 */
public class AppData {

    public static class App {

        private static final String KEY_STRARUP = "startup";
        private static final String KEY_VERSIONCODE = "versioncode";
        private static final String KEY_TOKEN = "token";
        private static final String KEY_LASTUSERNAME = "lastusername";
        private static final String KEY_DOMAIN = "domain";
        private static final String KEY_USER = "user";

        public static int getVersionCode() {
            String versioncode = PreferenceUtil.getString(ApplicationHelp.getApplicationContext(), KEY_VERSIONCODE);
            if (versioncode == null || "".equals(versioncode)) {
                return 0;
            } else {
                return Integer.parseInt(versioncode);
            }
        }

        public static void saveVersionCode(int versioncode) {
            PreferenceUtil.saveString(ApplicationHelp.getApplicationContext(), KEY_VERSIONCODE, versioncode + "");
        }

        public static void removeVersionCode() {
            PreferenceUtil.remove(ApplicationHelp.getApplicationContext(), KEY_VERSIONCODE);
        }

        public static String getToken() {
            String token = PreferenceUtil.getString(ApplicationHelp.getApplicationContext(), KEY_TOKEN);
            return token;
        }

        public static void saveToken(String token) {
            PreferenceUtil.saveString(ApplicationHelp.getApplicationContext(), KEY_TOKEN, token);
        }

        public static void removeToken() {
            PreferenceUtil.remove(ApplicationHelp.getApplicationContext(), KEY_TOKEN);
        }

        public static String getDomain() {
            String token = PreferenceUtil.getString(ApplicationHelp.getApplicationContext(), KEY_DOMAIN);
            return token;
        }

        public static void saveDomain(String phone) {
            PreferenceUtil.saveString(ApplicationHelp.getApplicationContext(), KEY_DOMAIN, phone);
        }

        public static void removeDomain() {
            PreferenceUtil.remove(ApplicationHelp.getApplicationContext(), KEY_DOMAIN);
        }

        public static void saveUser(User user) {
            PreferenceUtil.saveObject(ApplicationHelp.getApplicationContext(), KEY_USER, user);
        }

        public static User getUser() {
            return (User) PreferenceUtil.readObject(ApplicationHelp.getApplicationContext(), KEY_USER);
        }

        public static void removeUser() {
            PreferenceUtil.remove(ApplicationHelp.getApplicationContext(), KEY_USER);
        }

        //////////////

        public static void removeOrderInfo(String key) {
            PreferenceUtil.remove(ApplicationHelp.getApplicationContext(), key);
        }

        ///////////////保存用户名
        public static String getLastUserName() {
            String token = PreferenceUtil.getString(ApplicationHelp.getApplicationContext(), KEY_LASTUSERNAME);
            return token;
        }

        public static void saveLastUserName(String username) {
            PreferenceUtil.saveString(ApplicationHelp.getApplicationContext(), KEY_LASTUSERNAME, username);
        }

        public static void removeLastUserName() {
            PreferenceUtil.remove(ApplicationHelp.getApplicationContext(), KEY_LASTUSERNAME);
        }
    }

    /**
     * 记录了app中所有全局控制常量
     */
    public static class Config {
        public static boolean showVali = true;            //显示验证码（仅测试）
    }

    /**
     * 记录了app中所有的请求连接地址
     */
    public static class Url {

        /**
         * 服务器域名
         */
//		public static String domain = "http://192.168.118.110:9528/Carpooling/";								//客服测试服务器
//		public static String domain = "http://192.168.118.110:9528/Carpooling/";								//内部测试服务器
        public static String domain = "http://192.168.118.196:9527/Carpooling/";                                //开发服务器(谢启谋)
//		public static String domain = "http://192.168.0.119:8080/Carpooling/";								//开发服务器(李作焕)

        /**
         * 接口请求地址
         */
        public static String version			    	= domain + "updateAPK/version.json";	     								    //检查更新
        public static String checkMobile			    = domain + "mobile/user/checkMobile";											//检测用户是否注册
        public static String sendCode			    	= domain + "mobile/user/sendCode";												//发送验证码
        public static String login			    	= domain + "mobile/user/login";													//登录
        public static String logout			    	= domain + "mobile/user/logout";													//注销
        public static String register			    	= domain + "mobile/user/register";												//注册
        public static String setPsw			    	= domain + "mobile/user/updateByMobile";										//已创建账号设置密码（乘客）
        public static String getInfo			    	= domain + "mobile/user/getInfo";												//token登陆
        public static String upload			    	= domain + "res/upload";												            //上传资源
        public static String updateUser		    	= domain + "mobile/user/update";												    //修改个人信息
        public static String identify   		    	= domain + "mobile/user/submitAuth";										    //车主认证
        public static String feedback   		    	= domain + "mobile/suggest/add";										            //意见反馈
        public static String onOffLine   		    	= domain + "mobile/order/onOffLine";										    //司机上下线（司机）
        public static String updateLat   		    	= domain + "mobile/order/updateLat";										    //司机上传经纬度（司机）

    }
}
