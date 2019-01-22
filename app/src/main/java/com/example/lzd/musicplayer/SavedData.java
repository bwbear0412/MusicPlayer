package com.example.lzd.musicplayer;

import android.content.Context;
import android.content.SharedPreferences;

public class SavedData {
    public static void SavePath(Context context, String t1, String t2, String t3) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(t1, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(t2, t3);
        editor.commit();
    }

    public static String GetPath(Context context, String t1, String t2) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(t1, Context.MODE_PRIVATE);
        return sharedPreferences.getString(t2, null);
    }
}
