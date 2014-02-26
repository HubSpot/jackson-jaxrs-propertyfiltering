package com.hubspot.jackson.jaxrs;

public class WrappedTest extends AbstractIntegrationTest {

  @Override
  protected String path() {
    return "/wrapped";
  }

  @Override
  protected String queryParamName() {
    return "property";
  }

  @Override
  protected boolean wrapped() {
    return true;
  }
}
