package com.hubspot.jackson.jaxrs;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;

public class PropertyFilterMatchesTest {

  @Test
  public void itReturnsTrueWhenNoPropertiesSet() {
    PropertyFilter filter = filter();

    assertThat(filter.matches("propA")).isTrue();
    assertThat(filter.matches("propA.key1")).isTrue();
    assertThat(filter.matches("propB")).isTrue();
    assertThat(filter.matches("propB.key1")).isTrue();
  }

  @Test
  public void itMatchesChildrenOfIncludedProperties() {
    PropertyFilter filter = filter("propA");

    assertThat(filter.matches("propA")).isTrue();
    assertThat(filter.matches("propA.key1")).isTrue();
    assertThat(filter.matches("propB")).isFalse();
    assertThat(filter.matches("propB.key1")).isFalse();
  }

  @Test
  public void itDoesntMatchChildrenOfExcludedProperties() {
    PropertyFilter filter = filter("!propA");

    assertThat(filter.matches("propA")).isFalse();
    assertThat(filter.matches("propA.key1")).isFalse();
    assertThat(filter.matches("propB")).isTrue();
    assertThat(filter.matches("propB.key1")).isTrue();
  }

  @Test
  public void itMatchesNestedIncludes() {
    PropertyFilter filter = filter("propA.key1", "propB.key2");

    assertThat(filter.matches("propA")).isTrue();
    assertThat(filter.matches("propA.key1")).isTrue();
    assertThat(filter.matches("propA.key2")).isFalse();
    assertThat(filter.matches("propB")).isTrue();
    assertThat(filter.matches("propB.key1")).isFalse();
    assertThat(filter.matches("propB.key2")).isTrue();
  }

  @Test
  public void itMatchesNestedExcludes() {
    PropertyFilter filter = filter("!propA.key1", "!propB.key2");

    assertThat(filter.matches("propA")).isTrue();
    assertThat(filter.matches("propA.key1")).isFalse();
    assertThat(filter.matches("propA.key2")).isTrue();
    assertThat(filter.matches("propB")).isTrue();
    assertThat(filter.matches("propB.key1")).isTrue();
    assertThat(filter.matches("propB.key2")).isFalse();
  }

  @Test
  public void testNestedObject() {
    PropertyFilter filter = filter("*.name");

    assertThat(filter.matches("id")).isFalse();
    assertThat(filter.matches("id.other")).isFalse();
    assertThat(filter.matches("name")).isFalse();
    assertThat(filter.matches("name.other")).isFalse();
    assertThat(filter.matches("1")).isFalse();
    assertThat(filter.matches("1.id")).isFalse();
    assertThat(filter.matches("1.name")).isTrue();
    assertThat(filter.matches("1.name.other")).isTrue();
    assertThat(filter.matches("2")).isFalse();
    assertThat(filter.matches("2.id")).isFalse();
    assertThat(filter.matches("2.name")).isTrue();
    assertThat(filter.matches("2.name.other")).isTrue();
  }

  @Test
  public void testNestedWithoutPrefix() {
    PropertyFilter filter = filter("id", "name");

    assertThat(filter.matches("id")).isTrue();
    assertThat(filter.matches("id.other")).isTrue();
    assertThat(filter.matches("name")).isTrue();
    assertThat(filter.matches("name.other")).isTrue();
    assertThat(filter.matches("1")).isFalse();
    assertThat(filter.matches("1.id")).isFalse();
    assertThat(filter.matches("1.name")).isFalse();
    assertThat(filter.matches("1.name.other")).isFalse();
    assertThat(filter.matches("2")).isFalse();
    assertThat(filter.matches("2.id")).isFalse();
    assertThat(filter.matches("2.name")).isFalse();
    assertThat(filter.matches("2.name.other")).isFalse();
  }

  @Test
  public void testNestedExclusions() {
    PropertyFilter filter = filter("!*.name");

    assertThat(filter.matches("id")).isTrue();
    assertThat(filter.matches("id.other")).isTrue();
    assertThat(filter.matches("name")).isTrue();
    assertThat(filter.matches("name.other")).isTrue();
    assertThat(filter.matches("1")).isTrue();
    assertThat(filter.matches("1.id")).isTrue();
    assertThat(filter.matches("1.name")).isFalse();
    assertThat(filter.matches("1.name.other")).isFalse();
    assertThat(filter.matches("2")).isTrue();
    assertThat(filter.matches("2.id")).isTrue();
    assertThat(filter.matches("2.name")).isFalse();
    assertThat(filter.matches("2.name.other")).isFalse();
  }

  @Test
  public void testSecondLevelWildcard() {
    PropertyFilter filter = filter("9.*");

    assertThat(filter.matches("id")).isFalse();
    assertThat(filter.matches("id.other")).isFalse();
    assertThat(filter.matches("name")).isFalse();
    assertThat(filter.matches("name.other")).isFalse();
    assertThat(filter.matches("1")).isFalse();
    assertThat(filter.matches("1.id")).isFalse();
    assertThat(filter.matches("1.name")).isFalse();
    assertThat(filter.matches("1.name.other")).isFalse();
    assertThat(filter.matches("2")).isFalse();
    assertThat(filter.matches("2.id")).isFalse();
    assertThat(filter.matches("2.name")).isFalse();
    assertThat(filter.matches("2.name.other")).isFalse();
    assertThat(filter.matches("9")).isTrue();
    assertThat(filter.matches("9.id")).isTrue();
    assertThat(filter.matches("9.name")).isTrue();
    assertThat(filter.matches("9.name.other")).isTrue();
  }

  @Test
  public void testSecondLevelWildcardExclusion() {
    PropertyFilter filter = filter("!9.*");

    assertThat(filter.matches("id")).isTrue();
    assertThat(filter.matches("id.other")).isTrue();
    assertThat(filter.matches("name")).isTrue();
    assertThat(filter.matches("name.other")).isTrue();
    assertThat(filter.matches("1")).isTrue();
    assertThat(filter.matches("1.id")).isTrue();
    assertThat(filter.matches("1.name")).isTrue();
    assertThat(filter.matches("1.name.other")).isTrue();
    assertThat(filter.matches("2")).isTrue();
    assertThat(filter.matches("2.id")).isTrue();
    assertThat(filter.matches("2.name")).isTrue();
    assertThat(filter.matches("2.name.other")).isTrue();
    assertThat(filter.matches("9")).isTrue();
    assertThat(filter.matches("9.id")).isFalse();
    assertThat(filter.matches("9.name")).isFalse();
    assertThat(filter.matches("9.name.other")).isFalse();
  }

  @Test
  public void testMiddleLevelWildcard() {
    PropertyFilter filter = filter("9.*.name");

    assertThat(filter.matches("id")).isFalse();
    assertThat(filter.matches("id.other")).isFalse();
    assertThat(filter.matches("name")).isFalse();
    assertThat(filter.matches("name.other")).isFalse();
    assertThat(filter.matches("1")).isFalse();
    assertThat(filter.matches("1.id")).isFalse();
    assertThat(filter.matches("1.name")).isFalse();
    assertThat(filter.matches("1.name.other")).isFalse();
    assertThat(filter.matches("2")).isFalse();
    assertThat(filter.matches("2.id")).isFalse();
    assertThat(filter.matches("2.name")).isFalse();
    assertThat(filter.matches("2.name.other")).isFalse();
    assertThat(filter.matches("9")).isTrue();
    assertThat(filter.matches("9.id")).isFalse();
    assertThat(filter.matches("9.name")).isFalse();
    assertThat(filter.matches("9.a.name")).isTrue();
    assertThat(filter.matches("9.a.name.other")).isTrue();
  }

  @Test
  public void testMiddleLevelWildcardExclusion() {
    PropertyFilter filter = filter("!9.*.name");

    assertThat(filter.matches("id")).isTrue();
    assertThat(filter.matches("id.other")).isTrue();
    assertThat(filter.matches("name")).isTrue();
    assertThat(filter.matches("name.other")).isTrue();
    assertThat(filter.matches("1")).isTrue();
    assertThat(filter.matches("1.id")).isTrue();
    assertThat(filter.matches("1.name")).isTrue();
    assertThat(filter.matches("1.name.other")).isTrue();
    assertThat(filter.matches("2")).isTrue();
    assertThat(filter.matches("2.id")).isTrue();
    assertThat(filter.matches("2.name")).isTrue();
    assertThat(filter.matches("2.name.other")).isTrue();
    assertThat(filter.matches("9")).isTrue();
    assertThat(filter.matches("9.id")).isTrue();
    assertThat(filter.matches("9.name")).isTrue();
    assertThat(filter.matches("9.a.name")).isFalse();
    assertThat(filter.matches("9.a.name.other")).isFalse();
  }

  @Test
  public void testNestedObjectWithMultiplePropertyLevels() {
    PropertyFilter filter = filter("*.name", "9.id");

    assertThat(filter.matches("id")).isFalse();
    assertThat(filter.matches("id.other")).isFalse();
    assertThat(filter.matches("name")).isFalse();
    assertThat(filter.matches("name.other")).isFalse();
    assertThat(filter.matches("1")).isFalse();
    assertThat(filter.matches("1.id")).isFalse();
    assertThat(filter.matches("1.name")).isTrue();
    assertThat(filter.matches("1.name.other")).isTrue();
    assertThat(filter.matches("2")).isFalse();
    assertThat(filter.matches("2.id")).isFalse();
    assertThat(filter.matches("2.name")).isTrue();
    assertThat(filter.matches("2.name.other")).isTrue();
    assertThat(filter.matches("9")).isFalse();
    assertThat(filter.matches("9.id")).isTrue();
    assertThat(filter.matches("9.id.other")).isTrue();
    assertThat(filter.matches("9.name")).isTrue();
    assertThat(filter.matches("9.a.name")).isFalse();
    assertThat(filter.matches("9.a.name.other")).isFalse();
  }

  private static PropertyFilter filter(String... properties) {
    return new PropertyFilter(Arrays.asList(properties));
  }
}
