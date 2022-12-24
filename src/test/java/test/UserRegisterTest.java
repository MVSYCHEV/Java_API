package test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {
  private final String createUserMethod = "https://playground.learnqa.ru/api/user/";

  @Test
  public void testCreateUserWithExistingEmail(){
    String email = "vinkotov@example.com";
    Response responseCreateAuth = RestAssured.given().body(createUserBody(email)).post(createUserMethod).andReturn();
    Assertions.assertResponseCodeEquals(400, responseCreateAuth);
    Assertions.assertResponseTextEquals("Users with email '" + email + "' already exists", responseCreateAuth);
  }

  @Test
  public void testCreateUserSuccessfully(){
    Response responseCreateAuth = RestAssured.given().body(DataGenerator.getRegistrationData()).post(createUserMethod).andReturn();
    Assertions.assertResponseCodeEquals(200, responseCreateAuth);
    Assertions.assertJsonHasField("id", responseCreateAuth);
  }

  private Map<String, String> createUserBody(String email) {
    Map<String, String> body = new HashMap<>();
    body.put("email", email);
    body = DataGenerator.getRegistrationData(body);
    return body;
  }
}
