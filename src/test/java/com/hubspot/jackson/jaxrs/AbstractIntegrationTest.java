package com.hubspot.jackson.jaxrs;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hubspot.jackson.jaxrs.util.TestResource.TestObject;
import java.io.IOException;
import java.util.List;
import org.junit.Test;

public abstract class AbstractIntegrationTest extends BaseTest {

  private static final TypeReference<List<TestObject>> LIST_TYPE = new TypeReference<List<TestObject>>() {};

  @Test
  public void testNoFiltering() throws IOException {
    List<TestObject> objects = getObjects();

    assertIdPresent(objects);
    assertNamePresent(objects);
  }

  @Test
  public void testIncludeId() throws IOException {
    List<TestObject> objects = getObjects("id");

    assertIdPresent(objects);
    assertNameNotPresent(objects);
  }

  @Test
  public void testIncludeName() throws IOException {
    List<TestObject> objects = getObjects("name");

    assertIdNotPresent(objects);
    assertNamePresent(objects);
  }

  @Test
  public void testExcludeId() throws IOException {
    List<TestObject> objects = getObjects("!id");

    assertIdNotPresent(objects);
    assertNamePresent(objects);
  }

  @Test
  public void testExcludeName() throws IOException {
    List<TestObject> objects = getObjects("!name");

    assertIdPresent(objects);
    assertNameNotPresent(objects);
  }

  @Test
  public void testCommaSeparated() throws IOException {
    List<TestObject> objects = getObjects("id,name");

    assertIdPresent(objects);
    assertNamePresent(objects);
  }

  protected abstract String path();

  protected abstract String queryParamName();

  protected List<TestObject> getObjects(String... queryParams) throws IOException {
    return super.getObjects(LIST_TYPE, path(), queryParamName(), queryParams);
  }

  protected void assertIdPresent(List<TestObject> objects) {
    assertThat(objects).hasSize(10);
    for (int i = 0; i < 10; i++) {
      assertThat(objects.get(i).getId()).isEqualTo(i);
    }
  }

  protected void assertNamePresent(List<TestObject> objects) {
    assertThat(objects).hasSize(10);
    for (int i = 0; i < 10; i++) {
      assertThat(objects.get(i).getName()).isEqualTo("Test " + i);
    }
  }

  protected void assertIdNotPresent(List<TestObject> objects) {
    assertThat(objects).hasSize(10);
    for (int i = 0; i < 10; i++) {
      assertThat(objects.get(i).getId()).isNull();
    }
  }

  protected void assertNameNotPresent(List<TestObject> objects) {
    assertThat(objects).hasSize(10);
    for (int i = 0; i < 10; i++) {
      assertThat(objects.get(i).getName()).isNull();
    }
  }
}
