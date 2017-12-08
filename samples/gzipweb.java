import cn.bmob.javacloud.stub.*;

public class gzipweb extends CloudHandler {

	@Override
	public void onRequest(Request request, Response response, Modules modules)
			throws Throwable {
		String html = "<html><body><p><input type=\"text\"></input></p><p>Hello world -- BmobJavaCloud</p></body></html>";
		response.send(GZip.Encode(html.getBytes(ZConstant.UTF8)), 200, null,
				JSON.toJson("Content-Encoding", "gzip", "Content-Type",
						"text/html;charset=utf-8"));
	}

}