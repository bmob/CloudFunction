def onRequest(request, response, modules):
	age = request.getQueryParam("age")
	if age == None:
		response.send(modules.oData.find("_User").stringData)
	else:
		response.send(modules.oData.find("_User", where = BmobQuerier().addWhereGreaterThan("age", int(age))).stringData)	

