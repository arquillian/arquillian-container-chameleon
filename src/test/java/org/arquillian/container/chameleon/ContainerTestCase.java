/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.arquillian.container.chameleon;

import java.util.HashMap;
import java.util.Map;

import org.arquillian.container.chameleon.spi.model.ContainerAdapter;
import org.arquillian.container.chameleon.spi.model.Target.Type;
import org.junit.Assert;
import org.junit.Test;

public class ContainerTestCase {

    @Test
    public void resolveJBossAS7() throws Exception {
        ContainerAdapter adapter = load("jboss as:7.1.1.Final:managed");
        Assert.assertEquals(
                "org.jboss.as:jboss-as-arquillian-container-managed:7.1.1.Final",
                adapter.dependencies()[0]);
    }

    @Test
    public void resolveJBossAS7DomainManaged() throws Exception {
        ContainerAdapter adapter = load("jboss as domain:7.1.1.Final:managed");
        Assert.assertEquals(
                "org.jboss.as:jboss-as-arquillian-container-domain-managed:7.1.1.Final",
                adapter.dependencies()[0]);
    }

    @Test
    public void resolveJBossAS7DomainRemote() throws Exception {
        ContainerAdapter adapter = load("jboss as domain:7.1.1.Final:remote");
        Assert.assertEquals(
                "org.jboss.as:jboss-as-arquillian-container-domain-remote:7.1.1.Final",
                adapter.dependencies()[0]);
    }

    @Test
    public void overrideDefaultProtocolJBossAs7() throws Exception {
        ContainerAdapter adapter = load("jboss as:7.1.1.Final:managed");
        Assert.assertTrue(
                adapter.overrideDefaultProtocol());
        Assert.assertEquals(
                "Servlet 3.0",
                adapter.getDefaultProtocol());
    }

    @Test
    public void resolveJBossAS7DefaultType() throws Exception {
        ContainerAdapter adapter = load("jboss as:7.1.1.Final");
        Assert.assertEquals(Type.Managed, adapter.type());
    }

    @Test
    public void resolveJBossEAP60() throws Exception {
        ContainerAdapter adapter = load("jboss eap:6.0.0.GA:managed");
        Assert.assertEquals(
                "org.jboss.as:jboss-as-arquillian-container-managed:7.1.2.Final",
                adapter.dependencies()[0]);
    }

    @Test
    public void overrideDefaultProtocolJBossEAP60() throws Exception {
        ContainerAdapter adapter = load("jboss eap:6.0.0.GA:managed");
        Assert.assertTrue(
                adapter.overrideDefaultProtocol());
        Assert.assertEquals(
                "Servlet 3.0",
                adapter.getDefaultProtocol());
    }

    @Test
    public void resolveJBossEAP60DefaultType() throws Exception {
        ContainerAdapter adapter = load("jboss eap:6.0.0.GA");
        Assert.assertEquals(Type.Managed, adapter.type());
    }

    @Test
    public void resolveJBossEAP61() throws Exception {
        ContainerAdapter adapter = load("jboss eap:6.1.0.GA:managed");
        Assert.assertEquals(
                "org.jboss.as:jboss-as-arquillian-container-managed:7.1.3.Final",
                adapter.dependencies()[0]);
    }

    @Test
    public void overrideDefaultProtocolJBossEAP61() throws Exception {
        ContainerAdapter adapter = load("jboss eap:6.1.0.GA:managed");
        Assert.assertTrue(
                adapter.overrideDefaultProtocol());
        Assert.assertEquals(
                "Servlet 3.0",
                adapter.getDefaultProtocol());
    }

    @Test
    public void resolveJBossEAP61DefaultType() throws Exception {
        ContainerAdapter adapter = load("jboss eap:6.1.0.GA");
        Assert.assertEquals(Type.Managed, adapter.type());
    }

    @Test
    public void resolveJBossEAP7() throws Exception {
        ContainerAdapter adapter = load("jboss eap:7.0.0.GA:managed");
        Assert.assertEquals(
                "org.wildfly.arquillian:wildfly-arquillian-container-managed:2.0.0.Final",
                adapter.dependencies()[0]);
    }

    @Test
    public void overrideDefaultProtocolJBossEAP7() throws Exception {
        ContainerAdapter adapter = load("jboss eap:7.0.0.GA:managed");
        Assert.assertFalse(
                adapter.overrideDefaultProtocol());
    }

    @Test
    public void resolveJBossEAP7DefaultType() throws Exception {
        ContainerAdapter adapter = load("jboss eap:7.0.0.GA");
        Assert.assertEquals(Type.Managed, adapter.type());
    }

    @Test
    public void resolveWildFly8() throws Exception {
        ContainerAdapter adapter = load("wildfly:8.0.0.Final:managed");
        Assert.assertEquals(
                "org.wildfly:wildfly-arquillian-container-managed:8.0.0.Final",
                adapter.dependencies()[0]);
    }

    @Test
    public void overrideDefaultProtocolWildFly8() throws Exception {
        ContainerAdapter adapter = load("wildfly:8.0.0.Final:managed");
        Assert.assertTrue(
                adapter.overrideDefaultProtocol());
        Assert.assertEquals(
                "Servlet 3.0",
                adapter.getDefaultProtocol());
    }

    @Test
    public void resolveWildFly8DefaultType() throws Exception {
        ContainerAdapter adapter = load("wildfly:8.0.0.Final");
        Assert.assertEquals(Type.Managed, adapter.type());
    }

    @Test
    public void resolveWildFly81() throws Exception {
        ContainerAdapter adapter = load("wildfly:8.1.0.Final:managed");
        Assert.assertEquals(
                "org.wildfly:wildfly-arquillian-container-managed:8.1.0.Final",
                adapter.dependencies()[0]);
    }

    @Test
    public void resolveWildFly9() throws Exception {
        ContainerAdapter adapter = load("wildfly:9.0.0.CR1:managed");
        Assert.assertEquals(
                "org.wildfly.arquillian:wildfly-arquillian-container-managed:1.0.0.Final",
                adapter.dependencies()[0]);
    }

    @Test
    public void noOverrideDefaultProtocolWildFly9() throws Exception {
        ContainerAdapter adapter = load("wildfly:9.0.0.Final:managed");
        Assert.assertFalse(
                adapter.overrideDefaultProtocol());
        Assert.assertNull(adapter.getDefaultProtocol());
    }

    @Test
    public void resolveWildFly9DefaultType() throws Exception {
        ContainerAdapter adapter = load("wildfly:9.0.0.Final");
        Assert.assertEquals(Type.Managed, adapter.type());
    }

    @Test
    public void resolveWildFly10() throws Exception {
        ContainerAdapter adapter = load("wildfly:10.0.0.Beta2:managed");
        Assert.assertEquals(
                "org.wildfly.arquillian:wildfly-arquillian-container-managed:2.0.0.Final",
                adapter.dependencies()[0]);
    }

    @Test
    public void resolveWildFly10DomainManaged() throws Exception {
        ContainerAdapter adapter = load("wildfly domain:10.0.0.Final:managed");
        Assert.assertEquals(
                "org.wildfly.arquillian:wildfly-arquillian-container-domain-managed:2.0.0.Final",
                adapter.dependencies()[0]);
    }

    @Test
    public void resolveWildFly10DomainRemote() throws Exception {
        ContainerAdapter adapter = load("wildfly domain:10.0.0.Final:remote");
        Assert.assertEquals(
                "org.wildfly.arquillian:wildfly-arquillian-container-domain-remote:2.0.0.Final",
                adapter.dependencies()[0]);
    }

    @Test
    public void noOverrideDefaultProtocolWildFly10() throws Exception {
        ContainerAdapter adapter = load("wildfly:10.0.0.Beta2:managed");
        Assert.assertFalse(
                adapter.overrideDefaultProtocol());
        Assert.assertNull(adapter.getDefaultProtocol());
    }

    @Test
    public void resolveWildFly10DefaultType() throws Exception {
        ContainerAdapter adapter = load("wildfly:10.0.0.Beta2");
        Assert.assertEquals(Type.Managed, adapter.type());
    }

    @Test
    public void resolveWindowsFilePathSlash() throws Exception {
        ContainerAdapter adapter = load("wildfly:9.0.0.Final:managed");
        Map<String, String> config = new HashMap<String, String>();
        config.put("dist", "c:\\test");
        Map<String, String> resolvedConfig = adapter.resolveConfiguration(config);

        Assert.assertEquals(
                "c:\\test",
                resolvedConfig.get("jbossHome"));
    }

    private ContainerAdapter load(String target) throws Exception {
        ChameleonConfiguration c = new ChameleonConfiguration();
        c.setChameleonTarget(target);
        return c.getConfiguredAdapter();
    }
}
