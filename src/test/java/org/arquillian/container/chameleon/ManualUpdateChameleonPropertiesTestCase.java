package org.arquillian.container.chameleon;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import org.jboss.arquillian.container.test.api.Config;
import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ManualUpdateChameleonPropertiesTestCase {

    @ArquillianResource
    private ContainerController cc;

    @Test
    public void shouldBeAbleToStartTargetContainerWithNewArguments() throws Exception {
        // Update configuration from arquillian.xml to a new container target
        cc.start("manual",
                new Config()
                    .add("chameleonTarget", "glassfish:4.1:managed").map());

        Socket socket = null;
        try {
            // default GlassFish Admin port is 4848
            socket = new Socket(InetAddress.getLocalHost(), 4848);
            Assert.assertTrue(socket.isConnected());
        } finally {
            if(socket != null) {
                try {
                    socket.close();
                } catch(IOException ie) {}
            }
        }
    }
}
