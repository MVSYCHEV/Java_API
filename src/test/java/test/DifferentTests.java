package test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class DifferentTests {
  public final String firstUserAgent = "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";
  public final String secondUserAgent = "Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1";
  public final String thirdUserAgent = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";
  public final String fourthUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0";
  public final String fifthUserAgent = "'Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1";

  @Test
  public void testHelloWorld() {
    System.out.println("Hello from Misha :)");
  }

  @Test
  public void testGetText() {
    Response response = RestAssured.get("https://playground.learnqa.ru/api/get_text").andReturn();
    response.prettyPrint();
  }

  @Test
  public void testGetJsonHomework() {
    JsonPath response = RestAssured.get("https://playground.learnqa.ru/api/get_json_homework").jsonPath();
    List<String> listMessages = response.getList("messages.message");
    String second = listMessages.get(1);
    System.out.println(second);
  }

  @Test
  public void testRedirect() {
    Response response = RestAssured.given().redirects().follow(false).get("https://playground.learnqa.ru/api/long_redirect").andReturn();
    String redirectUrl = response.getHeader("location");
    System.out.println(redirectUrl);
  }

  @Test
  public void testCountOfRedirects() {
    Response response = RestAssured.given().redirects().follow(false).when().get("https://playground.learnqa.ru/api/long_redirect").andReturn();
    int statusCode = response.getStatusCode();
    int count = 0;
    while (statusCode != 200) {
      String url = response.getHeader("location");
      response = RestAssured.given().redirects().follow(false).when().get(url).andReturn();
      statusCode = response.getStatusCode();
      count++;
    }
    System.out.println(count);
  }

  @Test
  public void testToken() {
    String url = "https://playground.learnqa.ru/ajax/api/longtime_job";
    JsonPath response = RestAssured.get(url).jsonPath();
    String token = response.getString("token");
    int seconds = Integer.parseInt(response.getString("seconds"));

    response = RestAssured.given().queryParam("token", token).get(url).jsonPath();
    Assertions.assertEquals("Job is NOT ready", response.getString("status"));

    try {
      Thread.sleep(seconds * 1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    response = RestAssured.given().queryParam("token", token).get(url).jsonPath();
    Assertions.assertEquals("Job is ready", response.getString("status"));
    Assertions.assertNotNull(response.getString("result"));
  }

  @Test
  public void testPasswordSelection() {
    String responsePhrase = "";
    Object password = "";

    List<Object> uniquePasswords = passwords().stream().distinct().collect(Collectors.toList());
    for (Object uniquePassword : uniquePasswords) {
      Map<String, Object> body = new HashMap<>();
      body.put("login", "super_admin");
      body.put("password", uniquePassword);

      Response getSecretPassword = RestAssured
              .given()
              .body(body)
              .when()
              .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
              .andReturn();
      Assertions.assertTrue(getSecretPassword.getStatusCode() != 500);
      String authCookie = getSecretPassword.getCookie("auth_cookie");

      Response checkAuthCookie = RestAssured
              .given()
              .cookie("auth_cookie", authCookie)
              .when()
              .get("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
              .andReturn();
      String checkAuthCookieResponse = checkAuthCookie.asString();
      if (checkAuthCookieResponse.equals("You are authorized")) {
        checkAuthCookie.print();
        password = uniquePassword;
        break;
      }
    }

    System.out.println(responsePhrase);
    System.out.println(password);
  }

  @ParameterizedTest
  @ValueSource(strings = {"Какое-то непонятное количество символов", "Точно петнадцат"})
  public void testStringLength(String string) {
    Assertions.assertEquals(15, string.length(), "String length more than 15");
  }

  @Test
  public void testCookie() {
    Response response = RestAssured.get("https://playground.learnqa.ru/api/homework_cookie").andReturn();
    Map<String, String> cookies = response.getCookies();
    String cookie = "HomeWork";
    Assertions.assertTrue(cookies.containsKey(cookie), "Response doesn't contain" + cookie + " cookie");
    Assertions.assertEquals("hw_value", cookies.get(cookie), "The value of cookie " + cookie + " unexpected");
  }

  @Test
  public void testHeader() {
    Response response = RestAssured.get("https://playground.learnqa.ru/api/homework_header").andReturn();
    Headers headers = response.getHeaders();
    String header = "x-secret-homework-header";
    Assertions.assertTrue(headers.hasHeaderWithName(header));
    Assertions.assertEquals("Some secret value", headers.getValue(header), "Unexpected value of header " + header);
  }

  @ParameterizedTest
  @ValueSource(strings = {firstUserAgent, secondUserAgent, thirdUserAgent, fourthUserAgent, fifthUserAgent})
  public void testUserAgent(String userAgent) {
    RequestSpecification specification = RestAssured.given();
    specification.baseUri("https://playground.learnqa.ru/ajax/api/user_agent_check");
    String userAgentHeader = "User-Agent";

    switch (userAgent) {
      case firstUserAgent:
        specification.header(userAgentHeader, firstUserAgent);
        break;
      case secondUserAgent:
        specification.header(userAgentHeader, secondUserAgent);
        break;
      case thirdUserAgent:
        specification.header(userAgentHeader, thirdUserAgent);
        break;
      case fourthUserAgent:
        specification.header(userAgentHeader, fourthUserAgent);
        break;
      case fifthUserAgent:
        specification.header(userAgentHeader, fifthUserAgent);
        break;
      default:
        throw new IllegalArgumentException("Unexpected User Agent header");
    }

    JsonPath response = specification.get().jsonPath();

    switch (userAgent) {
      case firstUserAgent:
        Assertions.assertEquals("Mobile", response.getString("platform"), "Incorrect platform in " + firstUserAgent);
        Assertions.assertEquals("No", response.getString("browser"), "Incorrect browser in " + firstUserAgent);
        Assertions.assertEquals("Android", response.getString("device"), "Incorrect device in " + firstUserAgent);
        break;
      case secondUserAgent:
        Assertions.assertEquals("Mobile", response.getString("platform"), "Incorrect platform in " + secondUserAgent);
        Assertions.assertEquals("Chrome", response.getString("browser"), "Incorrect browser in " + secondUserAgent);
        Assertions.assertEquals("iOS", response.getString("device"), "Incorrect device in " + secondUserAgent);
        break;
      case thirdUserAgent:
        Assertions.assertEquals("Googlebot", response.getString("platform"), "Incorrect platform in " + thirdUserAgent);
        Assertions.assertEquals("Unknown", response.getString("browser"), "Incorrect browser in " + thirdUserAgent);
        Assertions.assertEquals("Unknown", response.getString("device"), "Incorrect device in " + thirdUserAgent);
        break;
      case fourthUserAgent:
        Assertions.assertEquals("Web", response.getString("platform"), "Incorrect platform in " + fourthUserAgent);
        Assertions.assertEquals("Chrome", response.getString("browser"), "Incorrect browser in " + fourthUserAgent);
        Assertions.assertEquals("No", response.getString("device"), "Incorrect device in " + fourthUserAgent);
        break;
      case fifthUserAgent:
        Assertions.assertEquals("Mobile", response.getString("platform"), "Incorrect platform in " + fifthUserAgent);
        Assertions.assertEquals("No", response.getString("browser"), "Incorrect browser in " + fifthUserAgent);
        Assertions.assertEquals("iPhone", response.getString("device"), "Incorrect device in " + fifthUserAgent);
        break;
    }
  }

  private List<Object> passwords() {
    ObjectMapper objectMapper = new ObjectMapper();
    List<Object> listOfPasswords = new ArrayList<>();
    Map<String, Object> columnsWithPasswords = new HashMap<>();
    try {
      JsonNode jsonNode = objectMapper.readTree(new File("src/test/resources/Passwords.json"));
      for (int i = 0; i < 25; i++) {
        JsonNode childNode = jsonNode.get(i);
        columnsWithPasswords.putAll(objectMapper.convertValue(childNode, new TypeReference<Map<String, Object>>() {
        }));
        listOfPasswords.addAll(columnsWithPasswords.values());
      }
    } catch (IOException exception) {
      exception.printStackTrace();
    }
    return listOfPasswords;
  }
}