import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.List;


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
}