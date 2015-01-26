package com.hubspot.jackson.jaxrs;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PropertyFilterTest {
  private static final ObjectMapper mapper = new ObjectMapper();

  @Test
  public void testNoFilters() {
    JsonNode node = filter();

    assertThat(node.get("propA").get("key1").textValue()).isEqualTo("value1");
    assertThat(node.get("propA").get("key2").textValue()).isEqualTo("value2");
    assertThat(node.get("propB").get("key1").textValue()).isEqualTo("value1");
    assertThat(node.get("propB").get("key2").textValue()).isEqualTo("value2");
  }

  @Test
  public void testInclude() {
    JsonNode node = filter("propA");

    assertThat(node.get("propA").get("key1").textValue()).isEqualTo("value1");
    assertThat(node.get("propA").get("key2").textValue()).isEqualTo("value2");
    assertThat(node.has("propB")).isFalse();
  }

  @Test
  public void testExclude() {
    JsonNode node = filter("!propA");

    assertThat(node.has("propA")).isFalse();
    assertThat(node.get("propB").get("key1").textValue()).isEqualTo("value1");
    assertThat(node.get("propB").get("key2").textValue()).isEqualTo("value2");
  }

  @Test
  public void testNestedInclude() {
    JsonNode node = filter("propA.key1", "propB.key2");

    assertThat(node.get("propA").get("key1").textValue()).isEqualTo("value1");
    assertThat(node.get("propA").has("key2")).isFalse();
    assertThat(node.get("propB").has("key1")).isFalse();
    assertThat(node.get("propB").get("key2").textValue()).isEqualTo("value2");
  }

  @Test
  public void testNestedExclude() {
    JsonNode node = filter("!propA.key1", "!propB.key2");

    assertThat(node.get("propA").has("key1")).isFalse();
    assertThat(node.get("propA").get("key2").textValue()).isEqualTo("value2");
    assertThat(node.get("propB").get("key1").textValue()).isEqualTo("value1");
    assertThat(node.get("propB").has("key2")).isFalse();
  }

  @Test
  public void testWildcardNestedInclude() {
    JsonNode input = node().set("propC", mapper.createObjectNode().put("key3", "value3"));
    JsonNode node = filter(input, "*.key1");

    assertThat(node.get("propA").get("key1").textValue()).isEqualTo("value1");
    assertThat(node.get("propA").has("key2")).isFalse();
    assertThat(node.get("propB").get("key1").textValue()).isEqualTo("value1");
    assertThat(node.get("propB").has("key2")).isFalse();
    assertThat(node.get("propC")).hasSize(0);
  }

  @Test
  public void testWildcardNestedExclude() {
    JsonNode node = filter("!*.key2");

    assertThat(node.get("propA").get("key1").textValue()).isEqualTo("value1");
    assertThat(node.get("propA").has("key2")).isFalse();
    assertThat(node.get("propB").get("key1").textValue()).isEqualTo("value1");
    assertThat(node.get("propB").has("key2")).isFalse();
  }

  @Test
  public void testReadmeWildcardInclusion() throws Exception {
    String json = "{" +
        "  \"id\": 54," +
        "  \"name\": \"Object\"," +
        "  \"child\": {" +
        "    \"id\": 96," +
        "    \"name\": \"Child Object\"" +
        "  }" +
        "}";
    JsonNode node = filter(mapper.readTree(json), "*.id");

    assertThat(node.get("id").intValue()).isEqualTo(54);
    assertThat(node.get("name").textValue()).isEqualTo("Object");
    assertThat(node.get("child").get("id").intValue()).isEqualTo(96);
    assertThat(node.get("child").has("name")).isFalse();
  }

  @Test
  public void testReadmeWildcardExclusion() throws Exception {
    String json = "{" +
        "  \"id\": 54," +
        "  \"name\": \"Object\"," +
        "  \"child\": {" +
        "    \"id\": 96," +
        "    \"name\": \"Child Object\"" +
        "  }" +
        "}";
    JsonNode node = filter(mapper.readTree(json), "!*.id");

    assertThat(node.get("id").intValue()).isEqualTo(54);
    assertThat(node.get("name").textValue()).isEqualTo("Object");
    assertThat(node.get("child").has("id")).isFalse();
    assertThat(node.get("child").get("name").textValue()).isEqualTo("Child Object");
  }

  private static JsonNode filter(String... properties) {
    return filter(node(), properties);
  }

  private static JsonNode filter(JsonNode node, String... properties) {
    new PropertyFilter(Arrays.asList(properties)).filter(node);
    return node;
  }

  private static ObjectNode node() {
    ObjectNode node = mapper.createObjectNode();
    node.put("propA", propertyNode());
    node.put("propB", propertyNode());

    return node;
  }

  private static JsonNode propertyNode() {
    ObjectNode node = mapper.createObjectNode();
    node.put("key1", "value1");
    node.put("key2", "value2");

    return node;
  }
}
