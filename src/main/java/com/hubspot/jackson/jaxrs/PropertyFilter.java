package com.hubspot.jackson.jaxrs;

import java.util.Collection;
import java.util.Collections;
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
    for (String property : properties) {
      if (!property.isEmpty()) {
        filter.addProperty(property);
      }
    }
  }

  public boolean hasFilters() {
    return filter.hasFilters();
  }

  public void filter(JsonNode node) {
    filter.filter(node);
  }

  @Override
  public String toString() {
    return "PropertyFilter[" + filter + "]";
  }

  private static class NestedPropertyFilter {
    private static final String WILDCARD = "*";

    private final Set<String> includedProperties = new HashSet<String>();
    private final Set<String> excludedProperties = new HashSet<String>();
    private final Map<String, NestedPropertyFilter> nestedProperties = new HashMap<String, NestedPropertyFilter>();

    public void addProperty(String property) {
      boolean excluded = property.startsWith("!");
      if (excluded) {
        property = property.substring(1);
      }

      int indexOfDot = property.indexOf('.');
      if (indexOfDot > -1) {
        String prefix = property.substring(0, indexOfDot);
        String suffix = property.substring(indexOfDot + 1);

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
      if (!includedProperties.isEmpty() && !includedProperties.contains(WILDCARD)) {
        object.retain(includedProperties);
      }

      if (excludedProperties.contains(WILDCARD)) {
        object.removeAll();
      } else {
        object.remove(excludedProperties);
      }

      for (Entry<String, NestedPropertyFilter> entry : nestedProperties.entrySet()) {
        Iterable<JsonNode> nodes;

        if (WILDCARD.equals(entry.getKey())) {
          nodes = object;
        } else {
          nodes = Collections.singletonList(object.get(entry.getKey()));
        }

        for (JsonNode node : nodes) {
          if (node != null) {
            entry.getValue().filter(node);
          }
        }
      }
    }

    @Override
    public String toString() {
      return "included: " + includedProperties
          + " excluded: " + excludedProperties
          + " nested: " + nestedProperties;
    }
  }
}
