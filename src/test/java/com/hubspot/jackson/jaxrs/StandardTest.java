package com.hubspot.jackson.jaxrs;

public class StandardTest extends AbstractIntegrationTest {

  @Override
  protected String path() {
    return "";
  }

  @Override
  protected String queryParamName() {
    return "property";
  }
}
