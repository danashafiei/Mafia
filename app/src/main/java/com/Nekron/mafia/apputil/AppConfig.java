package com.Nekron.mafia.apputil;

import android.content.Context;
import android.content.SharedPreferences;

import com.Nekron.mafia.R;

public class AppConfig {

    private Context context;
    private SharedPreferences sharedPreferences;

    public AppConfig(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.pref_file_key), Context.MODE_PRIVATE);
    }

    public boolean isUserLogin(){

        return sharedPreferences.getBoolean(context.getString(R.string.pref_is_user_login), false);
    }

    public void updateUserLoginStatus(boolean status){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.pref_is_user_login), status);
        editor.apply();
    }

    public void saveUsernameOfUser(String username){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_username_of_user), username);
        editor.apply();

    }
    public String getUsernameOfUser(){
        return sharedPreferences.getString(context.getString(R.string.pref_username_of_user), "Unknown");
    }
    public void saveRoomName(String roomName){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("room_name", roomName);
        editor.apply();
    }
    public String getRoomName(){
        return sharedPreferences.getString("room_name", "Unknown");
    }
    public boolean isGameStarted(){
        return sharedPreferences.getBoolean("isgamestarted", false);
    }
    public void updateGameStartedStatus(boolean status){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isgamestarted", status);
        editor.apply();
    }
    public void saveYourRole(String yourRole){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("your_role", yourRole);
        editor.apply();

    }
    public String getYourRole(){
        return sharedPreferences.getString("your_role", "Unknown");
    }
    public void saveRoomAdmin(boolean status){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.pref_room_admin), status);
        editor.apply();
    }
    public boolean getRoomAdmin(){
        return sharedPreferences.getBoolean(context.getString(R.string.pref_room_admin), false);
    }
}
