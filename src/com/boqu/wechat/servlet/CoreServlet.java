package com.boqu.wechat.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boqu.wechat.pojo.OpenID;
import com.boqu.wechat.service.CoreService;
import com.boqu.wechat.util.Constant;
import com.boqu.wechat.util.SignUtil;
import com.boqu.wechat.util.WeixinUtil;

@WebServlet("/CoreServlet")
public class CoreServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(CoreServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String prefix = config.getServletContext().getRealPath("/");
        String file = config.getInitParameter("log4j");
        String filePath = prefix + file;
        PropertyConfigurator.configure(filePath);
    }

    /**
     * 确认请求来自微信服务器
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        String signature = request.getParameter("signature");
        if (signature != null && signature.length() != 0) {
            String timestamp = request.getParameter("timestamp");
            String nonce = request.getParameter("nonce");
            String echostr = request.getParameter("echostr");

            PrintWriter out = response.getWriter();
            // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
            if (SignUtil.checkSignature(signature, timestamp, nonce)) {
                out.print(echostr);
            }
            out.close();
            out = null;
        }

        String code = request.getParameter("code");
        if (code != null && code.length() != 0) {
            String state = request.getParameter("state");
            OpenID openId = WeixinUtil.getOpenIdAndAccessToken(Constant.appId,
                    Constant.appSecret, code);
            // String timestamp = String.valueOf(System.currentTimeMillis());
            // String source = "wechat";
            switch (state) {
            // case "21": {
            // String redirectUrl = Constant.serverUrl + "/wechat/register.html"
            // + "?openId=" + openId.getOpenId() + "&accessToken=" +
            // openId.getAccessToken();
            // response.sendRedirect(redirectUrl);
            // break;
            // }
            case "22": {
                String redirectUrl = Constant.serverUrl
                        + "/wechat/subjects.html" + "?openId="
                        + openId.getOpenId() + "&accessToken="
                        + openId.getAccessToken();
                response.sendRedirect(redirectUrl);
                break;
            }
            case "31": {
                String redirectUrl = Constant.serverUrl
                        + "/wechat/user_center.html" + "?openId="
                        + openId.getOpenId() + "&accessToken="
                        + openId.getAccessToken();
                response.sendRedirect(redirectUrl);
                break;
            }
            case "32": {
                String redirectUrl = Constant.serverUrl
                        + "/wechat/bind_unbind.html" + "?openId="
                        + openId.getOpenId() + "&accessToken="
                        + openId.getAccessToken();
                response.sendRedirect(redirectUrl);
                break;
            }
            default: {
                String redirectUrl = Constant.serverUrl
                        + "/wechat/subjects.html" + "?openId="
                        + openId.getOpenId() + "&accessToken="
                        + openId.getAccessToken();
                response.sendRedirect(redirectUrl);
                break;
            }
            }
        }

    }

    /**
     * 处理微信服务器发来的消息
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // 将请求、响应的编码均设置为UTF-8（防止中文乱码）
        request.setCharacterEncoding("UTF-8");

        String respMessage;
        String jssdk = request.getParameter("jssdk");
//        System.out.println("jssdk=" + jssdk);

        if (jssdk != null && jssdk.length() != 0
                && "wechatproject".equalsIgnoreCase(jssdk)) {
            response.setCharacterEncoding("UTF-8");
            respMessage = CoreService.processJssdk(request);
//            System.out.println("jssdk");
        } else {
            // 调用核心业务类接收消息、处理消息(微信发送来的图文消息)
            response.setCharacterEncoding("UTF-8");
            respMessage = CoreService.processRequest(request);
//            System.out.println("xml");
        }

        // 响应消息
        PrintWriter out = response.getWriter();
        out.print(respMessage);
        out.close();
    }

}
