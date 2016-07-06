package org.arquillian.container.chameleon.spi.model;

import org.arquillian.container.chameleon.spi.model.Target.Type;

public class Container {

    private String name;
    private String versionExpression;
    private String defaultProtocol;

    private Adapter[] adapters;
    private String defaultType;

    private Dist dist;

    private String[] exclude;

    public ContainerAdapter matches(Target target) {
        if (target.getServer().equalsIgnoreCase(name)) {
            if (target.getVersion().matches(versionExpression)) {
                Type definedType = target.getType();
                if (target.getType() == Type.Default) {
                    if (defaultType != null) {
                        definedType = Type.from(defaultType);
                    }
                }
                for (Adapter adapter : adapters) {
                    if (adapter.isType(definedType)) {
                        return new ContainerAdapter(
                                target.getVersion(),
                                definedType,
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
