package org.arquillian.example.helloworld;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class GreetingService {

    @GET
    @Produces("text/plain")
    public String greet() {
        return "Hello World";
    }
}
