import cn.bmob.javacloud.stub.*;

public class req extends cn.bmob.javacloud.stub.CloudHandler {

	@Override
	public void onRequest(Request request, Response response, Modules modules)
			throws Throwable {

		response.send(
				fmt("请求的链接: %s\n请求方法: %s\nQuery参数: %s\nBody参数: %s\n请求体大小: %s\n请求头: %s\n时间: %s\n",
						request.getPath(),// 请求路径
						request.getMethod(),// 请求方法
						request.getQueryParams(),// 请求Get参数
						request.getParams(),// 请求Post/Put参数
						request.getBody().length,// 请求体大小
						request.getHeaders(),// 请求头
						dateFormat.format(new java.util.Date())// 当前时间戳
				), 200, "OK", JSON.toJson("Content-Type",
						"text/plain; charset=UTF-8"));// 返回中文，需要设置编码

	}

}