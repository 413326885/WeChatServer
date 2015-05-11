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
    private static String redirect_uri = "http://121.40.137.8/WeChatServer/coreServlet";

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

        String btn11_uri = WeixinUtil.getCodeUrl(Constant.appId, redirect_uri,
                "snsapi_base", "11");

        ViewButton btn11 = new ViewButton();
        btn11.setName("注册");
        btn11.setType("view");
        btn11.setUrl(btn11_uri);

        ViewButton btn12 = new ViewButton();
        btn12.setName("绑定");
        btn12.setType("view");
        btn12.setUrl("http://182.92.222.185:3000/login.html");

        ViewButton btn13 = new ViewButton();
        btn13.setName("个人中心");
        btn13.setType("view");
        btn13.setUrl("http://182.92.222.185:3000/investerdetail.html");

        ViewButton btn21 = new ViewButton();
        btn21.setName("充值");
        btn21.setType("view");
        btn21.setUrl("http://182.92.222.185:3000/cubic.html");

        ViewButton btn22 = new ViewButton();
        btn22.setName("取现");
        btn22.setType("view");
        btn22.setUrl("http://182.92.222.185:3000/cubicdetail.html");

        ViewButton btn31 = new ViewButton();
        btn31.setName("联系我们");
        btn31.setType("view");
        btn31.setUrl("http://182.92.222.185:3000/cubicdetail.html");

        CommonButton btn32 = new CommonButton();
        btn32.setName("使用帮助");
        btn32.setType("click");
        btn32.setKey("32");

        ComplexButton mainBtn1 = new ComplexButton();
        mainBtn1.setName("开户");
        mainBtn1.setSub_button(new Button[] { btn11, btn12, btn13 });

        ComplexButton mainBtn2 = new ComplexButton();
        mainBtn2.setName("投资");
        mainBtn2.setSub_button(new Button[] { btn21, btn22 });

        ComplexButton mainBtn3 = new ComplexButton();
        mainBtn3.setName("更多");
        mainBtn3.setSub_button(new Button[] { btn31, btn32 });

        /**
         * 这是公众号小嘉嘉目前的菜单结构，每个一级菜单都有二级菜单项<br>
         * 
         * 在某个一级菜单下没有二级菜单的情况，menu该如何定义呢？<br>
         * 比如，第三个一级菜单项不是“更多体验”，而直接是“幽默笑话”，那么menu应该这样定义：<br>
         * menu.setButton(new Button[] { mainBtn1, mainBtn2, btn33 });
         */
        Menu menu = new Menu();
        menu.setButton(new Button[] { mainBtn1, mainBtn2, mainBtn3 });

        return menu;
    }
}
