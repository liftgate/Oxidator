package io.liftgate.oxidator.content.delivery.command

import dev.minn.jda.ktx.events.listener
import dev.minn.jda.ktx.events.onCommand
import dev.minn.jda.ktx.messages.Embed
import io.liftgate.oxidator.command.invalidCommand
import io.liftgate.oxidator.content.VersionedContent
import io.liftgate.oxidator.content.VersionedContentRepository
import io.liftgate.oxidator.content.delivery.ContentCustomizer
import io.liftgate.oxidator.content.delivery.ContentScope
import io.liftgate.oxidator.content.delivery.OTCRepository
import io.liftgate.oxidator.content.delivery.job.PersonalizationJob
import io.liftgate.oxidator.content.delivery.job.PersonalizationJobService
import io.liftgate.oxidator.product.details.ProductDetailsRepository
import io.liftgate.oxidator.product.details.getProduct
import io.liftgate.oxidator.product.license.LicenseRepository
import io.liftgate.oxidator.utilities.Colors
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

/**
 * @author GrowlyX
 * @since 6/30/2024
 */
@Service
class DownloadCommand : InitializingBean
{
    @Autowired
    lateinit var jda: JDA

    @Autowired
    lateinit var licenseRepository: LicenseRepository

    @Autowired
    lateinit var productDetailsRepository: ProductDetailsRepository

    @Autowired
    lateinit var versionedContentRepository: VersionedContentRepository

    @Autowired
    lateinit var otcRepository: OTCRepository

    @Autowired
    lateinit var personalizationJobService: PersonalizationJobService

    override fun afterPropertiesSet()
    {
        jda.onCommand("download") { event ->
            val product = event.getProduct(productDetailsRepository)
                ?: return@onCommand

            val version = event.getOption("version")?.asString
                ?: return@onCommand

            event.deferReply(true).queue()

            val license = licenseRepository
                .findByDiscordUserAndAssociatedProduct(
                    event.user.idLong, product.id
                )

            if (license == null)
            {
                event.hook.sendMessageEmbeds(Embed {
                    color = Colors.Failure
                    title = "No License"
                    description = "You do not have a license for ${product.name}."
                }).queue()
                return@onCommand
            }

            val versionedContent = versionedContentRepository.findMatchingContentAvailableToLicense(
                productDetails = product,
                associatedLicense = license,
                version = version,
            )

            if (versionedContent == null)
            {
                event.hook.sendMessageEmbeds(Embed {
                    color = Colors.Failure
                    title = "Invalid Version"
                    description = "${product.name} does not have associated content with the version `$version`."
                }).queue()
                return@onCommand
            }

            val existing = otcRepository.findAllByAssociatedLicenseAndAssociatedContent(license, versionedContent)
            if (existing.isNotEmpty())
            {
                event.hook.sendMessageEmbeds(Embed {
                    color = Colors.Failure
                    title = "Already Downloaded"
                    description = "${product.name} (`$version`) has already been downloaded by you!"
                }).queue()
                return@onCommand
            }

            if (personalizationJobService.hasExisting(license, versionedContent))
            {
                event.hook.sendMessageEmbeds(Embed {
                    color = Colors.Gold
                    title = "Download In-Progress"
                    description = "Our servers are preparing your content!\n**You will get a private message from the Liftgate app when your download is ready!**"
                }).queue()
                return@onCommand
            }

            personalizationJobService.addToQueue(PersonalizationJob(
                license = license,
                content = versionedContent,
                user = event.user,
                customizer = object : ContentCustomizer
                {
                    override fun customize(job: PersonalizationJob, directory: File)
                    {

                    }
                }
            ))

            event.hook.sendMessageEmbeds(Embed {
                color = Colors.Success
                title = "Download Pending"
                description = "Our servers are preparing your content!\n**You will get a private message from the Liftgate app when your download is ready!**"
            }).queue()
        }

        jda.listener<CommandAutoCompleteInteractionEvent> {
            if (it.name != "download" || it.focusedOption.name != "version")
            {
                return@listener
            }

            val product = it.getProduct(productDetailsRepository)
                ?: return@listener

            val license = licenseRepository
                .findByDiscordUserAndAssociatedProduct(it.user.idLong, product.id)
                ?: return@listener

            val versions = versionedContentRepository
                .findAllByProductAndContentScopeEqualsOrAssociatedLicenseEquals(
                    product, ContentScope.Global, license
                )
                .map(VersionedContent::version)
                .map { version -> Command.Choice(version, version) }

            it.replyChoices(versions).queue()
        }
    }
}
