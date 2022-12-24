package test;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Epic("Authorisation cases")
@Feature("Authorization")
public class UserAuthTest extends BaseTestCase {
  private String cookieValue;
  private String headerValue;
  private int userIdOnAuthValue;
  private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

  @BeforeEach
  public void loginUser() {
    Response responseGetAuth = apiCoreRequests.makePostRequest(ApiMethods.USER_LOGIN, DataGenerator.getAuthorizationData());
    this.cookieValue = getCookie(responseGetAuth, BaseTestCase.cookieName);
    this.headerValue = getHeader(responseGetAuth, BaseTestCase.headerName);
    this.userIdOnAuthValue = getIntFromJson(responseGetAuth, BaseTestCase.userIdName);
  }

  @Test
  @Description("This test successfully authorize user by email and password")
  @DisplayName("Test positive auth user")
  public void testAuthUser() {
    Response responseCheckAuth = apiCoreRequests.makeGetRequest(ApiMethods.USER_AUTHORIZATION, headerValue, cookieValue);
    Assertions.assertJsonByName(responseCheckAuth, BaseTestCase.userIdName, userIdOnAuthValue);
  }

  @Description("This test check authorization status w/o sending auth cookie or token")
  @DisplayName("Test negative auth user")
  @ParameterizedTest
  @ValueSource(strings = {"cookie", "headers"})
  public void testNegativeAuthUser(String condition) {
    if (condition.equals("cookie")) {
      Response responseForCheck = apiCoreRequests.makeGetRequestWithCookie(ApiMethods.USER_AUTHORIZATION, cookieValue);
      Assertions.assertJsonByName(responseForCheck, BaseTestCase.userIdName, 0);
    } else if (condition.equals("headers")) {
      Response responseForCheck = apiCoreRequests.makeGetRequestWithToken(ApiMethods.USER_AUTHORIZATION, headerValue);
      Assertions.assertJsonByName(responseForCheck, BaseTestCase.userIdName, 0);
    } else {
      throw new IllegalArgumentException("Condition value isn't known " + condition);
    }
  }
}
