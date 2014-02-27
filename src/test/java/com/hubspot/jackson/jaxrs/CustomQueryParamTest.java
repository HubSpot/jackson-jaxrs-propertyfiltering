package com.hubspot.jackson.jaxrs;

public class CustomQueryParamTest extends AbstractIntegrationTest {

  @Override
  protected String path() {
    return "/custom";
  }

  @Override
  protected String queryParamName() {
    return "custom";
  }
}
