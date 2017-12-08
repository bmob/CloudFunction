import cn.bmob.javacloud.stub.*;

public class register extends CloudHandler {

	@Override
	public void onRequest(Request request, Response response, Modules modules)
			throws Throwable {

		JSONObject params = request.getParams();
		String mobile = params.getString("mobile");
		String smsCode = params.getString("smsCode");
		String avatar = params.getString("avatar");
		String password = params.getString("password");

		int resCode = 500;
		String resError = null;
		JSONObject resJson = null;

		if (isStrEmpty(mobile)) {
			resCode = 400;
			resError = "没有手机号";
		} else if (isStrEmpty(smsCode)) {
			resCode = 401;
			resError = "请输入短信验证码";
		} else if (password == null || password.length() < 6) {
			resCode = 402;
			resError = "密码不能少于6位";
		} else {
			HttpResponse res = modules.oData.userLoginBySMS(mobile, smsCode,
					JSON.toJson("username", mobile, "avatar", avatar,
							"password", password));
			if (isStrEmpty(res.jsonData.getString("objectId"))) {
				resCode = 404;
				resError = "注册失败:" + res.stringData;
			} else {
				resCode = 200;
				resJson = res.jsonData;
			}
		}

		if (resCode == 200)
			response.send(JSON.toJson("code", resCode, "data", resJson));
		else
			response.send(JSON.toJson("code", resCode, "error", resError));

	}
}
