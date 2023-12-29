package com.hubspot.jackson.jaxrs;

import static com.hubspot.jackson.jaxrs.util.TestResource.TestObject;

import java.io.IOException;
import java.util.List;

public class AlwaysPropertiesTest extends AbstractIntegrationTest {

  @Override
  protected String path() {
    return "/always";
  }

  @Override
  protected String queryParamName() {
    return "property";
  }

  @Override
  public void testNoFiltering() throws IOException {
    List<TestObject> objects = getObjects();

    assertIdPresent(objects);
    assertNameNotPresent(objects);
  }

  @Override
  public void testIncludeName() throws IOException {
    List<TestObject> objects = getObjects("name");

    assertIdPresent(objects);
    assertNamePresent(objects);
  }

  @Override
  public void testExcludeId() throws IOException {
    List<TestObject> objects = getObjects("!id");

    assertIdNotPresent(objects);
    assertNameNotPresent(objects);
  }

  @Override
  public void testExcludeName() throws IOException {
    List<TestObject> objects = getObjects("!name");

    assertIdPresent(objects);
    assertNameNotPresent(objects);
  }

  @Override
  public void testCommaSeparated() throws IOException {
    List<TestObject> objects = getObjects("id,name");

    assertIdPresent(objects);
    assertNamePresent(objects);
  }
}
