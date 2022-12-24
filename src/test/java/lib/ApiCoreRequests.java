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
    return given().filter(new AllureRestAssured()).header(new Header(BaseTestCase.headerName, token)).cookie(BaseTestCase.cookieName, cookie)
            .get(method.toString()).andReturn();
  }

  @Step("Make a GET-request with token and auth cookie")
  public Response makeGetRequest(String method, String token, String cookie) {
    return given().filter(new AllureRestAssured()).header(new Header(BaseTestCase.headerName, token)).cookie(BaseTestCase.cookieName, cookie)
            .get(method).andReturn();
  }

  @Step("Make a GET-request with auth cookie only")
  public Response makeGetRequestWithCookie(ApiMethods method, String cookie) {
    return given().filter(new AllureRestAssured()).cookie(BaseTestCase.cookieName, cookie).get(method.toString()).andReturn();
  }

  @Step("Make a GET-request with token only")
  public Response makeGetRequestWithToken(ApiMethods method, String token) {
    return given().filter(new AllureRestAssured()).header(new Header(BaseTestCase.headerName, token)).get(method.toString()).andReturn();
  }

  @Step("Make a POST-request")
  public Response makePostRequest(ApiMethods method, Map<String, String> authData) {
    return given().filter(new AllureRestAssured()).body(authData).post(method.toString()).andReturn();
  }

  @Step("Make a GET-request")
  public Response makeGetRequest(String method) {
    return given().filter(new AllureRestAssured()).get(method).andReturn();
  }

  @Step("Make a registration POST-request for new user")
  public Response makePostRegistrationRequest() {
    return makePostRequest(ApiMethods.CREATE_USER, DataGenerator.getRegistrationData());
  }

  @Step("Make a registration POST-request for new user with custom parameter")
  public Response makePostRegistrationRequestWithParam(Map<String, String> params) {
    return makePostRequest(ApiMethods.CREATE_USER, DataGenerator.getRegistrationData(params));
  }

  @Step("Make a registration POST-request for new user with custom body")
  public Response makePostRegistrationRequestWithBody(Map<String, String> params) {
    return makePostRequest(ApiMethods.CREATE_USER, params);
  }

  @Step("Make a GET-request for receive userData")
  public Response makeGetRequestReceiveUserData(int userId) {
    return makeGetRequest(ApiMethods.CREATE_USER.toString().concat(String.valueOf(userId)));
  }

  @Step("Make a GET-request for receive userData as authorized user")
  public Response makeGetRequestReceiveUserData(String header, String cookie, int userId) {
    return makeGetRequest(ApiMethods.CREATE_USER.toString().concat(String.valueOf(userId)), header, cookie);
  }
}
