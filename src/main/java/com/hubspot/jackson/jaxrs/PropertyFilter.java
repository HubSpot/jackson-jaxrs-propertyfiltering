package com.hubspot.jackson.jaxrs;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PropertyFilter {
  private final NestedPropertyFilter filter = new NestedPropertyFilter();

  public PropertyFilter(Collection<String> properties) {
    if (properties != null) {
      for (String property : properties) {
        property = property.trim();

        if (!property.isEmpty()) {
          filter.addProperty(property);
        }
      }
    }
  }

  public boolean hasFilters() {
    return filter.hasFilters();
  }

  public void filter(JsonNode node) {
    filter.filter(node);
  }

  private static class NestedPropertyFilter {
    private final Set<String> includedProperties = new HashSet<String>();
    private final Set<String> excludedProperties = new HashSet<String>();
    private final Map<String, NestedPropertyFilter> nestedProperties = new HashMap<String, NestedPropertyFilter>();

    public void addProperty(String property) {
      boolean excluded = property.startsWith("!");
      if (excluded) {
        property = property.substring(1);
      }

      if (property.contains(".")) {
        String prefix = property.substring(0, property.indexOf('.'));
        String suffix = property.substring(property.indexOf('.') + 1);

        NestedPropertyFilter nestedFilter = nestedProperties.get(prefix);
        if (nestedFilter == null) {
          nestedFilter = new NestedPropertyFilter();
          nestedProperties.put(prefix, nestedFilter);
        }

        if (excluded) {
          nestedFilter.addProperty("!" + suffix);
        } else {
          nestedFilter.addProperty(suffix);
          includedProperties.add(prefix);
        }
      } else if (excluded) {
        excludedProperties.add(property);
      } else {
        includedProperties.add(property);
      }
    }

    public boolean hasFilters() {
      return !(includedProperties.isEmpty() && excludedProperties.isEmpty() && nestedProperties.isEmpty());
    }

    public void filter(JsonNode node) {
      if (node.isObject()) {
        filter((ObjectNode) node);
      } else if (node.isArray()) {
        filter((ArrayNode) node);
      }
    }

    private void filter(ArrayNode array) {
      for (JsonNode node : array) {
        filter(node);
      }
    }

    private void filter(ObjectNode object) {
      if (!includedProperties.isEmpty()) {
        object.retain(includedProperties);
      }

      object.remove(excludedProperties);

      for (Entry<String, NestedPropertyFilter> entry : nestedProperties.entrySet()) {
        JsonNode node = object.get(entry.getKey());

        if (node != null) {
          entry.getValue().filter(node);
        }
      }
    }
  }
}
