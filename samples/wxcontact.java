import cn.bmob.javacloud.stub.*;

public class wxcontact extends CloudHandler {

	@Override
	public void onRequest(Request request, Response response, Modules modules)
			throws Throwable {

		// 本示例代码需要改动的地方从这里开始：
		// 以下信息均在 [微信公众平台 > 小程序管理页面 > 设置 > 开发设置]
		final String Token = null;// [开发设置 > 消息推送 > Token]
		// 推荐[登录Bmob > 设置 > 应用配置 > 微信小程序帐号服务配置] 配置以下两个参数，免除管理AccessToken生命周期的烦恼
		// 如果在Bmob后台配置了小程序的appid、appsecret，请不要再复制到这里，否则AccessToken冲突会影响你的小程序正常运作
		// [开发设置 > 开发者ID]
		final String WechatAppId = null, WechatAppSecret = null;
		if (WechatAppId != null)
			modules.oWechat.initWechatApp(WechatAppId, WechatAppSecret);
		// 本示例代码需要改动的地方到这里结束

		// SHA1验证是否从微信发送的请求
		if (modules.oWechat.isWechatRequest(Token, request)) {
			if ("GET".equals(request.getMethod())) { // 如果是GET请求，是微信验证Url，直接返回echostr参数即可
				response.send(request.getQueryParam("echostr"));
				return;
			}
		} else { // 请求合法性验证失败
			response.send("Error");
			return;
		}
		// 到这里的是合法的、Method不为GET的请求，参数都在Post Body里面
		final JSONObject postParams = request.getParams();
		// 获取参数里面的用户名、信息类型、文字内容、图片网址、事件类型
		final String FromUserName = postParams.getString("FromUserName"), Content = postParams
				.getString("Content"), PicUrl = postParams.getString("PicUrl"), MsgType = postParams
				.getString("MsgType"), Event = postParams.getString("Event");
		// 构建保存到Bmob数据库Feedback表的数据内容
		JSONObject feedback = JSON.toJson(//
				"record", JSON.stringify(JSON.toJson("post", postParams,
						"query", request.getQueryParams(), "path",
						request.getPath(), "method", request.getMethod())),// 所有请求信息
				"title", "wechat-contact", // Feedback类型
				"user", FromUserName // 用户id，保存起来可以主动调用回复信息方法
				);
		String responseMsg; // 即将回复给用户的文字信息
		if ("image".equals(MsgType)) { // 用户发送了图片类型
			JSON.setJson(feedback, "type", 2, "content", PicUrl); // Feedback表的内容追加
			responseMsg = "谢谢，已经收到了您发送的图片";
		} else if ("event".equals(MsgType)) { // 一般情况下，event类型是用户点击了小程序的联系客服
			JSON.setJson(feedback, "type", 3, "content", Event);
			responseMsg = "嗨，您好，有任何反馈意见可以直接在这里反馈哦";
		} else if ("text".equals(MsgType)) { // 用户发送了文字信息
			JSON.setJson(feedback, "type", 1, "content", Content);
			responseMsg = "感谢您的留言，已将留言递交给人工，共" + Content.length() + "字";
		} else { // 没有见过的信息类型
			JSON.setJson(feedback, "type", 4);
			responseMsg = "您好，暂时不支持该种类反馈，已通知人工客服";
		}
		// 调用oWechat的方法，将回复内容主动回复给用户
		HttpResponse res = modules.oWechat.sendWechatAppMsg(FromUserName,
				"text", responseMsg);
		JSON.setJson(feedback, "responseRes", res.stringData); // 保存回复结果到Feedback表
		// 保存Feedback表的内容，并根据保存成功与否，返回字符串给微信(其实返回什么好像没区别)
		response.send(isStrEmpty(modules.oData.insert("Feedback", feedback).jsonData
				.getString("objectId")) ? "fail" : "success");
	}
}
