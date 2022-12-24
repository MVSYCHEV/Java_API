package test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserGetTest extends BaseTestCase {
  private final String getUserMethod = "https://playground.learnqa.ru/api/user/2";
  private final String loginUserMethod = "https://playground.learnqa.ru/api/user/login";
  private final String headerName = "x-csrf-token";
  private final String cookieName = "auth_sid";

  @Test
  public void testGetUserDataNotAuth() {
    Response responseUserData = RestAssured.get(getUserMethod).andReturn();
    Assertions.assertJsonHasField("username", responseUserData);
    Assertions.assertJsonHasNotField("firstName", responseUserData);
    Assertions.assertJsonHasNotField("lastName", responseUserData);
    Assertions.assertJsonHasNotField("email", responseUserData);
  }

  @Test
  public void testGetUserDetailsAuthAsSameUser() {
    Map<String, String> userData = new HashMap<>();
    userData.put("email", "vinkotov@example.com");
    userData.put("password", "1234");

    Response responseGetAuth = RestAssured.given().body(userData).post(loginUserMethod).andReturn();
    String header = getHeader(responseGetAuth, headerName);
    String cookie = getCookie(responseGetAuth, cookieName);

    Response responseUserData = RestAssured.given().header(headerName, header).cookie(cookieName, cookie).get(getUserMethod).andReturn();
    String[] expectedFields = {"username", "firstName", "lastName", "email"};
    Assertions.assertJsonHasFields(expectedFields, responseUserData);
  }
}
