package org.arquillian.container.chameleon;

import java.util.HashMap;
import java.util.Map;

import org.arquillian.container.chameleon.spi.model.ContainerAdapter;
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
    public void overrideDefaultProtocolJBossAs7() throws Exception {
        ContainerAdapter adapter = load("jboss as:7.1.1.Final:managed");
        Assert.assertTrue(
                adapter.overrideDefaultProtocol());
        Assert.assertEquals(
                "Servlet 3.0",
                adapter.getDefaultProtocol());
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
    public void resolveWildFly10() throws Exception {
        ContainerAdapter adapter = load("wildfly:10.0.0.Beta2:managed");
        Assert.assertEquals(
                "org.wildfly.arquillian:wildfly-arquillian-container-managed:1.0.0.Final",
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
