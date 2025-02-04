package Utilis;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import pageObjectModel.ApiRequest;

public class JsonUtil {

	@SuppressWarnings("unchecked")
	public static Map<String, Object> readJsonFile(String filePath) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonHelper.setJsonObject(objectMapper.readTree(new File(filePath)));
		return objectMapper.readValue(new File(filePath), Map.class);

	}
	
	public static ApiRequest readJsonFile1(String filePath) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonHelper.setJsonObject(objectMapper.readTree(new File(filePath)));
		return objectMapper.readValue(new File(filePath), ApiRequest.class);

	}
}
