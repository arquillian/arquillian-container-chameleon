package org.arquillian.container.chameleon;

import org.jboss.arquillian.container.test.api.Config;
import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;

@RunWith(Arquillian.class)
@RunAsClient
public class ManualUpdateContainerPropertiesTestCase {

    @ArquillianResource
    private ContainerController cc;

    @Test
    public void shouldBeAbleToStartTargetContainerWithNewArguments() throws Exception {
        // given
        final InetAddress localHost = InetAddress.getLocalHost();
        final Integer newPort = 10990;
        final String jvmArguments = String.format("-Djboss.socket.binding.port-offset=1000 -Djboss.bind.address=%1$s -Djboss.bind.address.management=%1$s", localHost.getHostAddress());
        final Map<String, String> newContainerConfiguration = new Config()
                .add("chameleonTarget", "wildfly:9.0.0.Final:managed")
                .add("javaVmArguments", jvmArguments)
                .add("managementPort", newPort.toString())
                .add("managementAddress", localHost.getHostAddress())
                .map();

        // when
        cc.start("manual", newContainerConfiguration);

        // then
        assertConnectionToManagementConsole(localHost, newPort);
    }

    private void assertConnectionToManagementConsole(InetAddress localHost, Integer newPort) throws IOException {
        Socket socket = null;
        try {
            socket = new Socket(localHost, newPort);
            Assert.assertTrue(socket.isConnected());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ie) {
                }
            }
        }
    }
}
