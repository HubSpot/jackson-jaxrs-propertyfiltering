package com.hubspot.jackson.jaxrs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.hubspot.jackson.jaxrs.util.Helper;
import java.io.IOException;
import java.net.URL;
import org.assertj.core.util.Strings;
import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class BaseTest {

  private static ObjectReader reader = new ObjectMapper().reader();
  private static Server server;
  private static int port;

  @BeforeClass
  public static void start() throws Exception {
    server = Helper.INSTANCE.startServer();
    port = Helper.INSTANCE.getPort(server);
  }

  @AfterClass
  public static void stop() throws Exception {
    if (server != null) {
      server.stop();
    }
  }

  protected <T> T getObjects(
    TypeReference<T> typeReference,
    String path,
    String queryParamName,
    String... queryParams
  ) throws IOException {
    String urlString = "http://localhost:" + port + "/test" + path;
    if (queryParams.length > 0) {
      urlString +=
      "?" +
      queryParamName +
      "=" +
      Strings.join(queryParams).with("&" + queryParamName + "=");
    }

    URL url = new URL(urlString);

    return reader.forType(typeReference).readValue(url.openStream());
  }
}
