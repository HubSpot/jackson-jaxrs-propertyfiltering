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

  @GET
  @Path("/wrapped")
  @PropertyFiltering(on = "objects")
  public TestObjectWrapper getObjectsWrapped() {
    return new TestObjectWrapper(getObjects(), 10, true);
  }

  @GET
  @Path("/wrapped/custom")
  @PropertyFiltering(on = "objects", using = "custom")
  public TestObjectWrapper getObjectsWrappedAndCustomQueryParam() {
    return new TestObjectWrapper(getObjects(), 10, true);
  }

  private static List<TestObject> getObjects() {
    List<TestObject> objects = new ArrayList<TestObject>();
    for (int i = 0; i < 10; i++) {
      objects.add(new TestObject((long) i, "Test " + i));
    }

    return objects;
  }

  public static class TestObjectWrapper {
    private final List<TestObject> objects;
    private final int count;
    private final boolean hasMore;

    public TestObjectWrapper(@JsonProperty("objects") List<TestObject> objects,
                             @JsonProperty("count") int count,
                             @JsonProperty("hasMore") boolean hasMore) {
      this.objects = objects;
      this.count = count;
      this.hasMore = hasMore;
    }

    public List<TestObject> getObjects() {
      return objects;
    }

    public int getCount() {
      return count;
    }

    public boolean getHasMore() {
      return hasMore;
    }
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
