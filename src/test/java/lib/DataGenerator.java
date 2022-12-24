package lib;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataGenerator {
  public static String getRandomEmail() {
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    return "learnqa" + dateFormat.format(new Date()) + "@example.com";
  }

  public static Map<String, String> getRegistrationData() {
    Map<String, String> data = new HashMap<>();
    data.put("email", getRandomEmail());
    data.put("password", "123");
    data.put("username", "learnqa");
    data.put("firstName", "learnqa");
    data.put("lastName", "learnqa");
    return data;
  }

  public static Map<String, String> getRegistrationData(Map<String, String> nonDefaultValues) {
    Map<String, String> defaultValues = getRegistrationData();
    Map<String, String> userData = new HashMap<>();
    String[] keys = {"email", "password", "username", "firstName", "lastName"};
    for (String key : keys) {
      if (nonDefaultValues.containsKey(key)) {
        userData.put(key, nonDefaultValues.get(key));
      }
      else {
        userData.put(key, defaultValues.get(key));
      }
    }
    return userData;
  }

  public static Map<String, String> getRegistrationDataWithoutParameter(String parameter) {
    Map<String, String> defaultValues = getRegistrationData();
    if (defaultValues.containsKey(parameter)) {
      defaultValues.remove(parameter);
    } else {
      System.out.println("A parameter " + parameter + " isn't obligatory");
    }
    return defaultValues;
  }

  public static Map<String, String> getAuthorizationData() {
    Map<String, String> authData = new HashMap<>();
    authData.put("email", "vinkotov@example.com");
    authData.put("password", "1234");
    return authData;
  }
}
