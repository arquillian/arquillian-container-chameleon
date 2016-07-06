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

import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

@RunWith(Arquillian.class)
@RunAsClient
public abstract class ManualContainerControlTestTemplate {


    @ArquillianResource
    protected ContainerController containerController;

    protected void assertConnectionPossible(InetAddress localHost, Integer newPort) throws IOException {
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
