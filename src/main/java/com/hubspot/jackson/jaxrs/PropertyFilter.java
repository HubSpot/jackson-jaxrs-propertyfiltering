package com.hubspot.jackson.jaxrs;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

    applyWildcardsToNamedProperties(filter);
  }

  public boolean hasFilters() {
    return filter.hasFilters();
  }

  public void filter(JsonNode node) {
    filter.filter(node);
  }

  private void applyWildcardsToNamedProperties(NestedPropertyFilter root) {
    if (root.wildcardIncluded || root.wildcardExcluded) {
      NestedPropertyFilter wildcardFilters = root.nestedProperties.get("*");

      for (Entry<String, NestedPropertyFilter> wildcardSibling : root.nestedProperties.entrySet()) {
        wildcardSibling.getValue().mergeFilters(wildcardFilters);
      }
    } else {
      for (NestedPropertyFilter child : root.nestedProperties.values()) {
        applyWildcardsToNamedProperties(child);
      }
    }
  }

  private static class NestedPropertyFilter {
    private final Set<String> includedProperties = new HashSet<String>();
    private final Set<String> excludedProperties = new HashSet<String>();
    private final Map<String, NestedPropertyFilter> nestedProperties = new HashMap<String, NestedPropertyFilter>();
    private boolean wildcardIncluded = false;
    private boolean wildcardExcluded = false;

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
          if (prefix.equals("*")) {
            wildcardIncluded = true;
          } else {
            includedProperties.add(prefix);
          }
        }
      } else if (excluded) {
        if (property.equals("*")) {
          wildcardExcluded = true;
        } else {
          excludedProperties.add(property);
        }
      } else {
        if (property.equals("*")) {
          wildcardIncluded = true;
        } else {
          includedProperties.add(property);
        }
      }
    }

    public void mergeFilters(NestedPropertyFilter other) {
      includedProperties.addAll(other.includedProperties);
      excludedProperties.addAll(other.excludedProperties);
    }

    public boolean hasFilters() {
      return !(includedProperties.isEmpty() && excludedProperties.isEmpty() && nestedProperties.isEmpty() && wildcardIncluded && wildcardExcluded);
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
      if (!includedProperties.isEmpty() && !wildcardIncluded) {
        object.retain(includedProperties);
      }

      if (excludedProperties.contains("*")) {
        object.removeAll();
      } else {
        object.remove(excludedProperties);
      }

      Iterator<Entry<String, JsonNode>> fields = object.fields();
      while (fields.hasNext()) {
        Entry<String, JsonNode> field = fields.next();

        if (nestedProperties.containsKey(field.getKey())) {
          nestedProperties.get(field.getKey()).filter(field.getValue());
        } else if (nestedProperties.containsKey("*")) {
          nestedProperties.get("*").filter(field.getValue());
        }
      }
    }
  }
}
