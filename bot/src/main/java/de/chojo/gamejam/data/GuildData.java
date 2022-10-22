/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 DevCord Team and Contributor
 */

package de.chojo.gamejam.data;

import de.chojo.gamejam.data.wrapper.guild.Settings;
import de.chojo.sadu.base.QueryFactory;
import de.chojo.sadu.wrapper.QueryBuilderConfig;
import net.dv8tion.jda.api.entities.Guild;

import javax.sql.DataSource;

public class GuildData extends QueryFactory {

    public GuildData(DataSource dataSource, QueryBuilderConfig config) {
        super(dataSource, config);
    }

    public Settings getSettings(Guild guild) {
        return builder(Settings.class)
                .query("SELECT manager_role, locale FROM settings WHERE guild_id  = ?")
                .parameter(p -> p.setLong(guild.getIdLong()))
                .readRow(r -> new Settings(guild.getIdLong(), r.getString("locale"), r.getLong("manager_role")))
                .firstSync()
                .orElseGet(() -> new Settings(guild.getIdLong()));
    }

    public boolean updateSettings(Settings settings) {
        return builder()
                .query("""
                       INSERT INTO settings(guild_id, manager_role, locale) VALUES(?,?,?)
                       ON CONFLICT(guild_id)
                           DO UPDATE
                               SET manager_role = excluded.manager_role, locale = excluded.locale
                       """)
                .parameter(p -> p.setLong(settings.guildId()).setLong(settings.orgaRole())
                                 .setString(settings.locale()))
                .update()
                .sendSync()
                .changed();
    }
}
