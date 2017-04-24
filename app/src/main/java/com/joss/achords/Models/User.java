package com.joss.achords.Models;

import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable{

    private static final long serialVersionUID = -7399546295624447919L;

    private static final String USER_ID = "user_id";
    private static final String USER_NAME = "user_name";

    private String name;
    private UUID id;

    public User() {
        id = UUID.randomUUID();
    }

    public static User parseJson(String json){
        try {
            JSONObject obj = new JSONObject(json);
            User user = new User();
            user.setId(UUID.fromString(obj.getString(USER_ID)));
            user.setName(obj.getString(USER_NAME));
            return user;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public User(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void save(SharedPreferences sharedPreferences) {
        sharedPreferences.edit().putString(USER_ID, id.toString()).apply();
        sharedPreferences.edit().putString(USER_NAME, name).apply();
    }

    public static User getUser(SharedPreferences sharedPreferences) {
        User user = new User();
        user.setName(sharedPreferences.getString(USER_NAME, null));
        user.setId(UUID.fromString(sharedPreferences.getString(USER_ID, UUID.randomUUID().toString())));
        return user.getName() == null ? null : user;
    }

    public JSONObject toJSON(){
        try {
            JSONObject obj = new JSONObject();
            obj.put(USER_NAME, name);
            obj.put(USER_ID, id.toString());
            return obj;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
