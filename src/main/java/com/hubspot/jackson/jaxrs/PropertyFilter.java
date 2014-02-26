package com.hubspot.jackson.jaxrs;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PropertyFilter {
  private final Set<String> includedProperties;
  private final Set<String> excludedProperties;

  public PropertyFilter(Collection<String> properties) {
    Set<String> includedProperties = new HashSet<String>();
    Set<String> excludedProperties = new HashSet<String>();

    if (properties != null) {
      for (String property : properties) {
        property = property == null ? "" : property.trim();

        if (property.startsWith("!")) {
          excludedProperties.add(property.substring(1));
        } else if (!property.isEmpty()) {
          includedProperties.add(property);
        }
      }
    }

    // Don't exclude properties if they were explicitly included
    excludedProperties.removeAll(includedProperties);

    this.includedProperties = includedProperties;
    this.excludedProperties = excludedProperties;
  }

  public boolean includes(String property) {
    if (!includedProperties.isEmpty()) {
      return includedProperties.contains(property);
    } else {
      return !excludedProperties.contains(property);
    }
  }

  public boolean hasFilters() {
    return !(includedProperties.isEmpty() && excludedProperties.isEmpty());
  }

  public void filter(ArrayNode values) {
    for (JsonNode value : values) {
      if (value.isObject()) {
        filter((ObjectNode) value);
      }
    }
  }

  private void filter(ObjectNode value) {
    if (!includedProperties.isEmpty()) {
      value.retain(includedProperties);
    }

    value.remove(excludedProperties);
  }
}
