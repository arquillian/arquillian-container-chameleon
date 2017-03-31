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

import java.net.InetAddress;
import java.util.Map;
import org.jboss.arquillian.container.test.api.Config;
import org.junit.Test;

public class ManualUpdateContainerPropertiesTestCase extends ManualContainerControlTestTemplate {

    @Test
    public void shouldBeAbleToStartTargetContainerWithNewArguments() throws Exception {
        // given
        final InetAddress localHost = InetAddress.getLocalHost();
        final Integer newPort = 10990;
        final String jvmArguments = String.format(
            "-Djboss.socket.binding.port-offset=1000 -Djboss.bind.address=%1$s -Djboss.bind.address.management=%1$s",
            localHost.getHostAddress());
        final Map<String, String> newContainerConfiguration = new Config()
            .add("chameleonTarget", "wildfly:9.0.0.Final:managed")
            .add("javaVmArguments", jvmArguments)
            .add("managementPort", newPort.toString())
            .add("managementAddress", localHost.getHostAddress())
            .map();

        // when
        containerController.start("manual", newContainerConfiguration);

        // then
        assertConnectionPossible(localHost, newPort);
    }
}
