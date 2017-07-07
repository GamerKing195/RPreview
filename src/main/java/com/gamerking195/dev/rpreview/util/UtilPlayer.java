package com.gamerking195.dev.rpreview.util;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by GamerKing195 on 7/6/17.
 * <p>
 * License is specified by the distributor which this
 * file was written for. Otherwise it can be found in the LICENSE file.
 */
public class UtilPlayer {

    private UtilPlayer() {}
    @Getter
    private static UtilPlayer instance = new UtilPlayer();

    private ArrayList<String> currentUsers = new ArrayList<>();

    public void sendActionBar(Player player, String actionBar) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', actionBar)));
    }

    public void setUsing(Player player, boolean using) {
        if (using) {
            if (!currentUsers.contains(player.getUniqueId().toString()))
                currentUsers.add(player.getUniqueId().toString());
        } else {
            if (currentUsers.contains(player.getUniqueId().toString()))
                currentUsers.remove(player.getUniqueId().toString());
        }
    }

    public boolean isUsing(Player player) {
        return currentUsers.contains(player.getUniqueId().toString());
    }
}
