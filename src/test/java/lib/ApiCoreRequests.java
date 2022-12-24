package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {
  @Step("Make a GET-request with token and auth cookie")
  public Response makeGetRequest(ApiMethods method, String token, String cookie) {
    return given().filter(new AllureRestAssured()).header(new Header("x-csrf-token", token)).cookie("auth_sid", cookie)
            .get(method.toString()).andReturn();
  }

  @Step("Make a GET-request with auth cookie only")
  public Response makeGetRequestWithCookie(ApiMethods method, String cookie) {
    return given().filter(new AllureRestAssured()).cookie("auth_sid", cookie).get(method.toString()).andReturn();
  }

  @Step("Make a GET-request with token only")
  public Response makeGetRequestWithToken(ApiMethods method, String token) {
    return given().filter(new AllureRestAssured()).header(new Header("x-csrf-token", token)).get(method.toString()).andReturn();
  }

  @Step("Make a POST-request with")
  public Response makePostRequest(ApiMethods method, Map<String, String> authData) {
    return given().filter(new AllureRestAssured()).body(authData).post(method.toString()).andReturn();
  }
}
