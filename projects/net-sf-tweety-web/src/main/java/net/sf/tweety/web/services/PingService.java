/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.web.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.sf.tweety.web.TweetyServer;

import org.codehaus.jettison.json.JSONException;

/**
 * Provides a simple ping service that always returns the request
 * sent to it.
 */
@Path("ping")
public class PingService {
	
	/** The identifier of this service. */
	public static final String ID = "ping";

	/**
     * Handles all requests for theping service.
     * @return String Some request
     * @throws JSONException thrown if something is completely going wrong.
     */
	@POST    
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String handleRequest(final String query) throws JSONException {
		TweetyServer.log(PingService.ID, "Received and returned request " + query);
		return query;
	}
}
