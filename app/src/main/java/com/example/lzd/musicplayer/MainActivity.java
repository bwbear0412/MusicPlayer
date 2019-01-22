package com.example.lzd.musicplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, SeekBar.OnSeekBarChangeListener, View.OnTouchListener {
    private ListView mMusicListView;
    private ImageButton mPlayButton;
    private ImageButton mPreButton;
    private ImageButton mNextButton;
    private SeekBar mSeekBar;
    private TextView mCurrentPlayTime;
    private TextView mMusicTotalTime;

    private MusicListAdapter mMusicListAdapter;
    private MusicPlayer mMusicPlayer;
    private List<MusicInfo> mPlayList;
    private MusicPlayer.PlayerListener mPlayerListener;
    private int mSeekPostion;

    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean FLAG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(
                MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
            setContentView(R.layout.activity_main);
            init();
        }
    }


    @Override
    protected void onDestroy() {
//        服务停止
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            stopService(new Intent(this, MyService.class));
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_music_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, 2);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void init() {
//        启动服务
        startService(new Intent(this, MyService.class));
        if (SavedData.GetPath(this, "Path", "CURRENT_SEARCH_PATH") == null) {
            SavedData.SavePath(this, "Path", "CURRENT_SEARCH_PATH", "/sdcard");
        }
        mMusicListView = (ListView) findViewById(R.id.play_list);
        mPlayButton = (ImageButton) findViewById(R.id.play_button);
        mPreButton = (ImageButton) findViewById(R.id.pre_button);
        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mSeekBar = (SeekBar) findViewById(R.id.play_seek);
        mCurrentPlayTime = (TextView) findViewById(R.id.play_current_time);
        mMusicTotalTime = (TextView) findViewById(R.id.play_total_time);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl);

//        监听器
        mMusicListView.setOnItemClickListener(this);
        mPlayButton.setOnClickListener(this);
        mPreButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);

        mMusicListView.setOnTouchListener(this);
        mPlayButton.setOnTouchListener(this);
        mPreButton.setOnTouchListener(this);
        mNextButton.setOnTouchListener(this);

//        列表
        getPlayList();
        mMusicListAdapter = new MusicListAdapter(this, mPlayList);
        mMusicListView.setAdapter(mMusicListAdapter);

//        音乐播放器
        mMusicPlayer = MusicPlayer.getInstance();
        getPlayList();
        mMusicPlayer.setPlayList(mPlayList);
        getPlayListener();
        mMusicPlayer.setPlayerListener(mPlayerListener);
    }

    public void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMusicListAdapter.setSelectedIndex(mMusicPlayer.getCurrentIndex());
                mMusicListAdapter.notifyDataSetChanged();
                mSeekBar.setProgress(mMusicPlayer.getCurrentProgress());
                mSeekBar.setMax(mMusicPlayer.getTotalTime());
                mCurrentPlayTime.setText(formatTime(mMusicPlayer.getCurrentProgress()));
                mMusicTotalTime.setText(formatTime(mMusicPlayer.getTotalTime()));
            }
        });
    }

    //    进度条时间
    private String formatTime(int t) {
        t /= 1000;
        int h;
        int m;
        int s;
        if (t > 3600) {
            h = t / 3600;
            m = (t % 3600) / 60;
            s = (t % 3600) % 60;
            return String.format("%02d:%02d:%02d", h, m, s);
        } else {
            m = t / 60;
            s = t % 60;
            return String.format("%02d:%02d", m, s);
        }
    }

    public void getPlayList() {
        mPlayList = new ArrayList<>();
        Cursor mCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.YEAR,
                        MediaStore.Audio.Media.MIME_TYPE,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA},
                null, null, null);
//        当前目录下有多少首歌
        int index = 0;
        if (mCursor != null) {
            mCursor.moveToFirst();
        } else {
            return;
        }
        while (mCursor.moveToNext()) {
            String string1 = SavedData.GetPath(this, "Path", "CURRENT_SEARCH_PATH");
            String string2 = "/sdcard" + mCursor.getString(9).substring(19);
            if (string2.contains(string1)) {
                MusicInfo musicInfo = new MusicInfo();
                musicInfo.setMusicId(index);
                musicInfo.setFileName(mCursor.getString(1));
                musicInfo.setMusicName(mCursor.getString(2));
                musicInfo.setMusicDuration(mCursor.getInt(3));
                musicInfo.setMusicArtist(mCursor.getString(4));
                musicInfo.setMusicAlbum(mCursor.getString(5));
                musicInfo.setMusicYear(mCursor.getString(6));
//                if ("audio/mpeg".equals(mCursor.getString(7).trim())) {
//                    musicInfo.setmFileType("mp3");
//                } else if ("audio/x-ms-wma".equals(mCursor.getString(7).trim())) {
//                    musicInfo.setmFileType("wma");
//                } else {
                musicInfo.setFileType(mCursor.getString(7));
//                }
                musicInfo.setFileSize(mCursor.getString(8));
                if (mCursor.getString(9) != null) {
                    musicInfo.setFilePath(mCursor.getString(9));
                }
                index++;
                mPlayList.add(musicInfo);
            }
        }
        mCursor.close();
        if (index == 0) {
            mMusicListView.setVisibility(View.GONE);
            MusicInfo musicInfos = new MusicInfo();
            musicInfos.setFileName("Unknown");
            musicInfos.setMusicArtist("Unknown");
            mPlayList.add(musicInfos);
            mPreButton.setClickable(false);
            mPlayButton.setClickable(false);
            mNextButton.setClickable(false);
            mSeekBar.setClickable(false);
            mSeekBar.setEnabled(false);
            mSeekBar.setSelected(false);
            mSeekBar.setFocusable(false);
        } else {
            mMusicListView.setVisibility(View.VISIBLE);
            mPreButton.setClickable(true);
            mPlayButton.setClickable(true);
            mNextButton.setClickable(true);
            mSeekBar.setClickable(true);
            mSeekBar.setEnabled(true);
            mSeekBar.setSelected(true);
            mSeekBar.setFocusable(true);
        }
    }

    public void getPlayListener() {
        if (mPlayerListener == null) {
            mPlayerListener = new MusicPlayer.PlayerListener() {
                @Override
                public void onPlay() {
                    updateUI();
                }

                @Override
                public void onPause() {
                    updateUI();
                }

                @Override
                public void onResume() {
                    updateUI();
                }

                @Override
                public void onPlayNext() {
                    updateUI();
                }

                @Override
                public void onPlayPrev() {
                    updateUI();
                }

                @Override
                public void onProgressUpdate(int progress) {
                    updateUI();
                }
            };
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 2) {
            if (resultCode == 2) {
                swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_button:
                try {
                    if (mMusicPlayer.isPlaying()) {
                        mPlayButton.setImageResource(R.drawable.ic_music_pause);
                        mMusicPlayer.pause();
                    } else {
                        mPlayButton.setImageResource(R.drawable.ic_music_play);
                        mMusicPlayer.resume();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.pre_button:
                mPlayButton.setImageResource(R.drawable.ic_music_play);
                mMusicPlayer.playPrev();
                ShowToast.ShowShortToast(this, "当前播放音乐的是:" + mPlayList.get(mMusicPlayer.getCurrentIndex()).getFileName());
//                Toast.makeText(MainActivity.this, "\n当前播放音乐的是:\n" + mPlayList.get(mMusicPlayer.getCurrentIndex()).getFileName(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.next_button:
                mPlayButton.setImageResource(R.drawable.ic_music_play);
                mMusicPlayer.playNext();
                ShowToast.ShowShortToast(this, "当前播放音乐的是:" + mPlayList.get(mMusicPlayer.getCurrentIndex()).getFileName());
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ShowToast.ShowShortToast(this, "当前播放音乐的是:" + mPlayList.get(mMusicPlayer.getCurrentIndex()).getFileName());
        mPlayButton.setImageResource(R.drawable.ic_music_play);
        mMusicPlayer.play(position);
    }

    @Override
    public void onRefresh() {
        HandlerUtils.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMusicListAdapter.getMusicList().clear();
                getPlayList();
                mMusicListAdapter.getMusicList().addAll(mPlayList);
                mMusicPlayer.setPlayList(mPlayList);
                mMusicListAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                ShowToast.ShowShortToast(MainActivity.this, "已刷新列表");
            }
        }, 1000);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mSeekPostion = progress;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mMusicPlayer.setCurrentProgress(mSeekPostion);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.play_button:
                if (mMusicListView.getVisibility() == View.VISIBLE) {
                    if (mMusicPlayer.isPlaying()) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            mPlayButton.setImageResource(R.drawable.ic_music_play_focused);
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            mPlayButton.setImageResource(R.drawable.ic_music_play);
                        }
                    } else {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            mPlayButton.setImageResource(R.drawable.ic_music_pause_focused);
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            mPlayButton.setImageResource(R.drawable.ic_music_pause);
                        }
                    }
                }
                break;
            case R.id.pre_button:
                if (mMusicListView.getVisibility() == View.VISIBLE) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mPreButton.setImageResource(R.drawable.ic_music_previous_focused);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        mPreButton.setImageResource(R.drawable.ic_music_previous);
                    }
                }
                break;
            case R.id.next_button:
                if (mMusicListView.getVisibility() == View.VISIBLE) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mNextButton.setImageResource(R.drawable.ic_music_next_focused);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        mNextButton.setImageResource(R.drawable.ic_music_next);
                    }
                }
                break;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                switch (permissions[0]) {
                    case Manifest.permission.READ_EXTERNAL_STORAGE:
                        if (grantResults.length > 0
                                && grantResults[0]
                                == PackageManager.PERMISSION_GRANTED) {
                            Log.i("Shit!", "申请成功");
                            setContentView(R.layout.activity_main);
                            init();
                        } else {
                            Log.i("Shit!", "申请失败");
                            System.exit(0);
                        }
                        break;
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
