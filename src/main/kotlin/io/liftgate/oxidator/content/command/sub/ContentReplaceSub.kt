package io.liftgate.oxidator.content.command.sub

import dev.minn.jda.ktx.messages.Embed
import io.liftgate.oxidator.command.Subcommand
import io.liftgate.oxidator.content.VersionedContent
import io.liftgate.oxidator.content.VersionedContentRepository
import io.liftgate.oxidator.content.delivery.ContentScope
import io.liftgate.oxidator.content.source.ContentDataSource
import io.liftgate.oxidator.product.details.ProductDetailsRepository
import io.liftgate.oxidator.product.details.getProduct
import io.liftgate.oxidator.product.license.LicenseRepository
import io.liftgate.oxidator.utilities.Colors
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import java.net.URL

/**
 * @author GrowlyX
 * @since 6/30/2024
 */
@Component
class ContentReplaceSub : Subcommand
{
    @Autowired
    lateinit var licenseRepository: LicenseRepository

    @Autowired
    lateinit var applicationContext: ApplicationContext

    @Autowired
    lateinit var productDetailsRepository: ProductDetailsRepository

    @Autowired
    lateinit var versionedContentRepository: VersionedContentRepository

    override fun handle(event: GenericCommandInteractionEvent)
    {
        val product = event.getProduct(productDetailsRepository) ?: return
        val user = event.getOption("user")?.asUser
        val version = event.getOption("version")?.asString ?: return
        val file = event.getOption("file")?.asAttachment ?: return

        event.deferReply().queue()

        val license = if (user != null)
            licenseRepository.findByDiscordUserAndAssociatedProduct(
                user.idLong, product.id
            )
        else null

        val versionedContent = versionedContentRepository.findMatchingContentAvailableToLicense(
            productDetails = product,
            associatedLicense = license,
            version = version
        )

        if (versionedContent == null)
        {
            event.hook.sendMessageEmbeds(Embed {
                color = Colors.Failure
                title = "No Content"
                description = "No content exists with that version and product (and/or license)."
            }).queue()
            return
        }

        val cds = applicationContext.getBean<ContentDataSource>(versionedContent.contentDataSourceID)
        if (file.fileExtension !in listOf("zip", "jar"))
        {
            event.hook.sendMessageEmbeds(Embed {
                color = Colors.Failure
                title = "Invalid Content Type"
                description = "CDS only supports `.zip` and `.jar` files as content upload inputs."
            }).queue()
            return
        }

        val stream = URL(file.url).openStream()
        cds.delete(versionedContent.id, "content")
        cds.store(
            versionedContent.id,
            "content",
            file.contentType ?: "application/java-archive",
            stream
        )

        event.hook.sendMessageEmbeds(Embed {
            color = Colors.Success
            title = "Replaced Content"
            description = "Saved new content with ID `${versionedContent.id}` to DataSource `${versionedContent.contentDataSourceID}`."
        }).queue()
    }
}
