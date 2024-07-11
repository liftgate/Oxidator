package io.liftgate.oxidator.product.license.crypt

import io.liftgate.oxidator.utilities.logger
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Service
import java.io.File
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

@Service
class KeyGenerator : InitializingBean
{
    companion object
    {
        val PRIVATE_KEY_PATH = File("private_key.pem")
        val PUBLIC_KEY_PATH = File("public_key.pem")
    }

    fun generateKeyPair(): KeyPair
    {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(4096)
        return keyPairGenerator.generateKeyPair()
    }

    fun saveKeyToFile(key: ByteArray, filePath: File)
    {
        val encodedKey = Base64.getEncoder().encodeToString(key)
        filePath.writeText(encodedKey)
    }

    fun savePrivateKeyToFile(privateKey: PrivateKey, filePath: File)
    {
        saveKeyToFile(privateKey.encoded, filePath)
    }

    fun savePublicKeyToFile(publicKey: PublicKey, filePath: File)
    {
        saveKeyToFile(publicKey.encoded, filePath)
    }

    fun keysExist(): Boolean
    {
        return PRIVATE_KEY_PATH.exists() && PUBLIC_KEY_PATH.exists()
    }

    fun loadKeyFromFile(filePath: File): ByteArray
    {
        return Base64.getDecoder().decode(filePath.readText())
    }

    override fun afterPropertiesSet()
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
}
