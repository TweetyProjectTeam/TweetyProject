package net.sf.tweety.web;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * Uses the Grizzly HTTP server to instantiate the Tweety server
 * that provides API access to Tweety services.
 */
public class TweetyServer {
	
    // Base URI of this server
    public static final String BASE_URI = "http://localhost:8080/tweety/";

    /**
     * Main server method.
     * @param args additional arguments (none expected)
     * @throws IOException
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws IOException, InterruptedException {
    	// gather Tweety services (exposed as JAX-RS resources)
        final ResourceConfig resourceConfig = new ResourceConfig().packages("net.sf.tweety.web.services");
        // start server
        GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), resourceConfig);
        while(true)
        	Thread.sleep(1000);        
    }
}

