import cn.bmob.javacloud.stub.*;

public class paynotify extends CloudHandler {

	@Override
	public void onRequest(Request request, Response response, Modules modules)
			throws Throwable {

		// 参数详见 http://doc.bmob.cn/pay/android/#_10 > 支付回调
		String orderId = request.getParams().getString("out_trade_no");
		if (isStrEmpty(orderId))
			response.send("无订单号");
		else {
			HttpResponse res = modules.oData.payQuery(orderId);
			if ("SUCCESS".equals(res.jsonData.getString("trade_state"))) {
				// 这里可以查询Bmob数据库，看看订单是否已经处理，再进行余额增加、添加会员身份等操作
				response.send("已支付的订单，金额："
						+ res.jsonData.getDoubleValue("total_fee"));
			} else {
				response.send("查询失败或未支付");
			}
		}

	}

}