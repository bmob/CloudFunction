import cn.bmob.javacloud.stub.*

public class helloworld extends CloudHandler {

	@Override
	public void onRequest(Request request, Response response, Modules modules)
			throws Throwable {
		
		response.send("Hello world -- BmobJavaCloud");

	}

}
