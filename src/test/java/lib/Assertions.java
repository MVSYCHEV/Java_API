package lib;

import io.restassured.response.Response;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {
  public static void assertJsonByName(Response response, String name, int expectedValue) {
    assertJsonHasField(name, response);
    int value = response.jsonPath().getInt(name);
    assertEquals(expectedValue, value, "Json value isn't equals to expected value");
  }

  public static void assertJsonByName(Response response, String name, String expectedValue) {
    assertJsonHasField(name, response);
    String value = response.jsonPath().getString(name);
    assertEquals(expectedValue, value, "Json value isn't equals to expected value");
  }

  public static void assertResponseTextEquals(String expectedText, Response response) {
    assertEquals(expectedText, response.asString(), "Response text isn't as expected");
  }

  public static void assertResponseCodeEquals(int expectedStatusCode, Response response) {
    assertEquals(expectedStatusCode, response.getStatusCode(), "Response status code isn't as expected");
  }

  public static void assertJsonHasField(String expectedFieldName, Response response) {
    response.then().assertThat().body("$", hasKey(expectedFieldName));
  }

  public static void assertJsonHasNotField(String unexpectedFieldName, Response response) {
    response.then().assertThat().body("$", not(hasKey(unexpectedFieldName)));
  }

  public static void assertJsonHasFields(String[] expectedFieldNames, Response response) {
    for (String expectedFieldName : expectedFieldNames) {
      assertJsonHasField(expectedFieldName, response);
    }
  }
}
