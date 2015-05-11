package com.boqu.wechat.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.boqu.wechat.resp.Article;
import com.boqu.wechat.resp.NewsMessage;
import com.boqu.wechat.resp.TextMessage;
import com.boqu.wechat.util.MessageUtil;

public class CoreService {

    /**
     * 处理微信发来的请求
     * 
     * @param request
     * @return
     */
    public static String processRequest(HttpServletRequest request) {
        String respMessage = null;
        try {
            // 默认返回的文本消息内容
            String respContent = "小嘉嘉正在犯困中，请稍候再来！";

            // xml请求解析
            Map<String, String> requestMap = MessageUtil.parseXml(request);

            // 发送方帐号（open_id）
            String fromUserName = requestMap.get("FromUserName");
            // 公众帐号
            String toUserName = requestMap.get("ToUserName");
            // 消息类型
            String msgType = requestMap.get("MsgType");

            // 回复默认文本消息
            TextMessage textMessage = new TextMessage();
            textMessage.setToUserName(fromUserName);
            textMessage.setFromUserName(toUserName);
            textMessage.setCreateTime(new Date().getTime());
            textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
            textMessage.setFuncFlag(0);
            textMessage.setContent(getAutoReplyMenu());
            respMessage = MessageUtil.textMessageToXml(textMessage);

            // 文本消息
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
                // 接收用户发送的文本消息内容
                String content = requestMap.get("Content");

                // 创建图文消息
                NewsMessage newsMessage = new NewsMessage();
                newsMessage.setToUserName(fromUserName);
                newsMessage.setFromUserName(toUserName);
                newsMessage.setCreateTime(new Date().getTime());
                newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
                newsMessage.setFuncFlag(0);

                List<Article> articleList = new ArrayList<Article>();
                // 单图文消息
                if ("1".equals(content)) {
                    Article article = new Article();
                    article.setTitle("嘉实财富简介");
                    article.setDescription("嘉实财富管理有限公司成立于2012年3月，由嘉实基金管理有限公司投资组建，是国内首家基金公司设立的财富管理机构！");
                    article.setPicUrl("http://static.pkjiao.com/sysbackground/6.jpg");
                    article.setUrl("http://182.92.222.185:3000/cubicdetail.html");
                    articleList.add(article);
                    // 设置图文消息个数
                    newsMessage.setArticleCount(articleList.size());
                    // 设置图文消息包含的图文集合
                    newsMessage.setArticles(articleList);
                    // 将图文消息对象转换成xml字符串
                    respMessage = MessageUtil.newsMessageToXml(newsMessage);
                }
                // 单图文消息---不含图片
                else if ("2".equals(content)) {
                    Article article = new Article();
                    article.setTitle("嘉实财富简介");
                    // 图文消息中可以使用QQ表情、符号表情
                    article.setDescription("嘉实财富"
                            + emoji(0x1F6B9)
                            + "，截至2013年6月30日，资产管理规模近3000亿元，其中公募基金资产规模为1704亿元，居行业第二。");
                    // 将图片置为空
                    article.setPicUrl("");
                    article.setUrl("http://182.92.222.185:3000/cubicdetail.html");
                    articleList.add(article);
                    newsMessage.setArticleCount(articleList.size());
                    newsMessage.setArticles(articleList);
                    respMessage = MessageUtil.newsMessageToXml(newsMessage);
                }
                // 多图文消息
                else if ("3".equals(content)) {
                    Article article1 = new Article();
                    article1.setTitle("嘉实产品列表");
                    article1.setDescription("");
                    article1.setPicUrl("http://static.pkjiao.com/sysbackground/6.jpg");
                    article1.setUrl("http://182.92.222.185:3000/productitem.html");

                    Article article2 = new Article();
                    article2.setTitle("第2篇\n嘉实产品详情");
                    article2.setDescription("");
                    article2.setPicUrl("http://static.pkjiao.com/165x165/avatar/1065.jpg");
                    article2.setUrl("http://182.92.222.185:3000/productitemdetail.html");

                    Article article3 = new Article();
                    article3.setTitle("第3篇\n投顾观点详情");
                    article3.setDescription("");
                    article3.setPicUrl("http://static.pkjiao.com/165x165/avatar/1065.jpg");
                    article3.setUrl("http://182.92.222.185:3000/viewpointdetail.html");

                    articleList.add(article1);
                    articleList.add(article2);
                    articleList.add(article3);
                    newsMessage.setArticleCount(articleList.size());
                    newsMessage.setArticles(articleList);
                    respMessage = MessageUtil.newsMessageToXml(newsMessage);
                }
                // 多图文消息---首条消息不含图片
                else if ("4".equals(content)) {
                    Article article1 = new Article();
                    article1.setTitle("嘉实财富");
                    article1.setDescription("");
                    // 将图片置为空
                    article1.setPicUrl("");
                    article1.setUrl("http://182.92.222.185:3000/cubicdetail.html");

                    Article article2 = new Article();
                    article2.setTitle("第4篇\n嘉实可投资产品列表");
                    article2.setDescription("");
                    article2.setPicUrl("http://static.pkjiao.com/165x165/avatar/1065.jpg");
                    article2.setUrl("http://182.92.222.185:3000/product.html");

                    Article article3 = new Article();
                    article3.setTitle("第5篇\n嘉实工作室产品列表");
                    article3.setDescription("");
                    article3.setPicUrl("http://static.pkjiao.com/165x165/avatar/1065.jpg");
                    article3.setUrl("http://182.92.222.185:3000/cubic.html");

                    Article article4 = new Article();
                    article4.setTitle("第6篇\n嘉实投顾列表");
                    article4.setDescription("");
                    article4.setPicUrl("http://static.pkjiao.com/165x165/avatar/1065.jpg");
                    article4.setUrl("http://182.92.222.185:3000/invester.html");

                    articleList.add(article1);
                    articleList.add(article2);
                    articleList.add(article3);
                    articleList.add(article4);
                    newsMessage.setArticleCount(articleList.size());
                    newsMessage.setArticles(articleList);
                    respMessage = MessageUtil.newsMessageToXml(newsMessage);
                }
                // 多图文消息---最后一条消息不含图片
                else if ("5".equals(content)) {
                    Article article1 = new Article();
                    article1.setTitle("第7篇\n嘉实投顾介绍");
                    article1.setDescription("");
                    article1.setPicUrl("http://static.pkjiao.com/165x165/avatar/1065.jpg");
                    article1.setUrl("http://182.92.222.185:3000/investerdetail.html");

                    Article article2 = new Article();
                    article2.setTitle("第8篇\n嘉实投顾观点详情");
                    article2.setDescription("");
                    article2.setPicUrl("http://static.pkjiao.com/165x165/avatar/1065.jpg");
                    article2.setUrl("http://182.92.222.185:3000/viewpoint.html");

                    Article article3 = new Article();
                    article3.setTitle("如果觉得文章对你有所帮助，请通过我们点赞！");
                    article3.setDescription("");
                    // 将图片置为空
                    article3.setPicUrl("");
                    article3.setUrl("http://182.92.222.185:3000/cubicdetail.html");

                    articleList.add(article1);
                    articleList.add(article2);
                    articleList.add(article3);
                    newsMessage.setArticleCount(articleList.size());
                    newsMessage.setArticles(articleList);
                    respMessage = MessageUtil.newsMessageToXml(newsMessage);
                }
            }
            // 图片消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {
                respContent = "您发送的是图片消息，很抱歉我们目前只提供文本服务，谢谢你的支持！";
                textMessage.setContent(respContent);
                respMessage = MessageUtil.textMessageToXml(textMessage);
            }
            // 地理位置消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {
                respContent = "您发送的是地理位置消息，很抱歉我们目前只提供文本服务，谢谢你的支持！";
                textMessage.setContent(respContent);
                respMessage = MessageUtil.textMessageToXml(textMessage);
            }
            // 链接消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {
                respContent = "您发送的是链接消息，很抱歉我们目前只提供文本服务，谢谢你的支持！";
                textMessage.setContent(respContent);
                respMessage = MessageUtil.textMessageToXml(textMessage);
            }
            // 音频消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
                respContent = "谢谢你的关注，很抱歉我们目前只提供文本服务，谢谢你的支持。";
                textMessage.setContent(respContent);
                respMessage = MessageUtil.textMessageToXml(textMessage);
            }
            // 事件推送
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
                // 事件类型
                String eventType = requestMap.get("Event");
                // 订阅
                if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
                    respContent = "谢谢关注小嘉，谢谢你的支持！";
                    textMessage.setContent(respContent);
                    respMessage = MessageUtil.textMessageToXml(textMessage);
                }
                // 取消订阅
                else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
                    // TODO 取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息
                }
                // 自定义菜单点击事件
                else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {
                    // TODO 自定义菜单权没有开放，暂不处理该类消息
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return respMessage;
    }

    public static String getAutoReplyMenu() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("您好，我是小嘉嘉，请回复数字选择服务：").append("\n\n");
        buffer.append("1  股市预报").append("\n");
        buffer.append("2  股票查询").append("\n");
        buffer.append("3  股票搜索").append("\n");
        buffer.append("4  股票诊断").append("\n");
        buffer.append("5  股票推荐" + emoji(0x1F4B0)).append("\n"); // for unified
                                                                // emoji
        buffer.append("6  股票电台").append("\n");
        buffer.append("7  股票识别").append("\n");
        buffer.append("8  聊天唠嗑\ue111 \ue11b").append("\n\n"); // for softbank
                                                              // emoji
        buffer.append("回复“?”显示此帮助菜单");
        return buffer.toString();
    }

    /**
     * emoji表情转换(hex -> utf-16)
     * 
     * @param hexEmoji
     * @return
     */
    public static String emoji(int hexEmoji) {
        return String.valueOf(Character.toChars(hexEmoji));
    }
}
