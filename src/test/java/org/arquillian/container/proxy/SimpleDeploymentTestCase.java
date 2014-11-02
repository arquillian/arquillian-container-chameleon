package org.arquillian.container.proxy;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class SimpleDeploymentTestCase {

    @Deployment
    public static WebArchive deploy() {
        return ShrinkWrap.create(WebArchive.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addClass(SimpleBean.class);
    }

    @Inject
    private SimpleBean bean;

    @Test
    public void shouldNotBeNull() {
        Assert.assertNotNull(bean);
    }

    @Test
    public void shouldReturnName() {
        Assert.assertEquals("Proxy", bean.getName());
    }
}
