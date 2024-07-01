package io.liftgate.oxidator.utilities

import io.liftgate.oxidator.content.delivery.ContentCustomizer
import io.liftgate.oxidator.content.delivery.job.PersonalizationJob
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.CompressionLevel
import net.lingala.zip4j.model.enums.CompressionMethod
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Files

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
fun processJar(
    inputStream: InputStream,
    personalizationJob: PersonalizationJob,
    contentCustomizer: ContentCustomizer
): File
{
    // Create a temporary directory to extract the jar contents
    val tempDir = Files.createTempDirectory("jar_extraction").toFile()
    val outputJarFile = Files.createTempFile("customized_jar", ".jar").toFile()

    try
    {
        // Save input stream to a temporary JAR file
        val tempJarFile = Files.createTempFile("temp_jar", ".jar").toFile()
        FileOutputStream(tempJarFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }

        // Extract the contents of the jar file to the temp directory
        extractJar(tempJarFile, tempDir)

        // Apply the customizer to the extracted contents
        contentCustomizer.customize(personalizationJob, tempDir)

        // Repackage the contents of the temp directory into a new jar file
        createJar(tempDir, outputJarFile)
        logger.info { "New file size: ${outputJarFile.length()}" }

        // Return an InputStream of the new jar file
        return outputJarFile.apply {
            outputJarFile.deleteOnExit() // Ensure the file is deleted when the JVM exits
        }
    } finally
    {
        // Cleanup temporary directory
        tempDir.deleteRecursively()
    }
}

private fun extractJar(jarFile: File, destDir: File)
{
    ZipFile(jarFile).extractAll(destDir.absolutePath)
}

private fun createJar(sourceDir: File, jarFile: File)
{
    val zipFile = ZipFile(jarFile)

    val parameters = ZipParameters()
    parameters.compressionMethod = CompressionMethod.DEFLATE
    parameters.compressionLevel = CompressionLevel.FAST

    sourceDir.walkTopDown()
        .filter { it.isFile }
        .forEach { file ->
            val relativePath = sourceDir.toPath()
                .relativize(file.toPath())
                .toString()
                .replace(File.separatorChar, '/')

            parameters.fileNameInZip = relativePath
            zipFile.addFile(file, parameters)
        }
}
