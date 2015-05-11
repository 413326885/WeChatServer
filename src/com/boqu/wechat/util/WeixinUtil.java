package com.boqu.wechat.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boqu.wechat.pojo.AccessToken;
import com.boqu.wechat.pojo.Menu;
import com.boqu.wechat.pojo.OpenID;

/**
 * 公众平台通用接口工具类
 * 
 * @date 2013-08-09
 */
public class WeixinUtil {

    private static Logger log = LoggerFactory.getLogger(WeixinUtil.class);

    // 菜单创建（POST） 限100（次/天）
    public static String menu_create_url = "https://api.weixin.qq.com/cgi-bin/"
            + "menu/create?access_token=ACCESS_TOKEN";

    // 获取access_token的接口地址（GET） 限200（次/天）
    public final static String access_token_url = "https://api.weixin.qq.com/"
            + "cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    // 获取code的接口地址
    public final static String code_url = "https://open.weixin.qq.com/connect/"
            + "oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&"
            + "response_type=code&scope=SCOPE&state=STATE#wechat_redirect";

    // 获取openid的接口地址
    public final static String openid_url = "https://api.weixin.qq.com/sns/"
            + "oauth2/access_token?appid=APPID&secret=SECRET"
            + "&code=CODE&grant_type=authorization_code";

    /**
     * 获取access_token
     * 
     * @param appid
     *            凭证
     * @param appsecret
     *            密钥
     * @return
     */
    public static AccessToken getAccessToken(String appid, String appsecret) {
        AccessToken accessToken = null;

        String requestUrl = access_token_url.replace("APPID", appid).replace(
                "APPSECRET", appsecret);
        JSONObject jsonObject = httpRequest(requestUrl, "GET", null);
        // 如果请求成功
        if (null != jsonObject) {
            try {
                accessToken = new AccessToken();
                accessToken.setToken(jsonObject.getString("access_token"));
                accessToken.setExpiresIn(jsonObject.getInt("expires_in"));
            } catch (JSONException e) {
                accessToken = null;
                // 获取token失败
                log.error("获取token失败 errcode:{} errmsg:{}",
                        Integer.valueOf(jsonObject.getInt("errcode")),
                        jsonObject.getString("errmsg"));
            }
        }
        return accessToken;
    }

    /**
     * 获取openid
     * 
     * @param appid
     *            凭证
     * @param appsecret
     *            密钥
     * @return
     */
    public static OpenID getOpenIdAndAccessToken(String appid,
            String appsecret, String code) {
        OpenID openID = null;

        String requestUrl = openid_url.replace("APPID", appid)
                .replace("APPSECRET", appsecret).replace("CODE", code);
        JSONObject jsonObject = httpRequest(requestUrl, "GET", null);

        // 如果请求成功
        if (null != jsonObject) {
            try {
                openID = new OpenID();
                openID.setAccessToken(jsonObject.getString("access_token"));
                openID.setExpiresIn(jsonObject.getInt("expires_in"));
                openID.setOpenId(jsonObject.getString("openif"));
                openID.setRefreshToken(jsonObject.getString("refresh_token"));
                openID.setUnionId(jsonObject.getString("unionid"));
            } catch (JSONException e) {
                openID = null;
                // 获取token失败
                log.error("获取token失败 errcode:{} errmsg:{}",
                        Integer.valueOf(jsonObject.getInt("errcode")),
                        jsonObject.getString("errmsg"));
            }
        }
        return openID;
    }

    /**
     * 创建菜单
     * 
     * @param menu
     *            菜单实例
     * @param accessToken
     *            有效的access_token
     * @return 0表示成功，其他值表示失败
     */
    public static int createMenu(Menu menu, String accessToken) {
        int result = 0;

        // 拼装创建菜单的url
        String url = menu_create_url.replace("ACCESS_TOKEN", accessToken);
        // 将菜单对象转换成json字符串
        String jsonMenu = JSONObject.fromObject(menu).toString();
        // 调用接口创建菜单
        JSONObject jsonObject = httpRequest(url, "POST", jsonMenu);

        if (null != jsonObject) {
            if (0 != jsonObject.getInt("errcode")) {
                result = jsonObject.getInt("errcode");
                log.error("创建菜单失败 errcode:{} errmsg:{}",
                        Integer.valueOf(jsonObject.getInt("errcode")),
                        jsonObject.getString("errmsg"));
            }
        }

        return result;
    }

    /**
     * 发起https请求并获取结果
     * 
     * @param requestUrl
     *            请求地址
     * @param requestMethod
     *            请求方式（GET、POST）
     * @param outputStr
     *            提交的数据
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    public static JSONObject httpRequest(String requestUrl,
            String requestMethod, String outputStr) {
        JSONObject jsonObject = null;
        StringBuffer buffer = new StringBuffer();
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = { new MyX509TrustManager() };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            URL url = new URL(requestUrl);
            HttpsURLConnection httpUrlConn = (HttpsURLConnection) url
                    .openConnection();
            httpUrlConn.setSSLSocketFactory(ssf);

            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod(requestMethod);

            if ("GET".equalsIgnoreCase(requestMethod))
                httpUrlConn.connect();

            // 当有数据需要提交时
            if (null != outputStr) {
                OutputStream outputStream = httpUrlConn.getOutputStream();
                // 注意编码格式，防止中文乱码
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            // 将返回的输入流转换成字符串
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(
                    inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);

            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            inputStream = null;
            httpUrlConn.disconnect();
            jsonObject = JSONObject.fromObject(buffer.toString());
        } catch (ConnectException ce) {
            log.error("Weixin server connection timed out.");
        } catch (Exception e) {
            log.error("https request error:{}", e);
        }
        return jsonObject;
    }

    public static String getCodeUrl(String appid, String redirectUri,
            String scope, String state) {

        String redirect = null;
        try {
            redirect = URLEncoder.encode(redirectUri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String requestUrl = code_url.replace("APPID", appid)
                .replace("REDIRECT_URI", redirect).replace("SCOPE", scope)
                .replace("STATE", state);

//        try {
//            requestUrl = URLEncoder.encode(requestUrl, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        return requestUrl;
    }
}
