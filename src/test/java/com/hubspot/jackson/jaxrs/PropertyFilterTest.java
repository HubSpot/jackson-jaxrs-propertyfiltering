package com.hubspot.jackson.jaxrs;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Arrays;
import org.junit.Test;

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

  private static JsonNode filter(String... properties) {
    JsonNode node = node();

    new PropertyFilter(Arrays.asList(properties)).filter(node);
    return node;
  }

  private static JsonNode node() {
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
