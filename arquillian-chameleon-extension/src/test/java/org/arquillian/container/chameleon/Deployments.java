/*
 * JBoss, Home of Professional Open Source
 * Copyright 2017 Red Hat Inc. and/or its affiliates and other contributors
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

import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.io.File;

public class Deployments {

    private static final File[] WELD_SERVLET =
        Maven.resolver().resolve("org.jboss.weld.servlet:weld-servlet:1.1.33.Final").withTransitivity().asFile();

    static void enrichTomcatWithCdi(WebArchive archive) {
        String contextXml = "<Context>\n" +
            "   <Resource name=\"BeanManager\" \n" +
            "      auth=\"Container\"\n" +
            "      type=\"javax.enterprise.inject.spi.BeanManager\"\n" +
            "      factory=\"org.jboss.weld.resources.ManagerObjectFactory\"/>\n" +
            "</Context>\n";

        String webXml = "<web-app version=\"3.0\">\n" +
            "<listener>\n" +
            "      <listener-class>org.jboss.weld.environment.servlet.Listener</listener-class>\n" +
            "   </listener>" +
            "  <resource-env-ref>\n" +
            "    <resource-env-ref-name>BeanManager</resource-env-ref-name>\n" +
            "    <resource-env-ref-type>\n" +
            "            javax.enterprise.inject.spi.BeanManager\n" +
            "    </resource-env-ref-type>\n" +
            "  </resource-env-ref>\n" +
            "</web-app>";

        archive.addAsLibraries(WELD_SERVLET);
        archive.addAsManifestResource(new StringAsset(contextXml), "context.xml");
        archive.setWebXML(new StringAsset(webXml));
    }
}
