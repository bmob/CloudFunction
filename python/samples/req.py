def onRequest(request, response, modules):
	response.send({
		"urlParams": request.getQueryParams(),
		"bodyParams": request.getParams(),
		"headers": request.getHeaders(),
		"method": request.getMethod(),
		"path": request.getPath()
	})

