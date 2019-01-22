package com.example.lzd.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    //    private static final String DEFAULT_SEARCH_PATH = Environment.getExternalStorageDirectory().getPath();
    private static final String DEFAULT_SEARCH_PATH = "/sdcard/";
    private String CURRENT_SEARCH_PATH;
    private static final String PATH = "PATH";
    private Button settings_bt_path;
//    private String FLAG;
//    private int COUNT = 0;

    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_settings);
        } catch (Exception e) {
            Log.e("Shit!", e.getMessage());
        }
        init();
    }

    private void init() {
        try {
            CURRENT_SEARCH_PATH = SavedData.GetPath(this, "Path", "CURRENT_SEARCH_PATH");
        } catch (Exception e) {
            Log.e("Shit!", e.getMessage());
        }
        settings_bt_path = (Button) findViewById(R.id.settings_bt_path);
//        settings_bt_path.setText("歌曲搜索路径\r\n" + CURRENT_SEARCH_PATH == null ? DEFAULT_SEARCH_PATH : CURRENT_SEARCH_PATH);
        if (CURRENT_SEARCH_PATH == null) {
            settings_bt_path.setText("歌曲搜索路径\r\n" + DEFAULT_SEARCH_PATH);
        } else {
            settings_bt_path.setText("歌曲搜索路径\r\n" + CURRENT_SEARCH_PATH);
        }
//        FLAG = CURRENT_SEARCH_PATH == null ? DEFAULT_SEARCH_PATH : CURRENT_SEARCH_PATH;
        settings_bt_path.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_bt_path:
                try {
//                    COUNT++;
                    Intent intent = new Intent(SettingsActivity.this, FileDialogActivity.class);
                    intent.putExtra(PATH, CURRENT_SEARCH_PATH);
                    startActivityForResult(intent, 1);
                } catch (Exception e) {
                    Log.e("Shit!", e.getMessage());
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 1) {
                try {
                    CURRENT_SEARCH_PATH = data.getStringExtra(PATH);
//                    Log.i("FLAG", FLAG);
//                    if (CURRENT_SEARCH_PATH.equals(FLAG)) {
//                        getIntent().putExtra("PATH_NO_CHANGE", true);
//                    }
//                    FLAG = CURRENT_SEARCH_PATH;
                    SavedData.SavePath(this, "Path", "CURRENT_SEARCH_PATH", CURRENT_SEARCH_PATH);
                    settings_bt_path.setText("歌曲搜索路径\r\n" + CURRENT_SEARCH_PATH);
                } catch (Exception e) {
                    Log.e("Shit!", e.getMessage());
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
//        if (COUNT == 0) {
//            getIntent().putExtra("PATH_NO_CHANGE", true);
//        }
        setResult(2, getIntent());
        super.onBackPressed();
    }
}
