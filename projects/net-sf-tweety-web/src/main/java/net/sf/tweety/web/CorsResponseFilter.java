package net.sf.tweety.web;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;

/**
 * For enabling cross-origin resource sharing
 * @author Matthias Thimm
 *
 */
public class CorsResponseFilter implements ContainerResponseFilter {     
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {     
        MultivaluedMap<String, Object> headers = responseContext.getHeaders();     
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, POST");            
        headers.add("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, X-Codingpedia");
    }     
}
