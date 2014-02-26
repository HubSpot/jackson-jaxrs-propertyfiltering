package com.hubspot.jackson.jaxrs;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class PropertyFilteringMessageBodyWriter implements MessageBodyWriter<Object> {
  private static final Logger logger = Logger.getLogger(PropertyFilteringMessageBodyWriter.class.getName());
  private static final JacksonJsonProvider defaultProvider = new JacksonJsonProvider();

  private final ExecutorService writeExecutor = Executors.newCachedThreadPool();

  @Context
  Application application;

  @Context
  UriInfo uriInfo;

  @Override
  public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return isJsonType(mediaType) &&
           filteringEnabled(annotations) &&
           findJsonProvider().isWriteable(type, genericType, annotations, mediaType);
  }

  @Override
  public long getSize(Object object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return -1;
  }

  @Override
  public void writeTo(Object o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                      MultivaluedMap<String, Object> httpHeaders, OutputStream os) throws IOException {
    PropertyFiltering annotation = findPropertyFiltering(annotations);

    PropertyFilter propertyFilter = new PropertyFilter(uriInfo.getQueryParameters().get(annotation.using()));
    if (!propertyFilter.hasFilters()) {
      write(o, type, genericType, annotations, mediaType, httpHeaders, os);
      return;
    }

    JsonNode tree = toTree(o, type, genericType, annotations, mediaType, httpHeaders);

    JsonNode toFilter = "".equals(annotation.on()) ? tree : tree.get(annotation.on());

    if (toFilter != null && toFilter.isArray()) {
      propertyFilter.filter((ArrayNode) toFilter);
    }

    write(tree, tree.getClass(), tree.getClass(), annotations, mediaType, httpHeaders, os);
  }

  private JsonNode toTree(final Object o, final Class<?> type, final Type genericType,
                          final Annotation[] annotations, final MediaType mediaType,
                          final MultivaluedMap<String, Object> httpHeaders) throws IOException {
    PipedInputStream in = new PipedInputStream();
    final PipedOutputStream out = new PipedOutputStream(in);
    Future<IOException> writeFuture = writeExecutor.submit(new Callable<IOException>() {

      @Override
      public IOException call() throws Exception {
        try {
          write(o, type, genericType, annotations, mediaType, httpHeaders, out);
          return null;
        } catch (IOException e) {
          return e;
        } finally {
          closeQuietly(out);
        }
      }
    });

    try {
      return findJsonProvider().locateMapper(type, mediaType).readTree(in);
    } catch (IOException e) {
      throwWriteExceptionIfPresent(writeFuture);
      throw e;
    } finally {
      closeQuietly(in);
    }
  }

  private void write(Object o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                     MultivaluedMap<String, Object> httpHeaders, OutputStream os) throws IOException {
    findJsonProvider().writeTo(o, type, genericType, annotations, mediaType, httpHeaders, os);
  }

  private JacksonJsonProvider findJsonProvider() {
    for (Object o : application.getSingletons()) {
      if (o instanceof JacksonJsonProvider) {
        return (JacksonJsonProvider) o;
      }
    }

    return defaultProvider;
  }

  private void closeQuietly(Closeable closeable) {
    try {
      closeable.close();
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error closing " + closeable, e);
    }
  }

  private static void throwWriteExceptionIfPresent(Future<IOException> future) throws IOException {
    try {
      IOException exception = future.get();
      if (exception != null) {
        throw exception;
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      if (cause instanceof RuntimeException) {
        throw (RuntimeException) cause;
      } else {
        throw new RuntimeException(cause);
      }
    }
  }

  private static boolean isJsonType(MediaType mediaType) {
    return MediaType.APPLICATION_JSON_TYPE.getType().equals(mediaType.getType()) &&
            MediaType.APPLICATION_JSON_TYPE.getSubtype().equals(mediaType.getSubtype());
  }

  private static boolean filteringEnabled(Annotation... annotations) {
    return findPropertyFiltering(annotations) != null;
  }

  private static PropertyFiltering findPropertyFiltering(Annotation... annotations) {
    for (Annotation annotation : annotations) {
      if (annotation.annotationType() == PropertyFiltering.class) {
        return (PropertyFiltering) annotation;
      }
    }

    return null;
  }
}
