package com.gamerking195.dev.rpreview.util;
import com.gamerking195.dev.autoupdaterapi.UpdateLocale;
import com.gamerking195.dev.autoupdaterapi.Updater;
import com.gamerking195.dev.rpreview.RPreview;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilUpdater {

    private UtilUpdater() {}
    private static UtilUpdater instance = new UtilUpdater();
    public static UtilUpdater getInstance() {
        return instance;
    }

    @Getter
    private String latestVersion;
    @Getter
    private String updateInfo;
    @Getter
    private List<String> testedVersions;

    @Getter
    private boolean updateAvailable;
    private boolean updating;

    private JavaPlugin plugin = RPreview.getInstance();

    private Gson gson = new Gson();

    /*
     * UTILITIES
     */

    public void init() {
        new BukkitRunnable() {
            @Override
            public void run() {
                checkForUpdate();

                if (updateAvailable) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.isOp() || player.hasPermission("rpreview.use")) {
                            String currentVersion = RPreview.getInstance().getDescription().getVersion();
                            String mcVersion = Bukkit.getServer().getClass().getPackage().getName();
                            mcVersion = mcVersion.substring(mcVersion.lastIndexOf(".") + 1);
                            mcVersion = mcVersion.substring(1, mcVersion.length()-3).replace("_", ".");

                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&m------------------------------"));
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2&lRPReview &aV" + currentVersion + " &eby &a" + RPreview.getInstance().getDescription().getAuthors()));
                            player.sendMessage("");
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aThere is a RPreview update available!"));
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aVersion: &f" + latestVersion));
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aUpdates: \n" + updateInfo));
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSupported MC Versions: &f" + StringUtils.join(testedVersions, ", ")));
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
        }.runTaskTimer(RPreview.getInstance(), 0, 24000L);
    }

    public void checkForUpdate() {
        try {
            //Latest version number.
            String latestVersionInfo = readFrom("https://api.spiget.org/v2/resources/"+RPreview.getInstance().getResourceId()+"/versions/latest");

            Type type = new TypeToken<JsonObject>() {
            }.getType();
            JsonObject object = gson.fromJson(latestVersionInfo, type);

            latestVersion = object.get("name").getAsString();
            updateAvailable = !latestVersion.equals(RPreview.getInstance().getDescription().getVersion());

            if (updateAvailable) {
                //Supported mc versions

                Type objectType = new TypeToken<JsonObject>(){}.getType();

                JsonObject pluginInfoObject = gson.fromJson(readFrom("https://api.spiget.org/v2/resources/"+RPreview.getInstance().getResourceId()+"/"), objectType);

                testedVersions = gson.fromJson(pluginInfoObject.get("testedVersions"), new TypeToken<List<String>>(){}.getType());

                //Update description

                JsonObject latestUpdateObject = gson.fromJson(readFrom("https://api.spiget.org/v2/resources/"+RPreview.getInstance().getResourceId()+"/updates/latest"), objectType);

                String descriptionBase64 = gson.fromJson(latestUpdateObject.get("description"), new TypeToken<String>(){}.getType());
                String decodedDescription = new String(Base64.getDecoder().decode(descriptionBase64));

                Pattern pat = Pattern.compile("<li>(.*)</li>");

                Matcher match = pat.matcher(decodedDescription);

                StringBuilder sb = new StringBuilder();

                while (match.find())
                    sb.append(ChatColor.GREEN).append(" - ").append(match.group(1)).append("\n");

                updateInfo = sb.toString();
            }
        } catch (Exception exception) {
            RPreview.getInstance().printError(exception, "Error occurred whilst pinging spiget.");
            try {
                RPreview.getInstance().printPluginError("Json received from spigot.", readFrom("https://api.spiget.org/v2/resources/"+RPreview.getInstance().getResourceId()+"/"));
            } catch (Exception ignored) {}
        }
    }

    public void update(Player initiator) {
        checkForUpdate();

        if (updateAvailable && !updating) {
            sendActionBar(initiator, ChatColor.translateAlternateColorCodes('&', "&e&lUPDATING &2&lRPreview &aV" + plugin.getDescription().getVersion() + " &e&l» &aV" + latestVersion + " &8[RETREIVING UPDATER]"));

            updating = true;
            boolean delete = true;

            try {
                if (!Bukkit.getPluginManager().isPluginEnabled("AutoUpdaterAPI")) {
                    delete = false;

                    //Download AutoUpdaterAPI
                    URL url = new URL("https://api.spiget.org/v2/resources/39719/download");
                    HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                    httpConnection.setRequestProperty("User-Agent", "SpigetResourceUpdater");
                    long completeFileSize = httpConnection.getContentLength();

                    BufferedInputStream in = new BufferedInputStream(httpConnection.getInputStream());
                    FileOutputStream fos = new FileOutputStream(new File(plugin.getDataFolder().getPath().substring(0, plugin.getDataFolder().getPath().lastIndexOf("/")) + "/AutoUpdaterAPI.jar"));
                    BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);

                    byte[] data = new byte[1024];
                    long downloadedFileSize = 0;
                    int x;
                    while ((x = in.read(data, 0, 1024)) >= 0) {
                        downloadedFileSize += x;

                        if (downloadedFileSize % 5000 == 0) {
                            final int currentProgress = (int) ((((double) downloadedFileSize) / ((double) completeFileSize)) * 15);

                            final String currentPercent = String.format("%.2f", (((double) downloadedFileSize) / ((double) completeFileSize)) * 100);

                            String bar = "&a:::::::::::::::";

                            bar = bar.substring(0, currentProgress + 2) + "&c" + bar.substring(currentProgress + 2);

                            sendActionBar(initiator, ChatColor.translateAlternateColorCodes('&', "&e&lUPDATING &2&lRPreview &aV" + plugin.getDescription().getVersion() + " &e&l» &aV" + latestVersion + " &8&l| " + bar + " &8&l| &2" + currentPercent + "% &8[DOWNLOADING UPDATER]"));
                        }

                        bout.write(data, 0, x);
                    }

                    bout.close();
                    in.close();

                    sendActionBar(initiator, ChatColor.translateAlternateColorCodes('&', "&e&lUPDATING &2&lRPreview &aV" + plugin.getDescription().getVersion() + " &e&l» &aV" + latestVersion + " &8[RUNNING UPDATER]"));

                    Plugin target = Bukkit.getPluginManager().loadPlugin(new File(plugin.getDataFolder().getPath().substring(0, plugin.getDataFolder().getPath().lastIndexOf("/")) + "/AutoUpdaterAPI.jar"));
                    target.onLoad();
                    Bukkit.getPluginManager().enablePlugin(target);
                }

                UpdateLocale locale = new UpdateLocale();

                locale.setUpdating("&eUPDATING &2&l%plugin% &aV%old_version% &e&l» &a%new_version%");
                locale.setUpdateComplete("&eUPDATED &2&l%plugin% &eTO &aV%new_version%");
                locale.setUpdatingDownload("&eUPDATING &2&l%plugin% &aV%old_version% &e&l» &aV%new_version% &8| %download_bar% &8| &a%download_percent%&e%");

                locale.setFileName("RPreview-" + latestVersion);
                locale.setPluginName("RPreview");

                new Updater(initiator, plugin, RPreview.getInstance().getResourceId(), locale, delete, true).update();
            } catch (Exception ex) {
                ex.printStackTrace();
                sendActionBar(initiator, ChatColor.translateAlternateColorCodes('&', "&f&lUPDATING &1&lRPreview &b&lV" + plugin.getDescription().getVersion() + " &b&l» &1&lV" + latestVersion + " &8[&c&lUPDATE FAILED &7&o(Check Console)&8]"));
            }
        }
    }

    /*
     * PRIVATE UTILITIES
     */

    private String readFrom(String url) throws IOException
    {
        try (InputStream is = new URL(url).openStream())
        {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }

            return sb.toString();
        }
    }

    private void sendActionBar(Player player, String message) {
        if (player != null)
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', message)));
    }
}