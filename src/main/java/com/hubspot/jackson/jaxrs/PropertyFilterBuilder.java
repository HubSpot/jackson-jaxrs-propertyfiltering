package com.hubspot.jackson.jaxrs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.UriInfo;

public class PropertyFilterBuilder {
  private final UriInfo uriInfo;
  private String queryParamName;
  private String[] alwaysInclude;
  private String prefix;

  private PropertyFilterBuilder(UriInfo uriInfo) {
    this.uriInfo = uriInfo;
    this.queryParamName = "property";
    this.alwaysInclude = new String[0];
    this.prefix = "";
  }

  public static PropertyFilterBuilder newBuilder(UriInfo uriInfo) {
    return new PropertyFilterBuilder(uriInfo);
  }

  public PropertyFilter forAnnotation(PropertyFiltering annotation) {
    if (annotation == null) {
      return new PropertyFilter(Collections.<String>emptyList());
    } else {
      return usingQueryParam(annotation.using())
          .alwaysInclude(annotation.always())
          .applyPrefix(annotation.prefix())
          .build();
    }
  }

  public PropertyFilterBuilder usingQueryParam(String queryParamName) {
    this.queryParamName = queryParamName;
    return this;
  }

  public PropertyFilterBuilder alwaysInclude(String... properties) {
    this.alwaysInclude = properties;
    return this;
  }

  public PropertyFilterBuilder applyPrefix(String prefix) {
    this.prefix = prefix;
    return this;
  }

  public PropertyFilter build() {
    List<String> properties = new ArrayList<>();

    List<String> values = uriInfo.getQueryParameters().get(queryParamName);

    String prefix = this.prefix;
    if (!prefix.isEmpty() && !prefix.endsWith(".")) {
      prefix = prefix + ".";
    }

    if (values != null) {
      for (String value : values) {
        String[] parts = value.split(",");
        for (String part : parts) {
          part = part.trim();
          if (!part.isEmpty()) {
            properties.add(prefix + part);
          }
        }
      }
    }

    properties.addAll(Arrays.asList(alwaysInclude));

    return new PropertyFilter(properties);
  }
}
