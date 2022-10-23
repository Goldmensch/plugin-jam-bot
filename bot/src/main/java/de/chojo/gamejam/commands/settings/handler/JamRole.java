/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 DevCord Team and Contributor
 */

package de.chojo.gamejam.commands.settings.handler;

import de.chojo.gamejam.data.JamData;
import de.chojo.jdautil.interactions.slash.structure.handler.SlashHandler;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public final class JamRole implements SlashHandler {
    private final JamData jamData;

    public JamRole(JamData jamData) {
        this.jamData = jamData;
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event, EventContext context) {
        var settings = jamData.getJamSettings(event.getGuild());
        settings.jamRole(event.getOption("role").getAsRole().getIdLong());
        jamData.updateJamSettings(event.getGuild(), settings);
        event.reply(context.localize("command.settings.jamrole.message.updated")).setEphemeral(true).queue();
    }
}
