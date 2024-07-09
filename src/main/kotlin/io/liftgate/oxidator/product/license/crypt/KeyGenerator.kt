package io.liftgate.oxidator.product.license.crypt

import io.liftgate.oxidator.utilities.logger
import java.io.File
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

object KeyGenerator
{
    private const val PRIVATE_KEY_PATH = "private_key.pem"
    private const val PUBLIC_KEY_PATH = "public_key.pem"

    fun generateKeyPair(): KeyPair
    {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(4096)
        return keyPairGenerator.generateKeyPair()
    }

    fun saveKeyToFile(key: ByteArray, filePath: String)
    {
        val encodedKey = Base64.getEncoder().encodeToString(key)
        File(filePath).writeText(encodedKey)
    }

    fun savePrivateKeyToFile(privateKey: PrivateKey, filePath: String)
    {
        saveKeyToFile(privateKey.encoded, filePath)
    }

    fun savePublicKeyToFile(publicKey: PublicKey, filePath: String)
    {
        saveKeyToFile(publicKey.encoded, filePath)
    }

    fun keysExist(): Boolean
    {
        val privateKeyFile = File(PRIVATE_KEY_PATH)
        val publicKeyFile = File(PUBLIC_KEY_PATH)
        return privateKeyFile.exists() && publicKeyFile.exists()
    }

    fun loadKeyFromFile(filePath: String): ByteArray {
        return Base64.getDecoder().decode(File(filePath).readText())
    }

    val privateKey by lazy {
        val keyBytes = loadKeyFromFile(PRIVATE_KEY_PATH)
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        keyFactory.generatePrivate(keySpec)
    }

    val publicKey by lazy {
        val keyBytes = loadKeyFromFile(PUBLIC_KEY_PATH)
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        keyFactory.generatePublic(keySpec)
    }

    fun keyGenerateIfNeeded()
    {
        if (keysExist())
        {
            privateKey
            publicKey
            return
        }

        val keyPair = generateKeyPair()
        val privateKey = keyPair.private
        val publicKey = keyPair.public

        savePrivateKeyToFile(privateKey, PRIVATE_KEY_PATH)
        savePublicKeyToFile(publicKey, PUBLIC_KEY_PATH)

        this.privateKey
        this.publicKey

        logger.info {
            "Keys generated and saved to files."
        }
    }
}
