package org.arquillian.container.chameleon;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class SimpleBean {

    public String getName() {
        return "Proxy";
    }
}
