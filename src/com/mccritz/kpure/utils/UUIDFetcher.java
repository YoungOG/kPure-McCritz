package com.mccritz.kpure.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
 
public class UUIDFetcher {
   
    /**
    *  
    * UUID Fetcher v.2.0 by Max_Plays (02/14/2016)
    *
    * You may:
    * - Use this class in your project
    * - Share it only with your friends
    *
    * You may not:
    * - Re-upload it on the internet
    * - Pretend it belongs to you
    * - Delete this note
    *
    */
   
    public static String getUUID(String playerName){
        String uuid = "";
       
        try{
        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName + "?");
       
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        String line = reader.readLine();
       
        String[] id = line.split(",");
       
        uuid = id[0];
        uuid = uuid.substring(7, 39);
       
        }catch(IOException e){
            e.printStackTrace();
        }
        return uuid;
   
    }
 
}