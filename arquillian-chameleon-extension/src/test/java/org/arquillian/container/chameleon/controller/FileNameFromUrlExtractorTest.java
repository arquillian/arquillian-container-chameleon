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

package org.arquillian.container.chameleon.controller;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FileNameFromUrlExtractorTest {

    @Test
    public void should_extract_file_name_from_url() throws Exception {
        // given
        final String tomcatUrl = "http://archive.apache.org/dist/tomcat/tomcat-6/v6.0.48/bin/apache-tomcat-6.0.48.zip";
        final String expectedFileName = "apache-tomcat-6.0.48";

        // when
        final String fileName = new FileNameFromUrlExtractor(tomcatUrl).extract();

        // then
        assertEquals(expectedFileName, fileName);
    }
}