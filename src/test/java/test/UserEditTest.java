package test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {
  private final String createUserMethod = "https://playground.learnqa.ru/api/user/";
  private final String loginUserMethod = "https://playground.learnqa.ru/api/user/login";
  private final String headerName = "x-csrf-token";
  private final String cookieName = "auth_sid";

  @Test
  public void editJustCreatedTest() {
// Generate User
    Map<String, String> userData = DataGenerator.getRegistrationData();
    JsonPath responseCreateAuth = RestAssured.given().body(userData).post(createUserMethod).jsonPath();
    String userId = responseCreateAuth.getString("id");

// Login
    Map<String, String> authData = new HashMap<>();
    authData.put("email", userData.get("email"));
    authData.put("password", userData.get("password"));
    Response responseGetAuth = RestAssured.given().body(authData).post(loginUserMethod).andReturn();

// Edit User
    String newName = "Changed Name";
    Map<String, String> editData = new HashMap<>();
    editData.put("firstName", newName);
    Response responseEditUser = RestAssured.given()
            .header(headerName, getHeader(responseGetAuth, headerName))
            .cookie(cookieName, getCookie(responseGetAuth, cookieName))
            .body(editData).put(createUserMethod + userId).andReturn();

// Get User
    Response responseUserData = RestAssured.given()
            .header(headerName, getHeader(responseGetAuth, headerName))
            .cookie(cookieName, getCookie(responseGetAuth, cookieName))
            .get(createUserMethod + userId).andReturn();

    Assertions.assertJsonByName(responseUserData, "firstName", newName);
  }
}
