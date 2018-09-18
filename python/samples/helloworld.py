def onRequest(request, response, modules):
	name = request.getQueryParam("n")
	if name == None:
		name = "Guest"
	response.send("Hello world, " + name + " -- from bmob python cloud designed by bmob")

