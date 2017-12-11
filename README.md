# Javacloud

Bmob平台Java云函数

本篇文档主要将如何通过非Web的方式同步云函数

详细编写、使用云函数的方法请参考[详细文档](https://github.com/bmob/BmobJavaCloud/blob/master/doc/Java%E4%BA%91%E5%87%BD%E6%95%B0-%E5%AF%B9%E5%A4%96.md)


## Installation

1. 下载本项目内以下文件到本地:

		 /libs/Bmob-JavaCloud-Apis_xxx.jar
		 /exec/[goos]/*
		 
		 如，如果你用Windows开发，需要下载
		 /exec/windows_x64/bmobjc.exe
		 
2. 在IDE(如eclipse)内创建java项目，并将jar文件添加到项目依赖项
3. 创建一个类(例子为Test1)，并添加以下代码

		import cn.bmob.javacloud.stub.*;
		// import的内容不能变动，如果你写的云函数引用了一些Java自带类，请写类全名，不要添加import

		public class Test1 extends CloudHandler {
		
			@Override
			public void onRequest(Request request, Response response, Modules modules) throws Throwable {
				response.send("Hello world! -- Bmob Java Cloud");
			}
		
		}


4. 运行从 **/exec/[goos]/** 下载的可执行文件(下称$bmobjc)

		方式1
			$bmobjc [masterKey] [workSpace]
		方式2
			在运行目录下创建 "bmobkeys" 文件，并将masterKey和workSpace以json形式保存
			然后运行 '$bmobjc'
		方式3
			直接运行 '$bmobjc', 并按提示输入masterKey和workSpace
			
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
			    
	- 通过SDK请求(请参考 [详细文档](https://github.com/bmob/BmobJavaCloud/blob/master/doc/Java%E4%BA%91%E5%87%BD%E6%95%B0-%E5%AF%B9%E5%A4%96.md))

