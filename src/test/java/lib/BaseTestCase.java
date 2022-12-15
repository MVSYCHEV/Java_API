package lib;

import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import static org.hamcrest.Matchers.hasKey;

import java.util.Map;

public class BaseTestCase {
  protected String getHeader(Response response, String headerName) {
    Headers headers = response.getHeaders();
    Assertions.assertTrue(headers.hasHeaderWithName(headerName), "Response doesn't contain header with name " + headerName);
    return headers.getValue(headerName);
  }

  protected String getCookie(Response response, String cookieName) {
    Map<String, String> cookies = response.getCookies();
    Assertions.assertTrue(cookies.containsKey(cookieName), "Response doesn't contain cookie with name " + cookieName);
    return cookies.get(cookieName);
  }

  protected int getIntFromJson(Response response, String name) {
    response.then().assertThat().body("$", hasKey(name));
    return response.jsonPath().getInt(name);
  }
}
