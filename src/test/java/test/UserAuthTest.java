package test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import lib.Assertions;

import java.util.HashMap;
import java.util.Map;

public class UserAuthTest extends BaseTestCase {
  private final String cookieName = "auth_sid";
  private final String headerName = "x-csrf-token";
  private final String userIdName = "user_id";
  private String cookieValue;
  private String headerValue;
  private int userIdOnAuthValue;

  private final String userAuthMethod = "https://playground.learnqa.ru/api/user/auth";

  @BeforeEach
  public void loginUser() {
    Map<String, String> authData = new HashMap<>();
    authData.put("email", "vinkotov@example.com");
    authData.put("password", "1234");

    Response responseGetAuth = RestAssured.given().body(authData)
            .post("https://playground.learnqa.ru/api/user/login").andReturn();

    this.cookieValue = getCookie(responseGetAuth, cookieName);
    this.headerValue = getHeader(responseGetAuth, headerName);
    this.userIdOnAuthValue = getIntFromJson(responseGetAuth, userIdName);
  }

  @Test
  public void testAuthUser() {
    Response responseCheckAuth = RestAssured.given()
            .header(headerName, headerValue).cookie(cookieName, cookieValue)
            .get(userAuthMethod).andReturn();
    Assertions.assertJsonByName(responseCheckAuth, userIdName, userIdOnAuthValue);
  }

  @ParameterizedTest
  @ValueSource(strings = {"cookie", "headers"})
  public void testNegativeAuthUser(String condition) {
    RequestSpecification specification = RestAssured.given().baseUri(userAuthMethod);

    if (condition.equals("cookie")) {
      specification.cookie(cookieName, cookieValue);
    } else if (condition.equals("headers")) {
      specification.header(headerName, headerValue);
    } else {
      throw new IllegalArgumentException("Condition value isn't known " + condition);
    }

    Response responseForCheck = specification.get().andReturn();
    Assertions.assertJsonByName(responseForCheck, userIdName, 0);
  }
}
