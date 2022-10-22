/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 DevCord Team and Contributor
 */

package de.chojo.gamejam.commands.register.handler;

import de.chojo.gamejam.data.JamData;
import de.chojo.gamejam.data.wrapper.jam.JamSettings;
import de.chojo.jdautil.interactions.slash.structure.handler.SlashHandler;
import de.chojo.jdautil.localization.util.Replacement;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.time.ZonedDateTime;

public class Handler implements SlashHandler {
    private final JamData jamData;

    public Handler(JamData jamData) {
        this.jamData = jamData;
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event, EventContext context) {
        var optJam = jamData.getNextOrCurrentJam(event.getGuild());
        if (optJam.isEmpty()) {
            event.reply(context.localize("error.noUpcomingJam")).setEphemeral(true).queue();
            return;
        }
        var jam = optJam.get();
        var times = jam.times();

        if (!times.registration().contains(ZonedDateTime.now())) {
            if (times.registration().start().isAfter(ZonedDateTime.now())) {
                event.reply(context.localize("command.register.notYet",
                             Replacement.create("TIMESTAM", TimeFormat.DATE_TIME_LONG.format(times.registration().start()))))
                     .setEphemeral(true)
                     .queue();
                return;
            }
            event.reply(context.localize("command.register.notAnymore")).setEphemeral(true).queue();
            return;
        }

        if (jam.registrations().contains(event.getMember().getIdLong())) {
            event.reply(context.localize("command.register.alreadyRegistered")).setEphemeral(true).queue();
            return;
        }

        jamData.register(jam, event.getMember());
        JamSettings settings = jamData.getJamSettings(event.getGuild());
        var role = event.getGuild().getRoleById(settings.jamRole());
        if (role != null) {
            event.getGuild().addRoleToMember(event.getMember(), role).queue();
        }
        event.reply(context.localize("command.register.registered",
                     Replacement.create("TIMESTAMP", TimeFormat.DATE_TIME_LONG.format(times.jam().start()))))
             .setEphemeral(true)
             .queue();
    }
}
