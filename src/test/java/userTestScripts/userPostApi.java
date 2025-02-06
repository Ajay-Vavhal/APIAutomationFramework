package userTestScripts;

import java.io.IOException;
import org.testng.Assert;
import org.testng.annotations.Test;

import Utilis.ApiUtil;
import basePackage.BaseTest;
import io.restassured.response.Response;

public class userPostApi extends BaseTest {

	@Test
	public void usercreateWitList() throws IOException {
		// Create User
		Response response = ApiUtil.sendRequest("Post:listOfUsers","user.json");
		Assert.assertEquals(response.getStatusCode(),ApiUtil.getExpectedStatusCode());
		Assert.assertEquals(ApiUtil.getJsonNodeResponseBody(response.getBody().asString()),ApiUtil.getExpectedResponse());
		Assert.assertEquals(ApiUtil.getActualMessage(response),ApiUtil.getExpectedMessage());
		System.out.println(ApiUtil.apiRequestSent());
		System.out.println(ApiUtil.getExpectedResponse());
	}

	@Test
	public void getUser() throws IOException {
		// Get User
		Response response = ApiUtil.sendRequest("Get:getUser","user.json");
		Assert.assertEquals(response.getStatusCode(),ApiUtil.getExpectedStatusCode());
		//Assert.assertEquals(ApiUtil.getJsonObjresponseBody(response.getBody().asString()),ApiUtil.apiExpectedResponse());
		System.out.println(ApiUtil.apiRequestSent());
		System.out.println(ApiUtil.getExpectedResponse());
		System.out.println(ApiUtil.getJsonNodeResponseBody(response.getBody().asString()));
	}
}
