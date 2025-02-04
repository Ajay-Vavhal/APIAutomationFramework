package pageObjectModel;

import java.util.List;
import java.util.Map;

public class ApiRequest {

	private static String url;
	private static String method;
	private static List<Map<String, Object>>body;
	private static Map<String, Object>apiCall;
	private static Map<String, Object>request;
	private static Map<String, Object>response;

	// Default Constructor
	public ApiRequest() {}

	// Getters and Setters
	public static String getUrl() {
		return url; 
	}
	@SuppressWarnings("static-access")
	public void setUrl(String url) { 
		this.url = url; 
	}

	public static String getMethod() { 
		return method;  
	}
	public void setMethod(String method) { 
		this.method = method; 
	}

	public static List<Map<String, Object>> getBody() { 
		return body; 
	}
	
	public void setBody(List<Map<String, Object>> body) { 
		this.body = body; 
	}

	public static Map<String, Object> getApiCall() {
		return apiCall;
	}

	public void setApiCall(Map<String, Object> apiCall) {
		this.apiCall = apiCall;
	}

	public static Map<String, Object> getResponse() {
		return response;
	}

	public void setResponse(Map<String, Object> response) {
		this.response = response;
	}

	public static Map<String, Object> getRequest() {
		return request;
	}

	public void setRequest(Map<String, Object> request) {
		this.request = request;
	}



}
