package userTestScripts;

import java.io.IOException;
import org.testng.Assert;
import org.testng.annotations.Test;

import Utilis.ApiUtil;
import Utilis.ApiUtil2;
import Utilis.JsonHelper;
import io.restassured.response.Response;

public class userPostApi {

	@Test
	public void usercreateWitList() throws IOException {
		Response response = ApiUtil.sendRequest("Post:listOfUsers","user.json");
		Assert.assertEquals(response.getStatusCode(),200);
		Assert.assertEquals(response.getStatusCode(),JsonHelper.getJsonObject().get(JsonHelper.getApiName()).get("response").get("code").asInt());
		System.out.println(JsonHelper.getJsonObject().get(JsonHelper.getApiName()).get("response"));
		Assert.assertEquals(ApiUtil.getJsonObjresponseBody(response.getBody().asString()),JsonHelper.getJsonObject().get(JsonHelper.getApiName()).get("response"));
		System.out.println(ApiUtil.apiRequestSent());
		System.out.println(ApiUtil.apiExpectedResponse());
	}
}
