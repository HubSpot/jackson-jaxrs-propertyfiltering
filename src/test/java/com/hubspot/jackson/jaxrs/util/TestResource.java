package com.hubspot.jackson.jaxrs.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hubspot.jackson.jaxrs.PropertyFiltering;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
public class TestResource {

  @GET
  @PropertyFiltering
  public List<TestObject> getObjectsStandard() {
    return getObjects();
  }

  @GET
  @Path("/custom")
  @PropertyFiltering(using = "custom")
  public List<TestObject> getObjectsCustomQueryParam() {
    return getObjects();
  }

  private static List<TestObject> getObjects() {
    List<TestObject> objects = new ArrayList<TestObject>();
    for (int i = 0; i < 10; i++) {
      objects.add(new TestObject((long) i, "Test " + i));
    }

    return objects;
  }

  public static class TestObject {
    private final Long id;
    private final String name;

    public TestObject(@JsonProperty("id") Long id, @JsonProperty("name") String name) {
      this.id = id;
      this.name = name;
    }

    public Long getId() {
      return id;
    }

    public String getName() {
      return name;
    }
  }
}
