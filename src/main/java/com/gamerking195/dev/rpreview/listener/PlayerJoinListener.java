package com.gamerking195.dev.rpreview.listener;

import com.gamerking195.dev.rpreview.RPreview;
import com.gamerking195.dev.rpreview.util.UtilUpdater;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("rpreview.use") || player.isOp()) {
            if (UtilUpdater.getInstance().isUpdateAvailable()) {
                    String currentVersion = RPreview.getInstance().getDescription().getVersion();
                    String mcVersion = Bukkit.getServer().getClass().getPackage().getName();
                    mcVersion = mcVersion.substring(mcVersion.lastIndexOf(".") + 1);
                    mcVersion = mcVersion.substring(1, mcVersion.length()-3).replace("_", ".");

                    String latestVersion = UtilUpdater.getInstance().getLatestVersion();
                    String updateInfo = UtilUpdater.getInstance().getUpdateInfo();
                    List<String> testedVersions = UtilUpdater.getInstance().getTestedVersions();

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&m------------------------------"));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2&lRPReview &aV" + currentVersion + " &eby &a" + RPreview.getInstance().getDescription().getAuthors().toString().replace("[", "").replace("]", "")));
                    player.sendMessage("");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eThere is a RPreview update available!"));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eVersion: &a" + latestVersion));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eUpdates: \n" + updateInfo));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSupported MC Versions: &a" + StringUtils.join(testedVersions, ", ")));
                    if (!testedVersions.contains(mcVersion))
                        player.sendMessage(ChatColor.DARK_RED+"Warning your current version, "+mcVersion+", is not supported by this update, there may be unexpected bugs!");
                    player.sendMessage("");

                    TextComponent accept = new TextComponent("[CLICK TO UPDATE]");
                    accept.setColor(ChatColor.DARK_GREEN);
                    accept.setBold(true);
                    accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rpr update"));
                    accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&2&lRPREVIEW &aV" + currentVersion + " &e&l» &aV" + latestVersion+"\n&b\n&a    CLICK TO UPDATE")).create()));

                    player.spigot().sendMessage(accept);

                    player.sendMessage("");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&m------------------------------"));
                }
        }
    }
}
