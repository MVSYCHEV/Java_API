package test;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Epic("User cases")
@Feature("Get user information")
public class UserGetTest extends BaseTestCase {
  ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
  private final String getUserMethod = "https://playground.learnqa.ru/api/user/2";

  @Test
  @Description("Test check receive user data as unauthorized user")
  @DisplayName("Get user data as unauthorized user")
  public void testGetUserDataNotAuth() {
    Response responseUserData = apiCoreRequests.makeGetRequestReceiveUserData(2);
    Assertions.assertJsonHasField("username", responseUserData);
    Assertions.assertJsonHasNotField("firstName", responseUserData);
    Assertions.assertJsonHasNotField("lastName", responseUserData);
    Assertions.assertJsonHasNotField("email", responseUserData);
  }

  @Test
  @Description("Test check receive user data as the same user")
  @DisplayName("Get user data as the same user")
  public void testGetUserDetailsAuthAsSameUser() {
    Response responseGetAuth = apiCoreRequests.makePostRequest(ApiMethods.USER_LOGIN, DataGenerator.getAuthorizationData());
    String header = getHeader(responseGetAuth, BaseTestCase.headerName);
    String cookie = getCookie(responseGetAuth, BaseTestCase.cookieName);

    Response responseUserData = apiCoreRequests.makeGetRequestReceiveUserData(header, cookie, 2);
    String[] expectedFields = {"username", "firstName", "lastName", "email"};
    Assertions.assertJsonHasFields(expectedFields, responseUserData);
  }

  @Test
  @Description("Test check receiving user data, when we authorized as different user")
  @DisplayName("Get user data as different user")
  public void testGetUserDetailsAuthAsDifferentUser() {
    // create new user, which details we want to get
    Response responseCreate = apiCoreRequests.makePostRegistrationRequest();
    int id = Integer.parseInt(getStringFromJson(responseCreate, "id"));

    //login as user with id 2 and try to get created user details
    Response responseGetAuth = apiCoreRequests.makePostRequest(ApiMethods.USER_LOGIN, DataGenerator.getAuthorizationData());
    String header = getHeader(responseGetAuth, BaseTestCase.headerName);
    String cookie = getCookie(responseGetAuth, BaseTestCase.cookieName);

    Response responseUserData = apiCoreRequests.makeGetRequestReceiveUserData(header, cookie, id);
    Assertions.assertJsonHasField("username", responseUserData);
    String[] unexpectedFields = {"firstName", "lastName", "email", "password"};
    Assertions.assertJsonHasNotFields(unexpectedFields, responseUserData);
  }
}
