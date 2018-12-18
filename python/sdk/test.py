#coding=utf-8

# 导入bmob模块
from bmob import *
# 新建一个bmob操作对象
b = Bmob("appid", "restkey")
# 插入一行数据，原子计数、Pointer
print(
	b.insert(
		'Feedback', # 表名
		BmobUpdater.increment(
			"count", # 原子计数key
			2, # 原子计数值
			{ # 额外信息
				"content": "测试python",
				"user": BmobPointer("_User", "xxx"), # Pointer类型
				"date": BmobDate(1545098009351) ## Date类型
			}
		)
	).jsonData # 输出json格式的内容
)

print(
	b.find( # 查找数据库
		"Feedback", # 表名
		BmobQuerier(). # 新建查询逻辑
			addWhereNotExists("user") # user不存在
		).stringData # 输出string格式的内容
)
