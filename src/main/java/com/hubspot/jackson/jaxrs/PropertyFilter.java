package com.hubspot.jackson.jaxrs;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;

import com.fasterxml.jackson.core.filter.TokenFilter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PropertyFilter extends TokenFilter {
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

  public boolean matches(String property) {
    return filter.matches(property);
  }

  @Override
  public TokenFilter includeProperty(String name) {
    return filter.includeProperty(name);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", "PropertyFilter[", "]")
        .add("filter=" + filter)
        .toString();
  }

  private void applyWildcardsToNamedProperties(NestedPropertyFilter root) {
    if (root.nestedProperties.containsKey("*")) {
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

  private static class NestedPropertyFilter extends TokenFilter {
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

    public void mergeFilters(NestedPropertyFilter other) {
      includedProperties.addAll(other.includedProperties);
      excludedProperties.addAll(other.excludedProperties);
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

    public boolean matches(String property) {
      if (!hasFilters()) {
        return true;
      }

      final String prefix;
      final String suffix;
      if (property.contains(".")) {
        prefix = property.substring(0, property.indexOf('.'));
        suffix = property.substring(property.indexOf('.') + 1);
      } else {
        prefix = property;
        suffix = null;
      }

      if (excludedProperties.contains("*") || excludedProperties.contains(prefix)) {
        return false;
      } else if (includedProperties.contains("*") || includedProperties.contains(prefix)) {
        if (suffix != null && nestedProperties.containsKey(prefix)) {
          return nestedProperties.get(prefix).matches(suffix);
        } else if (nestedProperties.containsKey("*")) {
          return suffix != null && nestedProperties.get("*").matches(suffix);
        } else {
          return true;
        }
      } else if (suffix != null) {
        if (nestedProperties.containsKey(prefix)) {
          return nestedProperties.get(prefix).matches(suffix);
        } else if (nestedProperties.containsKey("*")) {
          return nestedProperties.get("*").matches(suffix);
        } else {
          return includedProperties.isEmpty();
        }
      } else {
        return includedProperties.isEmpty();
      }
    }

    private void filter(ArrayNode array) {
      for (JsonNode node : array) {
        filter(node);
      }
    }

    @Override
    public TokenFilter includeProperty(String name) {
      if (!includedProperties.isEmpty() && !includedProperties.contains("*") && !includedProperties.contains(name)) {
        return null;
      } else if (excludedProperties.contains("*") || excludedProperties.contains(name)) {
        return null;
      } else if (nestedProperties.containsKey(name)) {
        return nestedProperties.get(name);
      } else if (nestedProperties.containsKey("*")) {
        return nestedProperties.get("*");
      } else {
        return TokenFilter.INCLUDE_ALL;
      }
    }

    @Override
    public String toString() {
      return new StringJoiner(", ", "NestedPropertyFilter[", "]")
          .add("includedProperties=" + includedProperties)
          .add("excludedProperties=" + excludedProperties)
          .add("nestedProperties=" + nestedProperties)
          .toString();
    }

    private void filter(ObjectNode object) {
      if (!includedProperties.isEmpty() && !includedProperties.contains("*")) {
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
