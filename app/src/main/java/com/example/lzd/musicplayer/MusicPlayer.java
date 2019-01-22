package com.example.lzd.musicplayer;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayer {
    public interface PlayerListener {
        void onPlay();

        void onPause();

        void onResume();

        void onPlayNext();

        void onPlayPrev();

        void onProgressUpdate(int progress);
    }

    private static MusicPlayer mPlayer;
    private List<MusicInfo> mPlayList;
    private boolean isPlaying;
    private int mCurrentIndex;
    private int mCurrentProgress;
    private MediaPlayer mMediaPlayer;
    private PlayerListener mPlayerListener;
    private Timer mTimer;
    private int mTotalTime;

    public static MusicPlayer getInstance() {
        if (mPlayer == null) {
            mPlayer = new MusicPlayer();
        }
        return mPlayer;
    }

    public void play(int index) {
        MusicInfo musicInfo = mPlayList.get(index);
        if (mMediaPlayer != null) {
            if (isPlaying) {
                isPlaying = false;
                mMediaPlayer.pause();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mCurrentIndex = index;
        mCurrentProgress = 0;
        isPlaying = true;
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(musicInfo.getFilePath());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            startTimer();
            if (mPlayerListener != null) {
                mPlayerListener.onPlay();
            }
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                //End
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playNext();
                }
            });
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
        }
    }

    public void pause() {
        if (isPlaying) {
            mMediaPlayer.pause();
            isPlaying = false;
            if (mPlayerListener != null) {
                mPlayerListener.onPause();
            }
        }
    }

    public void resume() {
        if (!isPlaying) {
            mMediaPlayer.start();
            isPlaying = true;
            if (mPlayerListener != null) {
                mPlayerListener.onResume();
            }
        }
    }

    public void playNext() {
        if (mPlayList.size() - 1 == mCurrentIndex) {
            play(0);
        } else {
            play(mCurrentIndex + 1);
        }
        if (mPlayerListener != null) {
            mPlayerListener.onPlayNext();
        }
    }

    public void playPrev() {
        if (mCurrentIndex == 0) {
            play(mPlayList.size() - 1);
        } else {
            play(mCurrentIndex - 1);
        }
        if (mPlayerListener != null) {
            mPlayerListener.onPlayPrev();
        }
    }

    public List<MusicInfo> getPlayList() {
        return mPlayList;
    }

    public void setPlayList(List<MusicInfo> mPlayList) {
        this.mPlayList = mPlayList;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public int getCurrentProgress() {
        return mCurrentProgress;
    }

    public void setCurrentProgress(int mCurrentProgress) {
        this.mCurrentProgress = mCurrentProgress;
        mMediaPlayer.seekTo(mCurrentProgress);
    }

    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    public void setCurrentIndex(int mCurrentIndex) {
        this.mCurrentIndex = mCurrentIndex;
    }

    public PlayerListener getPlayerListener() {
        return mPlayerListener;
    }

    public void setPlayerListener(PlayerListener mPlayerListener) {
        this.mPlayerListener = mPlayerListener;
    }

    public int getTotalTime() {
        setTotalTime();
        return mTotalTime;
    }

    public void setTotalTime() {
        this.mTotalTime = mMediaPlayer.getDuration();
    }

    private void startTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isPlaying) {
                    mCurrentProgress = mMediaPlayer.getCurrentPosition();
                    if (mPlayerListener != null) {
                        mPlayerListener.onProgressUpdate(mCurrentProgress);
                    }
                }
            }
        }, 0, 1000);
    }
}
