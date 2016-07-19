package com.hubspot.jackson.jaxrs.util;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.hubspot.jackson.jaxrs.PropertyFilteringMessageBodyWriter;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * Adapted from Jackson source, credit to Tatu Saloranta
 * (unfortunately they don't ship a test JAR so I couldn't reuse their code)
 */
public enum Helper {
  INSTANCE;

  public Server startServer() throws Exception {
    Server server = new Server(0);
    ContextHandlerCollection contexts = new ContextHandlerCollection();
    server.setHandler(contexts);
    ServletHolder jaxrs = new ServletHolder(ServletContainer.class);
    jaxrs.setInitParameter("javax.ws.rs.Application", TestApplication.class.getName());
    ServletContextHandler mainHandler = new ServletContextHandler(contexts, "/", true, false);
    mainHandler.addServlet(jaxrs, "/*");

    server.setHandler(mainHandler);
    server.start();

    return server;
  }

  public int getPort(Server server) {
    return ((NetworkConnector) server.getConnectors()[0]).getLocalPort();
  }

  public static class TestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
      Set<Class<?>> classes = new HashSet<Class<?>>();
      classes.add(PropertyFilteringMessageBodyWriter.class);
      return classes;
    }

    @Override
    public Set<Object> getSingletons() {
      Set<Object> singletons = new HashSet<Object>();
      singletons.add(new TestResource());
      return singletons;
    }
  }
}
