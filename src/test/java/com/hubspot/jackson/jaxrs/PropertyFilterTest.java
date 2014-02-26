package com.hubspot.jackson.jaxrs;

import org.junit.Test;

import java.util.Arrays;

import static org.fest.assertions.api.Assertions.assertThat;

public class PropertyFilterTest {

  @Test
  public void testNoFilters() {
    PropertyFilter filter = filterOf();

    assertThat(filter.includes("propA")).isTrue();
    assertThat(filter.includes("propB")).isTrue();
  }

  @Test
  public void testInclude() {
    PropertyFilter filter = filterOf("propA");

    assertThat(filter.includes("propA")).isTrue();
    assertThat(filter.includes("propB")).isFalse();
  }

  @Test
  public void testExclude() {
    PropertyFilter filter = filterOf("!propA");

    assertThat(filter.includes("propA")).isFalse();
    assertThat(filter.includes("propB")).isTrue();
  }

  @Test
  public void testIncludeExclude() {
    PropertyFilter filter = filterOf("propA", "!propA");

    assertThat(filter.includes("propA")).isTrue();
    assertThat(filter.includes("propB")).isFalse();
  }

  private static PropertyFilter filterOf(String... properties) {
    return new PropertyFilter(Arrays.asList(properties));
  }
}
