package com.gamerking195.dev.rpreview.listener;

import com.gamerking195.dev.rpreview.util.UtilPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by GamerKing195 on 7/3/17.
 * <p>
 * License is specified by the distributor which this
 * file was written for. Otherwise it can be found in the LICENSE file.
 */
public class PlayerInteractListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (UtilPlayer.getInstance().isUsing(event.getPlayer())) {
            if (event.getPlayer().getInventory().getItemInMainHand() != null && event.getPlayer().getInventory().getItemInMainHand().getType().getMaxDurability() > 0) {
                ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

                int maxDur = item.getType().getMaxDurability();
                int dur = item.getDurability();

                if (event.getAction().name().contains("RIGHT") && dur != maxDur) {

                    if (event.getPlayer().isSneaking()) {
                        boolean toggle = !item.getItemMeta().spigot().isUnbreakable();

                        ItemMeta itemMeta = item.getItemMeta();

                        itemMeta.spigot().setUnbreakable(toggle);

                        item.setItemMeta(itemMeta);

                        String color = toggle ? "&a" : "&c";

                        UtilPlayer.getInstance().sendActionBar(event.getPlayer(), "&8Unbreakable: "+color+String.valueOf(toggle).toUpperCase());

                        return;
                    }

                    item.setDurability((short) (dur + 1));

                    dur++;

                    final int currentProgress = (int) ((((double) dur / (double) maxDur)) * 20);

                    String bar = "&2::::::::::::::::::::";

                    bar = bar.substring(0, currentProgress + 2) + "&4" + bar.substring(currentProgress + 2);

                    UtilPlayer.getInstance().sendActionBar(event.getPlayer(), "&a"+item.getType().name()+" &e&l| "+bar+" &e&l| &a"+dur+" &e&l/ &c"+maxDur);
                } else if (event.getAction().name().contains("LEFT") && dur != 0) {
                    item.setDurability((short) (dur - 1));

                    dur--;

                    final int currentProgress = (int) ((((double) dur / (double) maxDur)) * 20);

                    String bar = "&2::::::::::::::::::::";

                    bar = bar.substring(0, currentProgress + 2) + "&4" + bar.substring(currentProgress + 2);

                    UtilPlayer.getInstance().sendActionBar(event.getPlayer(), "&a"+item.getType().name()+" &e&l| "+bar+" &e&l| &a"+dur+" &e&l/ &c"+maxDur);
                }
            }
        }
    }
}
