package org.arquillian.container.chameleon.spi.model;

public class Dist {

    private String gav;

    public String gav() {
        return gav;
    }
    
    public Dist ofGav(String gav) {
         this.gav = gav;
         return this;
    }
}
