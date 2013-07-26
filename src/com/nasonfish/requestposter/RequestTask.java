package com.nasonfish.requestposter;

import com.google.common.io.Files;
import com.nyancraft.reportrts.ReportCreateEvent;
import com.nyancraft.reportrts.data.HelpRequest;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This will be our task that will post to pages
 * and be off the main thread, to decrease hanging.
 * 
 * @author nasonfish
 */
public class RequestTask extends BukkitRunnable {

    ReportCreateEvent event;
    File config;
    RequestPoster plugin;
    
    public RequestTask(RequestPoster plugin, ReportCreateEvent event, File config){
        this.event = event;
        this.config = config;
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        HelpRequest request = event.getRequest();
        for(String line : this.getAddresses()){
            if(line.startsWith("#")) continue;
            plugin.getLogger().info("Posting to URL '" + line + "'...");
            if(this.connect(line, this.getPostData(request))){
                //plugin.getLogger().info("Debug - Successfully posted to " + line + ". :-)");
            }
        }
    }
    
    public boolean connect(String urlString, String params){
        if(params == null || params.equals("")){
            return false;
        }
        try{
            URL url = new URL(urlString); 
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();           
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false); 
            connection.setRequestMethod("POST"); 
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(params.getBytes().length));
            connection.setUseCaches (false);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
            wr.writeBytes(params);
            wr.flush();
            wr.close();
            connection.getContent();
            connection.disconnect();
        } catch (Exception e){
            plugin.getLogger().severe("Error posting to address " + urlString + ". Error: " + e.getMessage());
            return false;
        }
        return true;
    }
    
    public List<String> getAddresses(){
        try{
            return Files.readLines(config, Charset.defaultCharset());
        } catch(IOException ex){
            plugin.getLogger().severe("Error reading addresses from the config file - check that this user has read permissions. (Error: "+ex.getMessage()+")");
        }
        return new ArrayList<String>();
    }
    
    public String getPostData(HelpRequest request){
        String data = "";
        try{
        data += "id=" + URLEncoder.encode(request.getId()+"", "utf-8");
        data += "&message=" + URLEncoder.encode(request.getMessage()+"", "utf-8");
        data += "&modid=" + URLEncoder.encode(request.getModId()+"", "utf-8");
        data += "&modname=" + URLEncoder.encode(request.getModName()+"", "utf-8");
        data += "&modtimestamp=" + URLEncoder.encode(request.getModTimestamp()+"", "utf-8");
        data += "&name=" + URLEncoder.encode(request.getName()+"", "utf-8");
        data += "&pitch=" + URLEncoder.encode(request.getPitch()+"", "utf-8");
        data += "&status=" + URLEncoder.encode(request.getStatus()+"", "utf-8");
        data += "&timestamp=" + URLEncoder.encode(request.getTimestamp()+"", "utf-8");
        data += "&world=" +URLEncoder.encode( request.getWorld()+"", "utf-8");
        data += "&x=" + URLEncoder.encode(request.getX()+"", "utf-8");
        data += "&y=" + URLEncoder.encode(request.getY()+"", "utf-8");
        data += "&yaw=" + URLEncoder.encode(request.getYaw()+"", "utf-8");
        data += "&z=" + URLEncoder.encode(request.getZ()+"", "utf-8");
        plugin.getLogger().info(data);
        } catch(Exception e){
            plugin.getLogger().severe("Unable to send POST data - Could not encode the data correctly. ("+e.getMessage()+")");
            return "";
        }
        return data;
    }
    
}
