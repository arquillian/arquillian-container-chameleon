package org.arquillian.container.chameleon.controller;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public final class Resolver {

    public static File[] resolve(File cacheFolder, MavenDependency[] dependencies) {
        String hash = hash(dependencies);
        File[] files = null;

        File cacheFile = getCacheFile(cacheFolder, hash);
        if (cacheFile.exists()) {
            files = readCache(cacheFile);
        } else {
            files = Maven.configureResolver()
                    .addDependencies(dependencies)
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
