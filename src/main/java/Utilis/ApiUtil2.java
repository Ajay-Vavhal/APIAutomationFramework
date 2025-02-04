package Utilis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pageObjectModel.ApiRequest;

public class ApiUtil2 {

	private static final String BASE_PATH = "src/test/resources/api_request/";

	@SuppressWarnings("unchecked")
	public static Response sendRequest(String apiCall,String fileName) throws IOException {
		JsonHelper.setApiName(apiCall);
		JsonHelper.setJsonFileName(fileName);
		ApiRequest apiRequest = JsonUtil.readJsonFile1(BASE_PATH + fileName);
		String[] apiMethod = apiCall.split(":");
		JsonHelper.setApiRequest(apiRequest.getRequest());
		Map<String, Object>methodData = (Map<String, Object>) apiRequest.getApiCall();
		if (methodData==null) {
			throw new IllegalArgumentException(apiMethod[1]+" Not found in "+fileName+" file");

		}
		Map<String, Object>requestData = (Map<String, Object>) apiRequest.getRequest();
		if (requestData == null) {
			throw new IllegalArgumentException("Incorrect json file format");
		}

		List<Map<String, Object>> body = handleJsonBody();

		RequestSpecification request = RestAssured.given()
				.baseUri(apiRequest.getUrl())
				.contentType(ContentType.JSON)
				.body(body);

		Response response = getResponse(apiMethod[0],request);
		return response;
	}

	@SuppressWarnings("unchecked")
	private static List<Map<String, Object>> handleJsonBody() {
		JsonNode bodyNode = JsonHelper.getJsonObject().get(JsonHelper.getApiName()).get("request").get("body");
		List<Map<String, Object>> bodyList = new ArrayList<>();
		if(bodyNode.isArray()) {
			for (JsonNode node : bodyNode) {
				Map<String, Object> map = JsonHelper.objectMapper.convertValue(node, Map.class);
				bodyList.add(map);
			}
		} else if (bodyNode.isObject()) {
			Map<String, Object> map =  JsonHelper.objectMapper.convertValue(bodyNode, Map.class);
			bodyList.add(map);
		} else {
			// Handle the case where 'body' is neither an array nor an object
			throw new IllegalArgumentException("'body' is neither an array nor an object");
		}
		return bodyList;
	}

	private static Response getResponse(String methodName,RequestSpecification request) {
		Response response;
		switch (methodName.toUpperCase()) {
		case "POST": 
			response = request.post();
			break;
		case "PUT": 
			response =  request.put();
			break;
		case "GET": 
			response =  request.get();
			break;
		case "DELETE": 
			response =  request.delete();
			break;
		default: 
			throw new IllegalArgumentException("Invalid HTTP method : "+methodName);
		}
		return response;
	}

	public static JsonNode apiRequestSent() throws IOException {
		JsonNode request = JsonHelper.getJsonObject().get(JsonHelper.getApiName()).get("request");
		//		Map<String, Object> apiData =  (Map<String, Object>) JsonHelper.getApiRequest().get(JsonHelper.getApiName());
		//		Map<String, Object> requestData = (Map<String, Object>) apiData.get("request");
		return request;
	}

	public static JsonNode apiExpectedResponse() {
		JsonNode responseData = JsonHelper.getJsonObject().get(JsonHelper.getApiName()).get("response");
		//		Map<String, Object> apiData= (Map<String, Object>) JsonHelper.getApiRequest().get(JsonHelper.getApiName());
		//		Map<String, Object> responseData = (Map<String, Object>) apiData.get("response");
		return responseData;
	}

	public static JsonNode getJsonObjresponseBody(String responseBody) throws JsonMappingException, JsonProcessingException {
		JsonNode jsonObj =  JsonHelper.objectMapper.readTree(responseBody);
		return jsonObj;
	}
}	
