import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;


public class HelloWorldTest {
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