package com.hubspot.jackson.jaxrs;

import com.hubspot.jackson.jaxrs.util.TestResource.TestObject;

import java.util.List;

public class JsonViewTest extends AbstractIntegrationTest {

  @Override
  protected String path() {
    return "/view";
  }

  @Override
  protected String queryParamName() {
    return "property";
  }

  @Override
  protected void assertNamePresent(List<TestObject> objects) {
    assertNameNotPresent(objects);
  }
}
