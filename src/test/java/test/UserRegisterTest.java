package test;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

@Epic("User cases")
@Feature("Registration")
public class UserRegisterTest extends BaseTestCase {
  private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

  @Test
  @Description("Test check registration with existing email")
  @DisplayName("Registration with existing email")
  public void testCreateUserWithExistingEmail(){
    String email = "vinkotov@example.com";
    Response responseCreateAuth = apiCoreRequests.makePostRegistrationRequestWithParam(createUserBody("email", email));
    Assertions.assertResponseCodeEquals(400, responseCreateAuth);
    Assertions.assertResponseTextEquals("Users with email '" + email + "' already exists", responseCreateAuth);
  }

  @Test
  @Description("Test check registration with new email")
  @DisplayName("Registration with new email")
  public void testCreateUserSuccessfully(){
    Response responseCreateAuth = apiCoreRequests.makePostRegistrationRequest();
    responseCreateAuth.prettyPrint();
    Assertions.assertResponseCodeEquals(200, responseCreateAuth);
    Assertions.assertJsonHasField("id", responseCreateAuth);
  }

  @Test
  @Description("Test check registration with incorrect email, w/o @")
  @DisplayName("Registration with incorrect email")
  public void testCreateUserWithIncorrectEmail() {
    String incorrectEmail = "example.com";
    Response responseCreateAuth = apiCoreRequests.makePostRegistrationRequestWithParam(createUserBody("email", incorrectEmail));
    Assertions.assertResponseCodeEquals(400, responseCreateAuth);
    Assertions.assertResponseTextEquals("Invalid email format", responseCreateAuth);
  }

  @Description("Test check registration with empty obligatory parameter")
  @DisplayName("Registration with empty obligatory parameters")
  @ParameterizedTest
  @ValueSource(strings = {"username", "firstName", "lastName", "email", "password"})
  public void testCreateUserWithEmptyParameter(String parameter) {
    Response response = apiCoreRequests.makePostRegistrationRequestWithParam(createUserBody(parameter, ""));
    Assertions.assertResponseCodeEquals(400, response);
    Assertions.assertResponseTextEquals("The value of '" + parameter + "' field is too short", response);
  }

  @Description("Test check registration w/o obligatory parameter")
  @DisplayName("Registration without obligatory parameters")
  @ParameterizedTest
  @ValueSource(strings = {"username", "firstName", "lastName", "email", "password"})
  public void testCreateUserWithoutParameter(String parameter) {
    Response response = apiCoreRequests.makePostRegistrationRequestWithBody(DataGenerator.getRegistrationDataWithoutParameter(parameter));
    Assertions.assertResponseCodeEquals(400, response);
    Assertions.assertResponseTextEquals("The following required params are missed: " + parameter, response);
  }

  @Description("Test check registration with short username and firstName")
  @DisplayName("Registration with short username and firstName")
  @ParameterizedTest
  @ValueSource(strings = {"username", "firstName"})
  public void testCreateUserWithShortUsernameAndFirstName(String parameter) {
    String shortName = "1";
    Response responseCreateAuth = apiCoreRequests.makePostRegistrationRequestWithParam(createUserBody(parameter, shortName));
    Assertions.assertResponseCodeEquals(400, responseCreateAuth);
    Assertions.assertResponseTextEquals("The value of '" + parameter + "' field is too short", responseCreateAuth);
  }

  @Description("Test check registration with long username and firstName")
  @DisplayName("Registration with long username and firstName")
  @ParameterizedTest
  @ValueSource(strings = {"username", "firstName"})
  public void testCreateUserWithLongUsernameAndFirstName(String parameter) {
    String longName = RandomStringUtils.random(251);
    Response responseCreateAuth = apiCoreRequests.makePostRegistrationRequestWithParam(createUserBody(parameter, longName));
    Assertions.assertResponseCodeEquals(400, responseCreateAuth);
    Assertions.assertResponseTextEquals("The value of '" + parameter + "' field is too long", responseCreateAuth);
  }

  private Map<String, String> createUserBody(String key, String value) {
    Map<String, String> body = new HashMap<>();
    body.put(key, value);
    body = DataGenerator.getRegistrationData(body);
    return body;
  }
}
