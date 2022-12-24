package lib;

public enum  ApiMethods {
  /**
   * For more details https://playground.learnqa.ru/api/map
   */

  USER_LOGIN("https://playground.learnqa.ru/api/user/login"),
  USER_AUTHORIZATION("https://playground.learnqa.ru/api/user/auth");

  private final String method;

  ApiMethods(String method) {
    this.method = method;
  }
  @Override
  public String toString() {
    return method;
  }
}
