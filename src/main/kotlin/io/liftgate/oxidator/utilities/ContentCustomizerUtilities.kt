package io.liftgate.oxidator.utilities

import io.liftgate.oxidator.content.delivery.ContentCustomizer
import io.liftgate.oxidator.content.delivery.job.PersonalizationJob
import java.io.*
import java.nio.file.Files
import java.util.jar.JarEntry
import java.util.jar.JarInputStream
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
fun processJar(inputStream: InputStream, personalizationJob: PersonalizationJob, contentCustomizer: ContentCustomizer): InputStream {
    // Create a temporary directory to extract the jar contents
    val tempDir = Files.createTempDirectory("jar_extraction").toFile()
    val outputJarFile = Files.createTempFile("customized_jar", ".jar").toFile()

    try {
        // Extract the contents of the jar file to the temp directory
        extractJar(inputStream, tempDir)

        // Apply the customizer to the extracted contents
        contentCustomizer.customize(personalizationJob, tempDir)

        // Repackage the contents of the temp directory into a new jar file
        createJar(tempDir, outputJarFile)

        // Return an InputStream of the new jar file
        return outputJarFile.inputStream()
    } finally {
        // Cleanup temporary files
        tempDir.deleteRecursively()
        outputJarFile.deleteOnExit()
    }
}

private fun extractJar(inputStream: InputStream, destDir: File) {
    JarInputStream(inputStream).use { jarInputStream ->
        var entry: JarEntry?
        while (jarInputStream.nextJarEntry.also { entry = it } != null) {
            val entryFile = File(destDir, entry!!.name)
            if (entry!!.isDirectory) {
                entryFile.mkdirs()
            } else {
                entryFile.parentFile.mkdirs()
                entryFile.outputStream().use { output ->
                    jarInputStream.copyTo(output)
                }
            }
        }
    }
}

private fun createJar(sourceDir: File, jarFile: File) {
    JarOutputStream(BufferedOutputStream(FileOutputStream(jarFile))).use { jarOutputStream ->
        Files.walk(sourceDir.toPath()).filter { path -> !Files.isDirectory(path) }.forEach { path ->
            val entryName = sourceDir.toPath().relativize(path).toString().replace(File.separatorChar, '/')
            val entry = ZipEntry(entryName)
            jarOutputStream.putNextEntry(entry)
            Files.newInputStream(path).use { input ->
                input.copyTo(jarOutputStream)
            }
            logger.info { "Packing $entryName into jar right now" }
            jarOutputStream.closeEntry()
        }
    }


    logger.info { "Final length: ${jarFile.length()}" }
}
