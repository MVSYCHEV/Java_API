package test;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("User cases")
@Feature("Delete user")
public class UserDeleteTest extends BaseTestCase {
  ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
  private String loginHeader;
  private String loginCookie;
  private String authEmail;
  private String authPassword;
  private int userIdOnAuthValue;
  private String userId;

  @Test
  @Description("Test check delete default user with id 2")
  @DisplayName("Test check delete default user")
  @Severity(SeverityLevel.MINOR)
  public void testDeleteDefaultUser() {
      Response responseLoginAsDefaultUser = apiCoreRequests.makePostRequest(ApiMethods.USER_LOGIN, DataGenerator.getAuthorizationData());
      loginHeader = getHeader(responseLoginAsDefaultUser, BaseTestCase.headerName);
      loginCookie = getCookie(responseLoginAsDefaultUser, BaseTestCase.cookieName);
      userIdOnAuthValue = getIntFromJson(responseLoginAsDefaultUser, BaseTestCase.userIdName);

      Response responseDeleteUser = apiCoreRequests.makeDeleteRequestUser(loginHeader, loginCookie, String.valueOf(userIdOnAuthValue));
      Assertions.assertResponseTextEquals("Please, do not delete test users with ID 1, 2, 3, 4 or 5.", responseDeleteUser);

      Response getUser = apiCoreRequests.makeGetRequestReceiveUserData(loginHeader, loginCookie, userIdOnAuthValue);
      String[] userData = {"username", "id", "lastName", "email", "lastName"};
      Assertions.assertJsonHasFields(userData, getUser);
  }

  @Test
  @Description("Test check delete created user")
  @DisplayName("Test check delete created user")
  @Severity(SeverityLevel.BLOCKER)
  public void testDeleteCreatedUser() {
    createUser();
    login(authEmail, authPassword);
    Response responseDeleteUser = apiCoreRequests.makeDeleteRequestUser(loginHeader, loginCookie, userId);
    Response getUser = apiCoreRequests.makeGetRequestReceiveUserData(loginHeader, loginCookie, userIdOnAuthValue);
    Assertions.assertResponseTextEquals("User not found", getUser);
  }
  @Test
  @Description("Test check delete user from another user account")
  @DisplayName("Test check delete another user")
  @Severity(SeverityLevel.BLOCKER)
  public void testDeleteUserAsAnotherUser() {
    createUser();

    Response responseLoginAsDefaultUser = apiCoreRequests.makePostRequest(ApiMethods.USER_LOGIN, DataGenerator.getAuthorizationData());
    loginHeader = getHeader(responseLoginAsDefaultUser, BaseTestCase.headerName);
    loginCookie = getCookie(responseLoginAsDefaultUser, BaseTestCase.cookieName);

    Response responseDeleteUser = apiCoreRequests.makeDeleteRequestUser(loginHeader, loginCookie, userId);
    Assertions.assertResponseTextEquals("Please, do not delete test users with ID 1, 2, 3, 4 or 5.", responseDeleteUser);

    login(authEmail, authPassword);
    Response getUser = apiCoreRequests.makeGetRequestReceiveUserData(loginHeader, loginCookie, userIdOnAuthValue);
    Assertions.assertJsonByName(getUser, "id", userId);
  }



  private void login(String email, String password) {
    Map<String, String> authData = new HashMap<>();
    authData.put("email", email);
    authData.put("password", password);
    Response responseGetAuth = apiCoreRequests.makePostRequest(ApiMethods.USER_LOGIN, authData);
    loginHeader = getHeader(responseGetAuth, BaseTestCase.headerName);
    loginCookie = getCookie(responseGetAuth, BaseTestCase.cookieName);
    userIdOnAuthValue = getIntFromJson(responseGetAuth, BaseTestCase.userIdName);
  }

  private void createUser() {
    Map<String, String> userData = DataGenerator.getRegistrationData();
    Response responseCreateAuth = apiCoreRequests.makePostRegistrationRequestWithBody(userData);
    userId = getStringFromJson(responseCreateAuth, "id");
    authEmail = userData.get("email");
    authPassword = userData.get("password");
  }
}
