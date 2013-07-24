package com.nasonfish.requestposter;

import com.nyancraft.reportrts.ReportCreateEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * A small plugin created to post
 * request data from ReportRTS
 * to a url, for use in statistics
 * on websites or IRC notifications.
 * 
 * @author nasonfish
 */
public class RequestPoster extends JavaPlugin implements Listener {
    
    File config;
    
    @Override
    public void onEnable(){
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getDataFolder().mkdir();
        config = new File(this.getDataFolder().getPath() + File.separator + "config.txt");
        if(!config.exists()){
            try {
                config.createNewFile();
                FileWriter writer = new FileWriter(config);
                PrintWriter out = new PrintWriter(writer);
                out.println("# RequestPoster config file - Put one HTTP address per line to post to, and we'll post all the data listed on our wiki (coming soon) to the page.");
                out.println("# Comments must _start_ the line with a `#` character, otherwise, we'll try to use it as an address.");
                out.println("# Thanks for using RequestPoster. :-)");
                this.getLogger().info("Configuration file successfully created in /plugins/RequestPoster/config.txt. Feel free to edit it.");
            } catch (IOException ex) {
                this.getLogger().log(Level.SEVERE,"Error creating/writing to config file for RequestPoster. ({0})."
                    + " Perhaps you should check if this user has permission to create files in /plugins/RequestPoster/."
                    + " Here's the message we got, for why we can't create the file: ", ex.getMessage()); 
                ex.printStackTrace();
            }
        }
    }
    
    @Override
    public void onDisable(){
        
    }
    
    @EventHandler
    public void onRequestCreate(final ReportCreateEvent event){
        BukkitTask task = new RequestTask(this, event, config).runTaskAsynchronously(this);
    }
    
}
