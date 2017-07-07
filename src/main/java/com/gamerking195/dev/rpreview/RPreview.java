package com.gamerking195.dev.rpreview;

import com.gamerking195.dev.rpreview.command.RPreviewCommand;
import com.gamerking195.dev.rpreview.listener.PlayerInteractListener;
import com.gamerking195.dev.rpreview.util.UtilUpdater;
import lombok.Getter;
import org.bstats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class RPreview extends JavaPlugin {

    @Getter
    private static RPreview instance;

    @Getter
    private int resourceId = 43475;

    private Logger log = getLogger();

    @Override
    public void onEnable() {
        instance = this;

        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), instance);

        this.getCommand("rpreview").setExecutor(new RPreviewCommand());
        this.getCommand("rpr").setExecutor(new RPreviewCommand());

        UtilUpdater.getInstance().init();

        new Metrics(instance);
    }

    public void printError(Exception ex, String extraInfo) {
        log.severe("A severe error has occurred with the RPreview plugin.");
        log.severe("If you cannot figure out this error on your own please copy and paste everything from here to END ERROR and post it at https://github.com/GamerKing195/RPreview/issues.");
        log.severe("");
        log.severe("============== BEGIN ERROR ==============");
        log.severe("PLUGIN VERSION: RPreview V" + getDescription().getVersion());
        log.severe("");
        log.severe("PLUGIN MESSAGE: "+extraInfo);
        log.severe("");
        log.severe("MESSAGE: " + ex.getMessage());
        log.severe("");
        log.severe("STACKTRACE: ");
        ex.printStackTrace();
        log.severe("");
        log.severe("============== END ERROR ==============");
    }

    public void printPluginError(String header, String message) {
        log.severe("============== BEGIN ERROR ==============");
        log.severe(header);
        log.severe("");
        log.severe("PLUGIN VERSION: RPReview V" + getDescription().getVersion());
        log.severe("");
        log.severe("PLUGIN MESSAGE: "+message);
        log.severe("");
        log.severe("============== END ERROR ==============");
    }
}
