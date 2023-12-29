package com.hubspot.jackson.jaxrs;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.filter.TokenFilter;
import com.fasterxml.jackson.core.util.JsonGeneratorDelegate;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Clone of FilteringGeneratorDelegate that forces it to write empty objects and arrays
 */
public class PropertyFilteringJsonGenerator extends JsonGeneratorDelegate {

  private TokenFilter itemFilter;
  private PropertyFilteringTokenContext filterContext;

  public PropertyFilteringJsonGenerator(JsonGenerator generator, PropertyFilter filter) {
    super(generator, false);
    this.itemFilter = filter;
    this.filterContext = PropertyFilteringTokenContext.createRootContext(filter);
  }

  @Override
  public void writeStartArray() throws IOException {
    // First things first: whole-sale skipping easy
    if (itemFilter == null) {
      filterContext = filterContext.createChildArrayContext(null, false);
    } else {
      filterContext = filterContext.createChildArrayContext(itemFilter, true);
      delegate.writeStartArray();
    }
  }

  @Override
  public void writeStartArray(int size) throws IOException {
    if (itemFilter == null) {
      filterContext = filterContext.createChildArrayContext(null, false);
    } else {
      filterContext = filterContext.createChildArrayContext(itemFilter, true);
      delegate.writeStartArray(size);
    }
  }

  @Override
  public void writeStartArray(Object forValue) throws IOException {
    if (itemFilter == null) {
      filterContext = filterContext.createChildArrayContext(null, false);
    } else {
      filterContext = filterContext.createChildArrayContext(itemFilter, true);
      delegate.writeStartArray(forValue);
    }
  }

  @Override
  public void writeStartArray(Object forValue, int size) throws IOException {
    if (itemFilter == null) {
      filterContext = filterContext.createChildArrayContext(null, false);
    } else {
      filterContext = filterContext.createChildArrayContext(itemFilter, true);
      delegate.writeStartArray(forValue, size);
    }
  }

  @Override
  public void writeEndArray() throws IOException {
    filterContext = filterContext.closeArray(delegate);

    if (filterContext != null) {
      itemFilter = filterContext.getFilter();
    }
  }

  @Override
  public void writeStartObject() throws IOException {
    if (itemFilter == null) {
      filterContext = filterContext.createChildObjectContext(itemFilter, false);
    } else {
      filterContext = filterContext.createChildObjectContext(itemFilter, true);
      delegate.writeStartObject();
    }
  }

  @Override
  public void writeStartObject(Object forValue) throws IOException {
    if (itemFilter == null) {
      filterContext = filterContext.createChildObjectContext(itemFilter, false);
    } else {
      filterContext = filterContext.createChildObjectContext(itemFilter, true);
      delegate.writeStartObject(forValue);
    }
  }

  @Override
  public void writeStartObject(Object forValue, int size) throws IOException {
    if (itemFilter == null) {
      filterContext = filterContext.createChildObjectContext(itemFilter, false);
    } else {
      filterContext = filterContext.createChildObjectContext(itemFilter, true);
      delegate.writeStartObject(forValue, size);
    }
  }

  @Override
  public void writeEndObject() throws IOException {
    filterContext = filterContext.closeObject(delegate);
    if (filterContext != null) {
      itemFilter = filterContext.getFilter();
    }
  }

  @Override
  public void writeFieldName(String name) throws IOException {
    TokenFilter state = filterContext.setFieldName(name);
    if (state == null) {
      itemFilter = null;
    } else if (state == TokenFilter.INCLUDE_ALL) {
      itemFilter = state;
      delegate.writeFieldName(name);
    } else {
      state = state.includeProperty(name);
      itemFilter = state;
      if (state != null) {
        delegate.writeFieldName(name);
      }
    }
  }

  @Override
  public void writeFieldName(SerializableString name) throws IOException {
    TokenFilter state = filterContext.setFieldName(name.getValue());
    if (state == null) {
      itemFilter = null;
    } else if (state == TokenFilter.INCLUDE_ALL) {
      itemFilter = state;
      delegate.writeFieldName(name);
    } else {
      state = state.includeProperty(name.getValue());
      itemFilter = state;
      if (state != null) {
        delegate.writeFieldName(name);
      }
    }
  }

  @Override
  public void writeFieldId(long id) throws IOException {
    String idString = Long.toString(id);
    TokenFilter state = filterContext.setFieldName(idString);
    if (state == null) {
      itemFilter = null;
    } else if (state == TokenFilter.INCLUDE_ALL) {
      itemFilter = state;
      delegate.writeFieldId(id);
    } else {
      state = state.includeProperty(idString);
      itemFilter = state;
      if (state != null) {
        delegate.writeFieldId(id);
      }
    }
  }

  /*
    /**********************************************************
    /* Public API, write methods, text/String values
    /**********************************************************
     */

  @Override
  public void writeString(String value) throws IOException {
    if (itemFilter != null) {
      delegate.writeString(value);
    }
  }

  @Override
  public void writeString(char[] text, int offset, int len) throws IOException {
    if (itemFilter != null) {
      delegate.writeString(text, offset, len);
    }
  }

  @Override
  public void writeString(SerializableString value) throws IOException {
    if (itemFilter != null) {
      delegate.writeString(value);
    }
  }

  @Override
  public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
    if (itemFilter != null) {
      delegate.writeRawUTF8String(text, offset, length);
    }
  }

  @Override
  public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
    // not exact match, but best we can do
    if (itemFilter != null) {
      delegate.writeUTF8String(text, offset, length);
    }
  }

  /*
    /**********************************************************
    /* Public API, write methods, binary/raw content
    /**********************************************************
     */

  @Override
  public void writeRaw(String text) throws IOException {
    if (itemFilter != null) {
      delegate.writeRaw(text);
    }
  }

  @Override
  public void writeRaw(String text, int offset, int len) throws IOException {
    if (itemFilter != null) {
      delegate.writeRaw(text);
    }
  }

  @Override
  public void writeRaw(SerializableString text) throws IOException {
    if (itemFilter != null) {
      delegate.writeRaw(text);
    }
  }

  @Override
  public void writeRaw(char[] text, int offset, int len) throws IOException {
    if (itemFilter != null) {
      delegate.writeRaw(text, offset, len);
    }
  }

  @Override
  public void writeRaw(char c) throws IOException {
    if (itemFilter != null) {
      delegate.writeRaw(c);
    }
  }

  @Override
  public void writeRawValue(String text) throws IOException {
    if (itemFilter != null) {
      delegate.writeRaw(text);
    }
  }

  @Override
  public void writeRawValue(String text, int offset, int len) throws IOException {
    if (itemFilter != null) {
      delegate.writeRaw(text, offset, len);
    }
  }

  @Override
  public void writeRawValue(char[] text, int offset, int len) throws IOException {
    if (itemFilter != null) {
      delegate.writeRaw(text, offset, len);
    }
  }

  @Override
  public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len)
    throws IOException {
    if (itemFilter != null) {
      delegate.writeBinary(b64variant, data, offset, len);
    }
  }

  @Override
  public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength)
    throws IOException {
    if (itemFilter != null) {
      return delegate.writeBinary(b64variant, data, dataLength);
    } else {
      return -1;
    }
  }

  /*
    /**********************************************************
    /* Public API, write methods, other value types
    /**********************************************************
     */

  @Override
  public void writeNumber(short v) throws IOException {
    if (itemFilter != null) {
      delegate.writeNumber(v);
    }
  }

  @Override
  public void writeNumber(int v) throws IOException {
    if (itemFilter != null) {
      delegate.writeNumber(v);
    }
  }

  @Override
  public void writeNumber(long v) throws IOException {
    if (itemFilter != null) {
      delegate.writeNumber(v);
    }
  }

  @Override
  public void writeNumber(BigInteger v) throws IOException {
    if (itemFilter != null) {
      delegate.writeNumber(v);
    }
  }

  @Override
  public void writeNumber(double v) throws IOException {
    if (itemFilter != null) {
      delegate.writeNumber(v);
    }
  }

  @Override
  public void writeNumber(float v) throws IOException {
    if (itemFilter != null) {
      delegate.writeNumber(v);
    }
  }

  @Override
  public void writeNumber(BigDecimal v) throws IOException {
    if (itemFilter != null) {
      delegate.writeNumber(v);
    }
  }

  @Override
  public void writeNumber(String encodedValue)
    throws IOException, UnsupportedOperationException {
    if (itemFilter != null) {
      delegate.writeNumber(encodedValue);
    }
  }

  @Override
  public void writeBoolean(boolean v) throws IOException {
    if (itemFilter != null) {
      delegate.writeBoolean(v);
    }
  }

  @Override
  public void writeNull() throws IOException {
    if (itemFilter != null) {
      delegate.writeNull();
    }
  }

  /*
    /**********************************************************
    /* Overridden field methods
    /**********************************************************
     */

  @Override
  public void writeOmittedField(String fieldName) throws IOException {
    // Hmmh. Not sure how this would work but...
    if (itemFilter != null) {
      delegate.writeOmittedField(fieldName);
    }
  }

  /*
    /**********************************************************
    /* Public API, write methods, Native Ids
    /**********************************************************
     */

  // 25-Mar-2015, tatu: These are tricky as they sort of predate actual filtering calls.
  //   Let's try to use current state as a clue at least...

  @Override
  public void writeObjectId(Object id) throws IOException {
    if (itemFilter != null) {
      delegate.writeObjectId(id);
    }
  }

  @Override
  public void writeObjectRef(Object id) throws IOException {
    if (itemFilter != null) {
      delegate.writeObjectRef(id);
    }
  }

  @Override
  public void writeTypeId(Object id) throws IOException {
    if (itemFilter != null) {
      delegate.writeTypeId(id);
    }
  }
}
