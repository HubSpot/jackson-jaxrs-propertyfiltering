package com.hubspot.jackson.jaxrs;

public class WrappedAndCustomQueryParamTest extends AbstractIntegrationTest {

  @Override
  protected String path() {
    return "/wrapped/custom";
  }

  @Override
  protected String queryParamName() {
    return "custom";
  }

  @Override
  protected boolean wrapped() {
    return true;
  }
}
