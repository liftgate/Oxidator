package io.liftgate.oxidator.product.license.crypt

import io.liftgate.oxidator.utilities.logger
import java.io.File
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
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

    fun keyGenerateIfNeeded()
    {
        if (keysExist())
        {
            return
        }

        val keyPair = generateKeyPair()
        val privateKey = keyPair.private
        val publicKey = keyPair.public

        savePrivateKeyToFile(privateKey, PRIVATE_KEY_PATH)
        savePublicKeyToFile(publicKey, PUBLIC_KEY_PATH)

        logger.info {
            "Keys generated and saved to files."
        }
    }
}
