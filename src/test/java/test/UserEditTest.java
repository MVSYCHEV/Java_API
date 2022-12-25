package test;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("User cases")
@Feature("Edit user")
public class UserEditTest extends BaseTestCase {
  ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
  private String userId;
  private String firstNameBeforeEdit;
  private String authEmail;
  private String authPassword;
  private String loginHeader;
  private String loginCookie;
  private final String firstNameField = "firstName";
  private final String emailField = "email";
  private final String newFirstName = "Changed Name";

  @BeforeEach
  public void createUser() {
    Map<String, String> userData = DataGenerator.getRegistrationData();
    Response responseCreateAuth = apiCoreRequests.makePostRegistrationRequestWithBody(userData);
    userId = getStringFromJson(responseCreateAuth, "id");
    authEmail = userData.get(emailField);
    authPassword = userData.get("password");
    firstNameBeforeEdit = userData.get(firstNameField);
  }

  @Test
  @Description("Test check edit created user data")
  @DisplayName("Test check edit created user data")
  public void editJustCreatedTest() {
    login(authEmail, authPassword);
// Edit User
    Response responseEditUser = apiCoreRequests.makePutRequestWithBody(loginHeader, loginCookie, userId,
            editData(firstNameField, newFirstName));
    getUser(firstNameField, newFirstName);
  }

  @Test
  @Description("Test check edit user data, when user is unauthorized")
  @DisplayName("Edit user data as unauthorized user")
  public void testEditAsUnauthorized() {
    login(authEmail, authPassword);
    // Edit User
    Response responseEditUser = apiCoreRequests.makePutRequestUnauthorized(userId, editData(firstNameField, newFirstName));
    Assertions.assertResponseTextEquals("Auth token not supplied", responseEditUser);

    getUser(firstNameField, firstNameBeforeEdit);
  }

  @Test
  @Description("Test check edit user data, as different user")
  @DisplayName("Edit user data as different user")
  public void testEditAsDifferentUser() {
    //login as default user
    login(DataGenerator.getAuthorizationData().get("email"), DataGenerator.getAuthorizationData().get("password"));
    String defaultUserHeader = loginHeader;
    String defaultUserCookie = loginCookie;

    // Edit User
    Response responseEditUser = apiCoreRequests.makePutRequestWithBody(defaultUserHeader, defaultUserCookie, userId,
            editData(firstNameField, newFirstName));
    Assertions.assertResponseTextEquals("Please, do not edit test users with ID 1, 2, 3, 4 or 5.", responseEditUser);

    // Relogin as created user
    login(authEmail, authPassword);
    getUser(firstNameField, firstNameBeforeEdit);
  }

  @Test
  @Description("Test check edit user email with incorrect email")
  @DisplayName("Edit user incorrect email")
  public void testEditIncorrectEmail() {
    login(authEmail, authPassword);

    // Edit User
    Response responseEditUser = apiCoreRequests.makePutRequestWithBody(loginHeader, loginCookie, userId,
            editData(emailField, "incorrectEmail.com"));
    Assertions.assertResponseTextEquals("Invalid email format", responseEditUser);

    getUser(emailField, authEmail);
  }

  @Test
  @Description("Test check edit user email with short firstName")
  @DisplayName("Edit user short firstName")
  public void testEditShortFirstName() {
    login(authEmail, authPassword);

    // Edit User
    Response responseEditUser = apiCoreRequests.makePutRequestWithBody(loginHeader, loginCookie, userId,
            editData(firstNameField, "1"));
    Assertions.assertJsonByName(responseEditUser, "error", "Too short value for field firstName");

    getUser(firstNameField, firstNameBeforeEdit);
  }

  private void login(String email, String password) {
    Map<String, String> authData = new HashMap<>();
    authData.put("email", email);
    authData.put("password", password);
    Response responseGetAuth = apiCoreRequests.makePostRequest(ApiMethods.USER_LOGIN, authData);
    loginHeader = getHeader(responseGetAuth, BaseTestCase.headerName);
    loginCookie = getCookie(responseGetAuth, BaseTestCase.cookieName);
  }

  private void getUser(String checkedField, String value) {
    Response responseUserData = apiCoreRequests.makeGetRequestReceiveUserData(loginHeader, loginCookie, Integer.parseInt(userId));
    Assertions.assertJsonByName(responseUserData, checkedField, value);
  }

  private Map<String, String> editData(String changedField, String newValue) {
    Map<String, String> editData = new HashMap<>();
    editData.put(changedField, newValue);
    return editData;
  }

}
