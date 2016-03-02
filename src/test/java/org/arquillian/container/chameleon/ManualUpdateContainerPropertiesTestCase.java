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

//@RunWith(Arquillian.class)
public class ManualUpdateContainerPropertiesTestCase {

    @ArquillianResource
    private ContainerController cc;

    //@Test
    public void shouldBeAbleToStartTargetContainerWithNewArguments() throws Exception {
        // Update configuration from arquillian.xml to bind to a new port
        cc.start("manual",
                new Config()
                    .add("javaVmArguments", "-Djboss.socket.binding.port-offset=1000")
                    .add("managementPort", "10990").map());

        Socket socket = null;
        try {
            socket = new Socket(InetAddress.getLocalHost(), 10990);
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
