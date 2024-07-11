package io.liftgate.oxidator.command

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent

/**
 * @author GrowlyX
 * @since 6/29/2024
 */
val invalidCommand = object : Subcommand
{
    override fun handle(event: GenericCommandInteractionEvent)
    {
        event.reply("Invalid command!").setEphemeral(true).queue()
    }
}
