# BmobJava云函数-Vue

*只需要云函数服务，就可以一键部署你的vue项目*

[示例网站](//javacloud.bmob.cn/f7693b7e98a35ed6)

## 发布步骤

*以下指令操作是基于Linux、Mac系统，windows系统请自行修改文件分隔符*


1. 编写vue项目

2. 修改 `config/index.js` 的 `assetsPublicPath` 为 *./* （默认为 `/`）

3. `npm run build`

4. 修改 `./dist/index.html`，加入 `<-- VERSION[1] -->`

5. 在vue根目录调用 `python tools/gzip_bmobjavacloud.py ./dist` 进行Gzip预处理

6. 在dist目录下压缩项目，调用 `zip -r dist.zip ./`

7. 登陆bmob官网后台，创建一个云函数，语言为**Java**，函数名为`NotFound`（大小写不敏感）

8. 将 [NotFound示例](//github.com/bmob/CloudFunction/tree/master/vue/NotFound.java) 内的内容复制进去，点击保存

9. 在Bmob数据库创建表，名为`WebProject`，并新建字段`version`(Number类型)、`project`(File类型)

10. 新增一行WebProkect数据，version为`1`，project点击上传步骤6声称的zip文件

11. 打开 `https://javacloud.bmob.cn/[secret key]` 即可看到你的vue项目

## 注意事项

- 请勿在项目内放置大文件例如高清图片等，云函数有一些限制
- 如果对目录结构、更新文件有兴趣，请详细阅读[NotFound示例](//github.com/bmob/CloudFunction/tree/master/vue/NotFound.java)中的代码
- 如果vue项目较为大型，可联系客服升级云函数配置
- Vue示例代码来自[//github.com/PanJiaChen/vue-admin-template](//github.com/PanJiaChen/vue-admin-template)，感谢作者的无私奉献
- 该功能尚在测试阶段，如果需要正式上线，请联系客服