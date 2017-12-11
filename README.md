# Javacloud

Bmob平台Java云端代码

## Installation

1. 下载本项目内以下文件到本地:

		 /libs/Bmob-JavaCloud-Apis_xxx.jar
		 /exec/[goos]/*
		 
2. 在IDE(如eclipse)内创建java项目，并将jar文件添加到项目依赖项
3. 创建一个类(例子为Test1)，并添加以下代码

		import cn.bmob.javacloud.stub.*;
		// import的内容不能变动，如果你写的云代码引用了一些Java自带类，请写类全名，不要添加import

		public class Test1 extends CloudHandler {
		
			@Override
			public void onRequest(Request request, Response response, Modules modules) throws Throwable {
				response.send("Hello world! -- Bmob Java Cloud");
			}
		
		}


4. 运行从 **/exec/[goos]/** 下载的可执行文件(下称$bmobjc)

		方式1
			$bmobjc [masterKey] [fworkSpace]
		方式2
			在运行目录下创建 "bmobkeys" 文件，并将masterKey和workSpace以json形式保存
			然后运行 '$bmobjc'
		方式3
			直接运行 '$bmobjc', 并按提示输入keys
			
5. 待代码同步完成后，可以通过以下方式访问：


	- 直接请求链接
	
		
			http://javacloud.bmob.cn/[secret key]/[cloudname]
		
	- 通过Bmob的key进行访问


			curl -X POST \
			    -H "X-Bmob-Application-Id: Your Application ID" \
			    -H "X-Bmob-REST-API-Key: Your REST API Key" \
			    -H "Content-Type: application/json" \
			    -d '{"name":"test"}' \
			    http://javacloud.bmob.cn/1/functions/[cloudname]
			    
		    
6. 查看日志

	附加的参数有：
	
	-	start	*开始时间, yyyy-MM-dd HH:mm:ss格式
	-	end		*结束时间
	-	key		搜索关键字(可用正则)
	-	fun		搜索方法名
	-	level	日志级别(N/D/W/E)
	-	limit	限制条数(默认100，最高100)
	-	skip	跳过行数
		
		
			curl -X GET \
			    -H "X-Bmob-Application-Id: Your Application ID" \
			    -H "X-Bmob-REST-API-Key: Your REST API Key" \
			    http://javacloudapi.antibrush.com/logs/[params...]
		    
	例如，搜索2018年10月19号一整天的，test1方法的Error级别日志
	
	
		curl -X GET \
		    -H "X-Bmob-Application-Id: Your Application ID" \
		    -H "X-Bmob-REST-API-Key: Your REST API Key" \
		    http://javacloudapi.antibrush.com/logs/?start=2018-10-19%2000%3A00%3A00&end=2018-10-20%2000%3A00%3A00&level=e
		    