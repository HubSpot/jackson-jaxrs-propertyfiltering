package com.hubspot.jackson.jaxrs.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.hubspot.jackson.jaxrs.PropertyFiltering;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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

  @GET
  @Path("/always")
  @PropertyFiltering(always = "id")
  public List<TestObject> getObjectsAlwaysProperties() {
    return getObjects();
  }

  @GET
  @Path("/view")
  @PropertyFiltering
  @JsonView(TestView.class)
  public List<TestObject> getObjectsWithView() {
    return getObjects();
  }

  @GET
  @Path("/nested/list")
  @PropertyFiltering
  public List<TestNestedObject> getNestedObjectsList() {
    return getNestedObjects();
  }

  private static List<TestObject> getObjects() {
    List<TestObject> objects = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      objects.add(new TestObject((long) i, "Test " + i));
    }

    return objects;
  }

  private static List<TestNestedObject> getNestedObjects() {
    List<TestNestedObject> objects = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      objects.add(new TestNestedObject((long) i * 100, "Test" + i * 100, new TestObject((long) i, "Test " + i)));
    }

    return objects;
  }

  public interface TestView {}
  public interface OtherView {}

  public static class TestObject {
    private final Long id;
    @JsonView(OtherView.class)
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

  public static class TestNestedObject extends TestObject {
    private final TestObject nested;

    public TestNestedObject(@JsonProperty("id") Long id, @JsonProperty("name") String name, @JsonProperty("nested") TestObject nested) {
      super(id, name);
      this.nested = nested;
    }

    public TestObject getNested() {
      return nested;
    }
  }
}
