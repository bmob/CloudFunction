package cn.bmob.javacloud.test;

import cn.bmob.javacloud.stub.*;

public class encrypt extends CloudHandler {

	@Override
	public void onRequest(Request request, Response response, Modules modules)
			throws Throwable {

		String htmlContent = "<!DOCTYPE html><html><head><meta http-equiv='Content-Type' content='text/html;charset=utf-8'><title>加密测试</title></head><body><form action='?' method='POST'><p>原文: </p><p><input type='text' name='clear' value=''> <select name='clear_type'><option value='default'>明文</option><option value='base64'>Base64值</option><option value='hex'>Hex值</option></select></p><p>密文(与原文同时存在时,密文的输入无效):</p><p> <input type='text' name='cipher' value=''> <select name='cipher_type'><option value='default'>明文</option><option value='base64'>Base64值</option><option value='hex'>Hex值</option></select></p><p>密钥: <input type='text' name='key' value='' placeholder='Base64值,缺省=16个0'></p><p>初始化向量: <input type='text' name='iv' value='' placeholder='Base64值,缺省=密钥'></p><input type='submit' value='提交'/> </form>";

		if (request.getMethod().equals("POST"))
			try {
				JSONObject params = request.getParams();
				String clear_text = params.getString("clear");
				String cipher_text = params.getString("cipher");
				String clear_type = params.getString("clear_type");
				String cipher_type = params.getString("cipher_type");
				String key_base64 = params.getString("key");
				String iv_base64 = params.getString("iv");

				byte[] key_bytes = Base64.Decode(key_base64);
				byte[] iv_bytes = Base64.Decode(iv_base64);
				if (key_bytes.length != 16)
					key_bytes = new byte[16];
				if (iv_bytes.length != 16)
					iv_bytes = key_bytes;

				byte[] clear_bytes = null, cipher_bytes = null;

				boolean isEncode = !isStrEmpty(clear_text)
						|| isStrEmpty(cipher_text);

				try {
					if (isEncode) {
						if ("base64".equals(clear_type))
							clear_bytes = Base64.Decode(clear_text);
						else if ("hex".equals(clear_type)) {
							byte[] hexBytes = clear_text.getBytes();
							clear_bytes = new byte[hexBytes.length >> 1];
							for (int i = 0; i < hexBytes.length; i += 2)
								clear_bytes[i >> 1] = Byte.parseByte(
										new String(hexBytes, i, 2), 16);
						} else
							clear_bytes = clear_text.getBytes();
						cipher_bytes = AES.Encode(clear_bytes, key_bytes,
								iv_bytes);
					} else {
						if ("base64".equals(cipher_type))
							cipher_bytes = Base64.Decode(cipher_text);
						else if ("hex".equals(clear_type)) {
							byte[] hexBytes = cipher_text.getBytes();
							cipher_bytes = new byte[hexBytes.length >> 1];
							for (int i = 0; i < hexBytes.length; i += 2)
								cipher_bytes[i >> 1] = Byte.parseByte(
										new String(hexBytes, i, 2), 16);
						} else
							cipher_bytes = cipher_text.getBytes();
						clear_bytes = AES.Decode(cipher_bytes, key_bytes,
								iv_bytes);
					}
				} catch (Throwable e) {
				}

				String clear_base64 = clear_bytes == null ? "Error" : Base64
						.Encode(clear_bytes);
				String cipher_base64 = cipher_bytes == null ? "Error" : Base64
						.Encode(cipher_bytes);
				String clear_md5 = clear_bytes == null ? "Error" : MD5
						.Encode(clear_bytes);
				String clear_sha1 = clear_bytes == null ? "Error" : SHA1
						.Encode(clear_bytes);
				String clear_gzip = clear_bytes == null ? "Error" : Base64
						.Encode(GZip.Encode(clear_bytes));

				htmlContent += fmt(
						"<p>----------------------------------------------------------------</p><p>原文Base64: %s</p><p>密文Base64: %s</p><p>原文MD5: %s</p><p>原文SHA1: %s</p><p>原文GZip(Base64): %s</p>",
						clear_base64, cipher_base64, clear_md5, clear_sha1,
						clear_gzip);

			} catch (Throwable e) {
				htmlContent += "<p>出现异常:" + e + "</p>";
			}

		response.send(htmlContent + "</body></html>", 200, null,
				JSON.toJson("Content-Type", "text/html"));
	}
}
