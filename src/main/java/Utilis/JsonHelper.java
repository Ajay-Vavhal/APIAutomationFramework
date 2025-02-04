package Utilis;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHelper {

	private static String apiName; 
	private static String jsonFileName; 
	private static Map<String, Object> apiRequest;
	private static JsonNode jsonObject;
	protected static ObjectMapper objectMapper = new ObjectMapper();

	public static String getApiName() {
		return apiName;
	}
	
	public static void setApiName(String apiName) {
		JsonHelper.apiName = apiName;
	}
	
	public static Map<String, Object> getApiRequest() {
		return apiRequest;
	}
	
	public static void setApiRequest(Map<String, Object> apiRequest) {
		JsonHelper.apiRequest = apiRequest;
	}

	public static String getJsonFileName() {
		return jsonFileName;
	}

	public static void setJsonFileName(String jsonFileName) {
		JsonHelper.jsonFileName = jsonFileName;
	}

	public static JsonNode getJsonObject() {
		return jsonObject;
	}

	public static void setJsonObject(JsonNode jsonObject) {
		JsonHelper.jsonObject = jsonObject;
	}
}