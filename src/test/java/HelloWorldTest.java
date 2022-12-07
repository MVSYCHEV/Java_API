import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
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
}
