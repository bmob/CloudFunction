import cn.bmob.javacloud.stub.*;

public class wxhello extends CloudHandler {

	@Override
	public void onRequest(Request request, Response response, Modules modules)
			throws Throwable {

		// 需结合wxcontact保存到Feedback表的逻辑使用

		String htmlContent = "<!DOCTYPE html><html><head><meta name='viewport' content='width=device-width, initial-scale=1.0, user-scalable=no'><meta http-equiv='Content-Type' content='text/html;charset=utf-8'><title>小程序工具</title></head><body><p>你在这里输入的内容，会发送给全部一天内在小程序客服里留过言的用户</p><p>请不要使用已上线小程序，以免对用户造成干扰</p><form action='?' method='POST'><p>消息内容: </p><p><input type='text' name='text' value=''></p> <input type='submit' value='提交'/> </form><p>----------------------------------------------------------------</p><div>";

		if ("POST".equals(request.getMethod())) {
			final String msg = request.getParams().getString("text");
			if (isStrEmpty(msg))
				htmlContent += "没有输入需要发送的内容";
			else {
				HttpResponse result = modules.oData
						.find(new Querier("Feedback")
								.keys("user")
								.groupby("user")
								.limit(1000)
								.addWhereGreaterThanOrEqualTo(
										"createdAt",
										new BmobDate(getTime()
												- (1000l * 60 * 60 * 24))));
				if (result.queryResults == null)
					htmlContent += "查询失败:" + result.stringData;
				else {
					JSONArray feedbacks = result.queryResults;
					int userCount = feedbacks.size();
					if (userCount == 0)
						htmlContent += "24小时内没有活跃用户";
					else {
						int succeedCount = 0;
						for (int i = 0; i < userCount; i++) {
							result = modules.oWechat.sendWechatAppMsg(feedbacks
									.getJSONObject(i).getString("user"),
									"text", msg);
							switch (result.jsonData.getIntValue("errcode")) {
							case 0:
								succeedCount++;
								continue;
							case 40001:
								htmlContent += fmt("<p>Access_Token错误: %s</p>",
										modules.oWechat.getAccessToken());
								break;
							case 40003:
								htmlContent += fmt(
										"<p>出现了不属于这个小程序的用户id: %s</p>",
										feedbacks.getJSONObject(i).getString(
												"user"));
								break;
							case 45015:
								htmlContent += "<p>对该用户的回复时间超过限制</p>";
								break;
							case 45047:
								htmlContent += "<p>客服接口下行条数超过上限</p>";
								break;
							}
							htmlContent += fmt("<p>发送给用户[%s]失败: %s</p>",
									feedbacks.getJSONObject(i),
									result.stringData);
						}
						htmlContent += fmt("<p>发送结果: 成功/总数 = %d/%d</p>",
								succeedCount, userCount);
					}
				}
			}
		}
		response.send(htmlContent + "</div></body></html>", 200, null,
				JSON.toJson("Content-Type", "text/html"));

	}
}
