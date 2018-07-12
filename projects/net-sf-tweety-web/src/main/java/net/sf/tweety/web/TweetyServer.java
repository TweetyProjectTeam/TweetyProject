/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.web;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;

/**
 * Uses the Grizzly HTTP server to instantiate the Tweety server
 * that provides API access to Tweety services.
 */
public class TweetyServer {
	
    // Base URI of this server
    public static final String BASE_URI = "http://192.168.0.2:8080/tweety/";//"http://192.168.0.2:8080/tweety/"; 
    // Log file of this server
    private static final String LOG = "tweetyserver.log";
     
    /**
     * Main server method.
     * @param args additional arguments (none expected)
     * @throws IOException
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws IOException, InterruptedException {
    	// gather Tweety services (exposed as JAX-RS resources)
    	TweetyServer.log("server", "Initiliazing TweetyServer...");
    	final ResourceConfig resourceConfig = new ResourceConfig().packages("net.sf.tweety.web.services");
    	resourceConfig.register(CorsResponseFilter.class);
    	TweetyServer.log("server", "Starting TweetyServer...");
        // start server    	
        GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), resourceConfig);        
        while(true)
        	Thread.sleep(1000);         
    }       
    
    /**
     * Writes the given message to the log of this server.
     * @param source the source of the message (should be some identifier of the service)
     * @param message some message 
     */
    public static void log(String source, String message){
    	try {
			Writer output = new BufferedWriter(new FileWriter(TweetyServer.LOG, true));
			output.append(new java.util.Date() + "\t" + "[" + source + "]" + "\t" + message + "\n");
			output.close();
		} catch (IOException e) {
			System.err.println("Log file '" + TweetyServer.LOG + "' cannot be written.");
		}
    }
}

