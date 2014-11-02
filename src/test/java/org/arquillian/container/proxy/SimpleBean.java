package org.arquillian.container.proxy;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class SimpleBean {

    public String getName() {
        return "Proxy";
    }
}
