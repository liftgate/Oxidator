package io.liftgate.oxidator.content.delivery;


import com.google.common.io.Files;
import io.liftgate.oxidator.content.delivery.job.PersonalizationJob;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
public enum ContentCustomizerUtilities {
    INSTANCE;

    public static void customizeJar(InputStream originalJar, OutputStream newSource,
                                        PersonalizationJob job, ContentCustomizer contentCustomizer) throws IOException {
        // Create a temporary directory to extract the original jar
        File tempDir = Files.createTempDir();
        tempDir.deleteOnExit();

        File tempFile = new File(tempDir, "tmpjar");
        tempFile.createNewFile();

        FileUtils.copyToFile(originalJar, tempFile);

        // Extract the original jar contents
        try (ZipFile zipFile = new ZipFile(tempFile)) {
            zipFile.stream().forEach(entry -> {
                try (InputStream is = zipFile.getInputStream(entry)) {
                    File outFile = new File(tempDir, entry.getName());
                    if (entry.isDirectory()) {
                        outFile.mkdirs();
                    } else {
                        FileUtils.copyInputStreamToFile(is, outFile);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        // Add the new metadata file to the temporary directory
        contentCustomizer.customize(job, tempDir);

        // Create a new jar file with the original contents plus the metadata file
        try (JarOutputStream jos = new JarOutputStream(newSource)) {
            addDirectoryToJar(jos, tempDir, tempDir.getAbsolutePath().length() + 1);
        }

        // Clean up the temporary directory
        FileUtils.deleteDirectory(tempDir);
    }

    private static void addDirectoryToJar(JarOutputStream jos, File dir, int prefixLength) throws IOException {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                addDirectoryToJar(jos, file, prefixLength);
            } else {
                String entryName = file.getAbsolutePath().substring(prefixLength).replace('\\', '/');
                jos.putNextEntry(new JarEntry(entryName));
                try (FileInputStream fis = new FileInputStream(file)) {
                    IOUtils.copy(fis, jos);
                }
                jos.closeEntry();
            }
        }
    }
}
