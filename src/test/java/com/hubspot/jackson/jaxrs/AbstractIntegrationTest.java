package com.hubspot.jackson.jaxrs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.hubspot.jackson.jaxrs.util.Helper;
import com.hubspot.jackson.jaxrs.util.TestResource.TestObject;
import org.assertj.core.util.Strings;
import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractIntegrationTest {
  private static ObjectReader reader;
  private static Server server;
  private static int port;
  private static TypeReference<List<TestObject>> listType;

  @BeforeClass
  public static void start() throws Exception {
    reader = new ObjectMapper().reader();
    server = Helper.INSTANCE.startServer();
    port = Helper.INSTANCE.getPort(server);
    listType = new TypeReference<List<TestObject>>() { };
  }

  @AfterClass
  public static void stop() throws Exception {
    if (server != null) {
      server.stop();
    }
  }

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
    String urlString = "http://localhost:" + port + "/test" + path();
    if (queryParams.length > 0) {
      urlString += "?" + queryParamName() +"=" + Strings.join(queryParams).with("&" + queryParamName() + "=");
    }

    URL url = new URL(urlString);

    return reader.withType(listType).readValue(url.openStream());
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
