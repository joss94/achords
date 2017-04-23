package com.joss.achords.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
 * Created by joss on 03/03/17.
 */

public class Songlist {

    private String name;
    private String user;
    private List<UUID> songsIds;

    public Songlist() {
        songsIds = new ArrayList<>();
        name = "";
        user="";
    }

    Songlist(JSONObject obj){
        this.songsIds = new ArrayList<>();
        try {
            this.name = obj.getString("name");
            this.user = obj.getString("user");
            JSONArray array = obj.getJSONArray("songsId");
            for(int i =0; i<array.length();i++){
                songsIds.add(UUID.fromString(array.getString(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<UUID> getSongsIds() {
        return songsIds;
    }

    public void setSongsIds(ArrayList<UUID> songsIds) {
        this.songsIds = songsIds;
    }

    JSONObject toJson(){
        JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            obj.put("name", name);
            obj.put("user", user);
            for(UUID id:songsIds){
                array.put(id.toString());
            }
            obj.put("songsId", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
