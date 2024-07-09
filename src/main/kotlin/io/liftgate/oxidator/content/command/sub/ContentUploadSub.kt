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
class ContentUploadSub : Subcommand
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
        val product = event.getProduct(productDetailsRepository)
            ?: return

        val user = event.getOption("user")?.asUser
        val multiDownload = event.getOption("multi-download")?.asBoolean ?: false
        val version = event.getOption("version")?.asString
            ?: return
        val dataSource = event.getOption("datasource")?.asString
            ?: "gridfs"

        val cds = applicationContext.getBean<ContentDataSource>(dataSource)

        val file = event.getOption("file")?.asAttachment
            ?: return

        event.deferReply().queue()

        val license = if (user != null)
            licenseRepository.findByDiscordUserAndAssociatedProduct(
                user.idLong, product.id
            )
        else null

        if (user != null && license == null)
        {
            event.hook.sendMessageEmbeds(Embed {
                color = Colors.Failure
                title = "No License"
                description = "The user you tried to attach this content to does not have a license for ${product.name}."
            }).queue()
            return
        }

        if (file.fileExtension != "jar" || file.fileExtension != "zip")
        {
            event.hook.sendMessageEmbeds(Embed {
                color = Colors.Failure
                title = "Invalid Content Type"
                description = "CDS only supports `.zip` and `.jar` files as content upload inputs."
            }).queue()
            return
        }

        val stream = URL(file.url).openStream()
        val newContent = VersionedContent(
            name = file.fileName,
            product = product,
            version = version,
            contentDataSourceID = dataSource,
            contentScope = if (user != null) ContentScope.LicenseSpecific else ContentScope.Global,
            associatedLicense = license,
            multiDownloadAllowed = multiDownload
        )

        versionedContentRepository.save(newContent)
        cds.store(
            newContent.id,
            "content",
            file.contentType ?: "application/java-archive",
            stream
        )

        event.hook.sendMessageEmbeds(Embed {
            color = Colors.Success
            title = "Saved Content"
            description = "Saved new content with ID `${newContent.id}` to DataSource `$dataSource`."
        }).queue()
    }
}
