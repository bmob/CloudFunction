import cn.bmob.javacloud.stub.*;

public class sms extends CloudHandler {

	@Override
	public void onRequest(Request request, Response response, Modules modules)
			throws Throwable {

		String mobile = request.getParams().getString("mobile");
		if (isStrEmpty(mobile))
			response.send(JSON.toJson("code", 400, "error", "没有手机号"));
		else {
			HttpResponse res = modules.oData.sendSMSCode(mobile, "register");// 需要在Bmob后台创建名为"register"的模版信息
			Integer smsId = res.jsonData.getInteger("smsId");
			if (smsId == null)
				response.send(JSON.toJson("code", 401, "error", "发送失败:"
						+ res.stringData));
			else
				response.send(JSON.toJson("code", 200, "data", smsId));
		}
	}
}
