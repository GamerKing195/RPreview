package com.gamerking195.dev.rpreview.command;

import com.gamerking195.dev.rpreview.RPreview;
import com.gamerking195.dev.rpreview.util.UtilPlayer;
import com.gamerking195.dev.rpreview.util.UtilUpdater;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by GamerKing195 on 7/6/17.
 * <p>
 * License is specified by the distributor which this
 * file was written for. Otherwise it can be found in the LICENSE file.
 */
public class RPreviewCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rpreview.use")) {
                if (args.length > 0) {
                    if (args[0].equalsIgnoreCase("toggle") || args[0].equalsIgnoreCase("t")) {
                        boolean toggle = !UtilPlayer.getInstance().isUsing(player);

                        UtilPlayer.getInstance().setUsing(player, toggle);

                        String status = toggle ? "&aENABLED" : "&cDISABLED";

                        UtilPlayer.getInstance().sendActionBar(player, "&8Resource Pack Testing: "+status);
                    } else if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("e")) {
                        UtilPlayer.getInstance().setUsing(player, true);
                        UtilPlayer.getInstance().sendActionBar(player, "&8Resource Pack Testing: &aENABLED");
                    } else if (args[0].equalsIgnoreCase("disabled") || args[0].equalsIgnoreCase("d")) {
                        UtilPlayer.getInstance().setUsing(player, false);
                        UtilPlayer.getInstance().sendActionBar(player, "&8Resource Pack Testing: &cDISABLED");
                    } else if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("s")) {
                        ItemStack itemStack = player.getInventory().getItemInMainHand();
                        if (itemStack != null) {
                            if (args.length >= 2) {
                                if (NumberUtils.isNumber(args[1])) {

                                    short dur = Short.valueOf(args[1]);
                                    short maxDur = itemStack.getType().getMaxDurability();
                                    final int currentProgress = (int) ((((double) dur / (double) maxDur)) * 20);

                                    String bar = "&2::::::::::::::::::::";
                                    bar = bar.substring(0, currentProgress + 2) + "&4" + bar.substring(currentProgress + 2);

                                    UtilPlayer.getInstance().sendActionBar(player, "&a"+itemStack.getType().name()+" &e&l| "+bar+" &e&l| &a"+dur+" &e&l/ &c"+maxDur);
                                    itemStack.setDurability(Short.valueOf(args[1]));
                                } else {
                                    UtilPlayer.getInstance().sendActionBar(player, "&4Invalid argument! &8(&o"+args[1]+"&8 is not a valid number!)");
                                    sendHelp(player);
                                }
                            } else {
                                UtilPlayer.getInstance().sendActionBar(player, "&4Invalid amount of arguments!");
                                sendHelp(player);
                            }
                        }
                    } else if(args[0].equalsIgnoreCase("update") || args[0].equalsIgnoreCase("u")) {
                        if (player.hasPermission("rpreviewer.use")) {
                            UtilPlayer.getInstance().sendActionBar(player, "&2&lRPreview &aChecking for updates...");

                            UtilUpdater.getInstance().checkForUpdate();

                            if (UtilUpdater.getInstance().isUpdateAvailable())
                                UtilUpdater.getInstance().update(player);
                            else
                                UtilPlayer.getInstance().sendActionBar(player, "&c&lNO UPDATES AVAILABLE.");
                        }
                    } else
                        sendHelp(player);
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&m-----------------------"));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2&lRPreview &fV"+ RPreview.getInstance().getDescription().getVersion()+" &aby &f"+ RPreview.getInstance().getDescription().getAuthors().toString().replace("[", "").replace("]", "")));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSpigot: &fhttps://www.spigotmc.org/resources/rpreview."+RPreview.getInstance().getResourceId()+"/"));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aGitHub: &fhttps://github.com/GamerKing195/RPreview"));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aHelp: &f/rpreview help"));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&m-----------------------"));
                }
            }
        }
        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&m-----------------------"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2&lRPreview &fV"+ RPreview.getInstance().getDescription().getVersion()+" &aby &f"+ RPreview.getInstance().getDescription().getAuthors().toString().replace("[", "").replace("]", "")));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a/rpreview [toggle:enable:disable] | &fEnables or disables resource pack testing mode."));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a/rpreview set [amount] | &fSets your current items durability to that amount."));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a/rpreview update | &fUpdates the plugin to the latest version."));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a/rpr [t:e:d] | &fAlias for /rpreview."));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a/rpr s [amount] | &fAlias for /rpreview set."));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a/rpr u | &fAlias for /rpreview update."));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&m-----------------------"));
    }
}
