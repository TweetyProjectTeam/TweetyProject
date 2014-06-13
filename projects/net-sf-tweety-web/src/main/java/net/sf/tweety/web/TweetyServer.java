/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

