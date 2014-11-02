package org.arquillian.container.proxy.spi;

import org.jboss.arquillian.container.spi.ConfigurationException;

public class Target {

    static enum Server {
        WILDFLY,
        JBOSS_AS,
        JBOSS_EAP
    }

    static enum Type {
        Remote,
        Managed,
        Embedded
    }

    private Server server;
    private String version;
    private Type type;

    public Type getType() {
        return type;
    }

    public Server getServer() {
        return server;
    }

    public String getVersion() {
        return version;
    }

    public static Target from(String source) {
        Target target = new Target();

        String[] sections = source.split(":");
        if(sections.length != 3) {
            throw new ConfigurationException("Wrong target format [" + source + "] server:version:type");
        }
        for(Server server : Server.values()) {
            if(sections[0].toLowerCase().contains(server.name().toLowerCase().replaceAll("_", " "))) {
                target.server = server;
                break;
            }
        }
        target.version = sections[1];
        for(Type type : Type.values()) {
            if(sections[2].toLowerCase().contains(type.name().toLowerCase())) {
                target.type = type;
                break;
            }
        }
        return target;
    }

    @Override
    public String toString() {
        return server + ":" + version + ":" + type;
    }
}