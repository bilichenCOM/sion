package utils;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public class JsonParser {
	private static final Logger LOGGER = Logger.getLogger(JsonParser.class);

	public static JSONObject parseJson(HttpServletRequest request) throws IOException {
		StringBuilder requestBody = new StringBuilder();
		BufferedReader reader = request.getReader();
		String line = "";
		while((line = reader.readLine()) != null) {
			requestBody.append(line);
		}
		LOGGER.debug("jsonString: " + requestBody);
		return new JSONObject(requestBody.toString());
	}
}