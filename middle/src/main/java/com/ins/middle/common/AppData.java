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
        private static final String KEY_PHONE = "phone";
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

        public static String getPhone() {
            String token = PreferenceUtil.getString(ApplicationHelp.getApplicationContext(), KEY_PHONE);
            return token;
        }

        public static void savePhone(String phone) {
            PreferenceUtil.saveString(ApplicationHelp.getApplicationContext(), KEY_PHONE, phone);
        }

        public static void removePhone() {
            PreferenceUtil.remove(ApplicationHelp.getApplicationContext(), KEY_PHONE);
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
        public static final boolean showVali = false;            //显示验证码（仅测试）
    }

    /**
     * 记录了app中所有的请求连接地址
     */
    public static class Url {

        /**
         * 服务器域名
         */
		public static final String domain = "http://139.129.111.76:8082/WoGia/";								//客服测试服务器
//		public static final String domain = "http://192.168.0.139:8080/WoGia/";								//内部测试服务器
//        public static final String domain = "http://192.168.0.155/WoGia/";                                //开发服务器(谢启谋)
//		public static final String domain = "http://192.168.0.119:8080/WoGia/";								//开发服务器(李作焕)

        /**
         * 接口请求地址
         */
        public static final String version			    	= domain + "updateAPK/versionxx.json";	     								//检查更新
        public static final String getInfo			    	= domain + "app/user/getInfo";												//获取个人信息
        public static final String login			    		= domain + "app/user/login";													//登录
        public static final String updateUser			    = domain + "app/user/updateUser";											//注册填写个人信息
        public static final String updateUserInfo		    = domain + "app/user/updateUserInfo";										//更新个人信息
        public static final String sendCode			    	= domain + "app/user/sendCode";												//发送验证码
        public static final String sendCodeLogin			= domain + "app/user/sendCodeLogin";										//登录时发送发送验证码（验重复）
        public static final String forgetPwd			    = domain + "app/user/forgetPwd";											//忘记密码
        public static final String updatePwd			    = domain + "app/user/updatePwd";											//修改密码
        public static final String loginOut			    	= domain + "app/user/loginOut";												//登出
        public static final String upload				    = domain + "res/upload";														//上传
        public static final String findCitys			    = domain + "app/city/findCitys";											//获取城市筛选列表
        public static final String findProjects			    = domain + "app/project/findProjects";										//获取项目列表
        public static final String findDevice			    = domain + "app/device/findDevice";											//获取分区（设备）
        public static final String monitorData			    = domain + "app/monitorData/monitorData";									//获取流量数据
        public static final String monitorWater			    = domain + "app/monitorData/monitorWater";								//获取流量数据
        public static final String bengInfo				    = domain + "app/deviceInformation/findDeviceInformationByDeviceId";	//泵站信息
        public static final String chartPage				= domain + "requestWeb/monitorPage";										//监控图表
        public static final String getProjectBySelf		= domain + "app/project/getProjectBySelf";								//获取自己的项目
        public static final String getPart					= domain + "app/componentDevice/findComponentDevice";					//获取零件
        public static final String orderAdd					= domain + "app/order/add";					                                //添加订单
        public static final String getOrders				= domain + "app/order/getOrders";					                        //获取订单
        public static final String getTech				    = domain + "app/user/getTech";					                            //获取技术人员列表
        public static final String orderDetail				= domain + "app/order/orderDetail";					                        //获取订单详情
        public static final String allotOrder				= domain + "app/order/allotOrder";					                        //分配订单
        public static final String acceptOrder				= domain + "app/order/acceptOrder";					                        //接受任务/开始维修/维修完成
        public static final String verifiOrder				= domain + "app/order/verifiOrder";					                        //完成拒绝订单
        public static final String orderProg				= domain + "app/order/getOrderData";					                    //订单进度
        public static final String findWaterWork			= domain + "app/water/findWaterWork";					                    //查询水厂列表
        public static final String getNews                  = domain + "app/news/findNews";					                            //获取资讯列表
        public static final String getBanners   			= domain + "app/news/getBanners";					                        //查询banner
        public static final String newsDetail   			= domain + "requestWeb/newsDetail";					                        //资讯详情
        public static final String getMsg          			= domain + "app/info/getMsg";					                            //系统公告
        public static final String getStopWaterInfo   		= domain + "app/info/getStopWaterInfo";				                    //停水公告
        public static final String systemInfo       		= domain + "requestWeb/systemInfo";					                        //系统公告详情
        public static final String servicePromise  	    = domain + "requestWeb/servicePromise";					                //服务承诺
        public static final String feedback           	    = domain + "app/suggest/add";			               		                //意反馈
        public static final String findContacts        	    = domain + "app/user/findContacts";		               		                //获取联系人列表
        public static final String getDing          	    = domain + "app/project/stareOneStare";		               		        //获取盯一盯数据
        public static final String getUsers          	    = domain + "app/user/batchGetUserInfo";		               		        //批量获取用户信息
        public static final String applyPage          	    = domain + "requestWeb/applyPage";		               		                //申请专项服务（Web页面）
        public static final String getApplyDetail     	    = domain + "app/pad/findNewestByApplyId";		               		        //获取专项服务详情
        public static final String getApplies          	    = domain + "app/project/getApplyProjects";		          		        //获取专项服务列表
        public static final String upFeedCalendar          = domain + "app/componentDevice/maintainCalendar";		          		//保养日历
        public static final String aboutUs                  = domain + "requestWeb/aboutUs";		          		                    //关于我们
        public static final String readContactUs           = domain + "res/readContactUs";		          		                        //联系我们


    }
}
