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

package org.arquillian.container.chameleon.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;

public final class Resolver {

    public static File[] resolve(File cacheFolder, MavenDependency[] dependencies, String settingsXml) {
        String hash = hash(dependencies);
        File[] files = null;

        File cacheFile = getCacheFile(cacheFolder, hash);
        if (cacheFile.exists()) {
            files = readCache(cacheFile);
        } else {
            ConfigurableMavenResolverSystem resolver = Maven.configureResolver();
            if (settingsXml != null) {
                resolver.fromFile(settingsXml);
            }

            files = resolver.addDependencies(dependencies)
                    .resolve()
                    .withTransitivity()
                    .asFile();

            writeCache(getCacheFile(cacheFolder, hash), files);
        }
        return files;
    }

    private static void writeCache(File cacheFile, File[] files) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(cacheFile));
            for (File file : files) {
                bw.write(file.getAbsolutePath());
                bw.newLine();
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not write cache file " + cacheFile, e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (Exception e) {
                    throw new RuntimeException("Could not close written cache file " + cacheFile, e);
                }
            }
        }
    }

    private static File[] readCache(File cacheFile) {
        List<File> files = new ArrayList<File>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(cacheFile));
            String line;
            while ((line = br.readLine()) != null) {
                files.add(new File(line));
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not read cache file " + cacheFile + ". Please remove the file and rerun", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    throw new RuntimeException("Could not close read cache file " + cacheFile, e);
                }
            }
        }
        return files.toArray(new File[]{});
    }

    private static File getCacheFile(File cacheFolder, String hash) {
        return new File(cacheFolder, hash + ".cache");
    }

    private static String hash(MavenDependency[] dependencies) {
        try {
            StringBuilder sb = new StringBuilder();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            for (MavenDependency dependency : dependencies) {
                sb.append(dependency.toString());
            }

            byte[] hash = digest.digest(sb.toString().getBytes());
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
