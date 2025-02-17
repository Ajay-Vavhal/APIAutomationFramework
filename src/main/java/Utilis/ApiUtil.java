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

public class ApiUtil {	
	private static final String BASE_PATH = "src/test/resources/api_request/";
	private static String authToken; // Static variable to store the token
	private static long tokenExpiryTime; // Timestamp when the token expires
	private static final Object tokenLock = new Object(); // For thread safety

	private ApiUtil() {
		
	}
	
 	@SuppressWarnings("unchecked")
	public static Response sendRequest(String apiCall,String fileName) throws IOException {
		synchronized(tokenLock) {
			JsonHelper.setApiName(apiCall);
			JsonHelper.setJsonFileName(fileName);
			Map<String, Object> apiRequest = JsonUtil.readJsonFile(BASE_PATH + fileName);
			String[] apiMethod = apiCall.split(":");
			JsonHelper.setApiRequest(apiRequest);
			Map<String, Object>methodData = (Map<String, Object>) apiRequest.get(apiCall);

			// API Call
			if (methodData==null) {
				throw new IllegalArgumentException(apiMethod[1]+" Not found in "+fileName+" file");

			}

			// Request Data
			Map<String, Object>requestData = (Map<String, Object>) methodData.get("request");
			if (requestData == null) {
				throw new IllegalArgumentException("Incorrect json file format");
			}

			List<Map<String, Object>> body = handleJsonBody();

			RequestSpecification request = RestAssured.given()
					.baseUri(requestData.get("url").toString())
					.contentType(ContentType.JSON)
					.body(body);

			// Token Authorization - Only if for Authorization Cases
			if (isTokenValid()) {
				request.header("Authorization", "Bearer " + getAuthToken());
			} else {
				System.err.println("No valid token available. Proceeding without Authorization header.");
			}
			Response response = getResponse(apiMethod[0],request);
			System.out.println(response.getBody().asString());

			return response;
		}
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

	public static void generateToken(String tokenUrl, Map<String, String> tokenRequestParams) throws IOException {
		synchronized(tokenLock) { // Ensure thread safety
			try {
				RequestSpecification request = RestAssured.given()
						.baseUri(tokenUrl)
						.contentType(ContentType.JSON)
						.body(tokenRequestParams);

				Response response = request.post(); 

				if (response.getStatusCode() == 200) {
					authToken = response.jsonPath().getString("token"); // Extract token from response
					long expiresIn = response.jsonPath().getLong("expires_in"); // Token expiry time in seconds
					tokenExpiryTime = System.currentTimeMillis() + (expiresIn * 1000); // Calculate expiry timestamp
				} else {
					throw new RuntimeException("Failed to generate token. Status code: " + response.getStatusCode());
				}
			} catch (Exception e) {
				throw new IOException("Token generation failed: " + e.getMessage(), e);
			}
		}
	}

	// Method to check if the token is valid (not expired)
	private static boolean isTokenValid() {
		synchronized (tokenLock) {  // Ensure thread safety
			return authToken != null && System.currentTimeMillis() < tokenExpiryTime;
		}
	}

	private static String getAuthToken() {
		synchronized (tokenLock) {  // Ensure thread safety
			if (!isTokenValid()) {
				throw new RuntimeException("Token is either expired or not generated.");
			}
			return authToken;
		}
	}

	public static JsonNode apiRequestSent() throws IOException {
		JsonNode request = JsonHelper.getJsonObject().get(JsonHelper.getApiName()).get("request");
		//		Map<String, Object> apiData =  (Map<String, Object>) JsonHelper.getApiRequest().get(JsonHelper.getApiName());
		//		Map<String, Object> requestData = (Map<String, Object>) apiData.get("request");
		return request;
	}

	public static JsonNode getExpectedResponse() {
		JsonNode responseData = JsonHelper.getJsonObject().get(JsonHelper.getApiName()).get("response");
		//		Map<String, Object> apiData= (Map<String, Object>) JsonHelper.getApiRequest().get(JsonHelper.getApiName());
		//		Map<String, Object> responseData = (Map<String, Object>) apiData.get("response");
		return responseData;
	}

	public static String getExpectedMessage() {
		String actualMessage = JsonHelper.getJsonObject().get(JsonHelper.getApiName()).get("response").get("message").asText();
		//		Map<String, Object> apiData= (Map<String, Object>) JsonHelper.getApiRequest().get(JsonHelper.getApiName());
		//		Map<String, Object> responseData = (Map<String, Object>) apiData.get("response");
		return actualMessage;
	}

	public static int getExpectedStatusCode() {
		int statusCode = JsonHelper.getJsonObject().get(JsonHelper.getApiName()).get("response").get("code").asInt();
		//		Map<String, Object> apiData= (Map<String, Object>) JsonHelper.getApiRequest().get(JsonHelper.getApiName());
		//		Map<String, Object> responseData = (Map<String, Object>) apiData.get("response");
		return statusCode;
	}

	public static JsonNode getJsonNodeResponseBody(String responseBody) throws JsonMappingException, JsonProcessingException {
		JsonNode jsonObj =  JsonHelper.objectMapper.readTree(responseBody);
		return jsonObj;
	}

	public static String getActualMessage(Response response) throws JsonMappingException, JsonProcessingException {
		String actualMessage =  response.jsonPath().getString("message");
		return actualMessage;
	}
}	
