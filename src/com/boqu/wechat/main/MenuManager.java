package com.boqu.wechat.main;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boqu.wechat.pojo.AccessToken;
import com.boqu.wechat.pojo.Button;
import com.boqu.wechat.pojo.CommonButton;
import com.boqu.wechat.pojo.ComplexButton;
import com.boqu.wechat.pojo.Menu;
import com.boqu.wechat.pojo.ViewButton;
import com.boqu.wechat.util.Constant;
import com.boqu.wechat.util.WeixinUtil;

public class MenuManager {

    private static Logger log = LoggerFactory.getLogger(MenuManager.class);

    // 获取code回调地址
    private static String redirect_uri = "http://wechattest.jia16.com/WeChatServer/coreServlet";

    public static void main(String[] args) {

        PropertyConfigurator.configure("log4j.properties");

        // 调用接口获取access_token
        AccessToken at = WeixinUtil.getAccessToken(Constant.appId,
                Constant.appSecret);

        if (null != at) {
            // 调用接口创建菜单
            int result = WeixinUtil.createMenu(getMenu(), at.getToken());

            // 判断菜单创建结果
            if (0 == result)
                log.error("菜单创建成功！");
            else
                log.error("菜单创建失败，错误码：" + result);
        }
    }

    /**
     * 组装菜单数据
     * 
     * @return
     */
    private static Menu getMenu() {

        // 联系我们
        String url11 = "http://mp.weixin.qq.com/s?__biz=MzA4MjgzMzE3NQ==&mid=209271248&idx=1&sn=611c37f7d349dded28b485fc72338a6c#rd";
        // 平台简介
        String url12 = "http://mp.weixin.qq.com/s?__biz=MzA4MjgzMzE3NQ==&mid=209271983&idx=1&sn=8e7caf3f530e41c50a6b38ac811fa710#rd";
        // 帮助中心
        String url13 = "http://mp.weixin.qq.com/s?__biz=MzA4MjgzMzE3NQ==&mid=209272156&idx=1&sn=91f2e119bd35bee851821d264daa8f4a#rd";
        // 近期活动
        String url21 = "http://mp.weixin.qq.com/s?__biz=MzA4MjgzMzE3NQ==&mid=209363322&idx=1&sn=4084dce31ff63f42d9e53bc082e98338#rd";

        String url22 = WeixinUtil.getCodeUrl(Constant.appId, redirect_uri,
                "snsapi_base", "22");
        String url31 = WeixinUtil.getCodeUrl(Constant.appId, redirect_uri,
                "snsapi_base", "31");
        String url32 = WeixinUtil.getCodeUrl(Constant.appId, redirect_uri,
                "snsapi_base", "32");

        // CommonButton btn11 = new CommonButton();
        // btn11.setName("联系我们");
        // btn11.setType("click");
        // btn11.setKey("11");
        //
        // CommonButton btn12 = new CommonButton();
        // btn12.setName("平台简介");
        // btn12.setType("click");
        // btn12.setKey("12");
        //
        // CommonButton btn13 = new CommonButton();
        // btn13.setName("帮助中心");
        // btn13.setType("click");
        // btn13.setKey("13");

        ViewButton btn11 = new ViewButton();
        btn11.setName("联系我们");
        btn11.setType("view");
        btn11.setUrl(url11);

        ViewButton btn12 = new ViewButton();
        btn12.setName("平台简介");
        btn12.setType("view");
        btn12.setUrl(url12);

        ViewButton btn13 = new ViewButton();
        btn13.setName("帮助中心");
        btn13.setType("view");
        btn13.setUrl(url13);

        ViewButton btn21 = new ViewButton();
        btn21.setName("近期活动");
        btn21.setType("view");
        btn21.setUrl(url21);

        ViewButton btn22 = new ViewButton();
        btn22.setName("我要投资");
        btn22.setType("view");
        btn22.setUrl(url22);

        ViewButton btn31 = new ViewButton();
        btn31.setName("个人中心");
        btn31.setType("view");
        btn31.setUrl(url31);

        ViewButton btn32 = new ViewButton();
        btn32.setName("微信绑定");
        btn32.setType("view");
        btn32.setUrl(url32);

        ComplexButton mainBtn1 = new ComplexButton();
        mainBtn1.setName("嘉石榴");
        mainBtn1.setSub_button(new Button[] { btn11, btn12, btn13 });

        ComplexButton mainBtn2 = new ComplexButton();
        mainBtn2.setName("要投资");
//        mainBtn2.setSub_button(new Button[] { btn21});
        mainBtn2.setSub_button(new Button[] { btn21, btn22 });

        ComplexButton mainBtn3 = new ComplexButton();
        mainBtn3.setName("个人中心");
        mainBtn3.setSub_button(new Button[] { btn31, btn32 });

        Menu menu = new Menu();
        menu.setButton(new Button[] { mainBtn1, mainBtn2, mainBtn3 });

        return menu;
    }
}
