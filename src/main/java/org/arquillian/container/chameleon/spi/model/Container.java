package org.arquillian.container.chameleon.spi.model;

public class Container {

    private String name;
    private String versionExpression;
    private String defaultProtocol;

    private Adapter[] adapters;

    private Dist dist;

    private String[] exclude;

    public ContainerAdapter matches(Target target) {
        if (target.getServer().equalsIgnoreCase(name)) {
            if (target.getVersion().matches(versionExpression)) {
                for (Adapter adapter : adapters) {
                    if (adapter.isType(target.getType())) {
                        return new ContainerAdapter(
                                target.getVersion(),
                                target.getType(),
                                adapter,
                                dist,
                                exclude,
                                defaultProtocol);
                    }
                }
            }
        }
        return null;
    }

}
