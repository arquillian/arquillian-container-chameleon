package org.arquillian.container.chameleon;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class GreetingService {
    public String greet(String who) {
        return "Hello, " + who + "!";
    }
}
