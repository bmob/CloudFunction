public static void onRequest(final Request request, final Response response, final Modules modules) throws Throwable {
	final int Version = 1; // vue项目版本号
	// index.html文件需要包含 <!-- VERSION[Version] -->
	// 如版本为1时，包含 <!-- VERSION[1] -->
	
	boolean isGZiped = true; // 是否已将.css、.js、.html文件进行过gzip压缩
	// 数据库中，项目对应的表名和下载地址、版本号的字段名
	final String ProjectTable = "WebProject", DownloadAttrName = "project", VersionAttrName = "version";

	String path = request.getPath(); // 获取请求地址
	// 避免后面参数的影响
	int index = path.indexOf("?");
	if (index != -1)
		path = path.substring(0, index);
	if (path.length() == 17) {
		response.send("Go homepage", 302, "Redirect",
				JSON.toJson("Location", path + "/homepage"));
		return;
	}

	if (path.length() > 16
			&& path.substring(0, 17).matches("/[0-9a-f]{16}"))
		path = path.substring(17);

	// 判断是否为首页
	final boolean isHomepage = path.equals("/homepage");
	if (isHomepage) {
		path = "index.html"; // vue首页html文件名
		isGZiped = false;
	}

	// 获取持久化项
	PersistenceItem perItem = modules.oPersistence == null ? null
			: modules.oPersistence.get(path);
	if (perItem == null) { // 本应用不支持持久化，或者无法读写
		response.send("Cannot load project, sorry.", 404, "NOTFOUND");
		return;
	}
	// 读取持久化内容
	long size = perItem.size();
	byte[] body = new byte[(int) size];
	if (size != 0 && !perItem.read(body)) { // 读取失败
		response.send("Cannot read project, sorry.", 404, "NOTFOUND");
		return;
	}

	// 如果是首页，顺便看看云端保存的项目是否最新版
	if (size == 0
			|| isHomepage
			&& !new String(body).contains("<!-- VERSION[" + Version
					+ "] -->")) {
		HttpResponse res = modules.oData.find(new Querier(ProjectTable)
				.addWhereEqualTo(VersionAttrName, Version)
				.order("-createdAt").limit(1));
		if (res.queryResults == null) { // 查询失败，可能是没建表
			response.send("Cannot find in bmob:" + res.stringData, 404,
					"NOTFOUND");
			return;
		}
		if (res.queryResults.size() == 0) { // 没有这个版本号的项目
			response.send("No project match version:" + Version, 404,
					"NOTFOUND");
			return;
		}
		// 获取项目下载地址，必须以.zip为后缀
		String url = null;
		try {
			url = res.queryResults.getJSONObject(0)
					.getJSONObject(DownloadAttrName).getString("url");
			if (url == null || !url.endsWith(".zip"))
				throw new NullPointerException();
		} catch (Throwable e) {
			response.send("No project to download:" + Version, 404,
					"NOTFOUND");
			return;
		}
		res = modules.oHttp.get(url);
		if (res.res.code != 200)
			response.send("Failed to download:" + url, 404, "NOTFOUND");// 下载失败
		else {
			modules.oPersistence.clean();

			if (modules.oPersistence.unzip(res.data))
				response.send(
						"<html><script type='text/javascript'>setTimeout(function(){window.location.reload();},1000);</script><body>正在升级中，请稍等...</body></html>",
						200, "OK", JSON.toJson(ZConstant.Header_CType,
								"text/html; charset=utf-8")); // 下载并解压成功，刷新网页
			else
				response.send("Failed to unzip:" + url, 404, "NOTFOUND"); // 解压失败
		}

		return;
	}

	// 根据文件类型，确定Content-Type
	JSONObject responseHeaders = new JSONObject();
	String cType = "text/html";
	if (path.endsWith(".css"))
		cType = "text/css";
	else if (path.endsWith(".js"))
		cType = "application/javascript";
	else if (!path.endsWith(".html"))
		isGZiped = false; // 其它类型，没有进行gzip压缩
	responseHeaders.put(ZConstant.Header_CType, cType + "; charset=utf-8");
	if (isGZiped)
		responseHeaders.put("Content-Encoding", "gzip"); // 如果是已压缩的

	response.send(body, 200, "OK", responseHeaders);
}