package com.hubspot.jackson.jaxrs.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.hubspot.jackson.jaxrs.PropertyFiltering;

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

  @GET
  @Path("/nested")
  @PropertyFiltering
  public TestNestedObject getNestedObject() {
    return getNestedObject(1);
  }

  @GET
  @Path("/nested/object")
  @PropertyFiltering
  public Map<Long, TestObject> getNestedObjectsMap() {
    Map<Long, TestObject> result = new HashMap<>();
    for (TestNestedObject testNestedObject : getNestedObjects()) {
      result.put(testNestedObject.getId(), testNestedObject);
    }
    return result;
  }

  @GET
  @Path("/array/list")
  @PropertyFiltering
  public List<TestArrayObject> getArrayObjectsList() {
    return getArrayObjects();
  }

  @GET
  @Path("/array")
  @PropertyFiltering
  public TestArrayObject getArrayObject() {
    return getArrayObject(1);
  }

  @GET
  @Path("/array/object")
  @PropertyFiltering
  public Map<Long, TestArrayObject> getArrayObjectsMap() {
    Map<Long, TestArrayObject> result = new HashMap<>();
    for (TestArrayObject testArrayObject : getArrayObjects()) {
      result.put(testArrayObject.getId(), testArrayObject);
    }
    return result;
  }

  @GET
  @Path("/prefix")
  @PropertyFiltering(prefix = "nested")
  public TestNestedObject getPrefixedNestedObjectWithoutPeriod() {
    return getNestedObject(1);
  }

  @GET
  @Path("/prefix/period")
  @PropertyFiltering(prefix = "nested.")
  public TestNestedObject getPrefixedNestedObjectWithPeriod() {
    return getNestedObject(1);
  }

  private static List<TestObject> getObjects() {
    List<TestObject> objects = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      objects.add(new TestObject((long) i, "Test " + i));
    }

    return objects;
  }

  private static TestNestedObject getNestedObject(long i) {
    return new TestNestedObject(i,
          "Test " + i,
          new TestObject(i * 100, "Nested Test " + i * 100),
          new TestObject(i * 1_000, "SecondNested Test " + i * 1_000));
  }

  private static List<TestNestedObject> getNestedObjects() {
    List<TestNestedObject> objects = new ArrayList<>();
    for (long i = 0; i < 10; i++) {
      objects.add(getNestedObject(i));
    }

    return objects;
  }

  private static TestArrayObject getArrayObject(long i) {
    List<TestNestedObject> nested = new ArrayList<>();
    for (long j = i; j < i + 10; j++) {
      nested.add(getNestedObject(j));
    }

    return new TestArrayObject(i, "Test " + i, nested);
  }

  private static List<TestArrayObject> getArrayObjects() {
    List<TestArrayObject> objects = new ArrayList<>();
    for (long i = 0; i < 10; i++) {
      objects.add(getArrayObject(i));
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
    private final TestObject secondNested;

    public TestNestedObject(@JsonProperty("id") Long id, @JsonProperty("name") String name, @JsonProperty("nested") TestObject nested, @JsonProperty("secondNested") TestObject secondNested) {
      super(id, name);
      this.nested = nested;
      this.secondNested = secondNested;
    }

    public TestObject getNested() {
      return nested;
    }

    public TestObject getSecondNested() {
      return secondNested;
    }
  }

  public static class TestArrayObject extends TestObject {
    private final List<TestNestedObject> nested;

    public TestArrayObject(@JsonProperty("id") Long id, @JsonProperty("name") String name, @JsonProperty("nested") List<TestNestedObject> nested) {
      super(id, name);
      this.nested = nested;
    }

    public List<TestNestedObject> getNested() {
      return nested;
    }
  }
}
