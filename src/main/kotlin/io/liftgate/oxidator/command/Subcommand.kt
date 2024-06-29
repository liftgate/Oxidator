package io.liftgate.oxidator.command

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent

interface Subcommand
{
    fun handle(event: GenericCommandInteractionEvent)
}