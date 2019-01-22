package com.example.lzd.musicplayer;

public class MusicInfo {
    private int mMusicId;
    private String mFileName;
    private String mMusicName;
    private int mMusicDuration;
    private String mMusicArtist;
    private String mMusicAlbum;
    private String mMusicYear;
    private String mFileType;
    private String mFileSize;
    private String mFilePath;

    public int getMusicId() {
        return mMusicId;
    }

    public void setMusicId(int mMusicId) {
        this.mMusicId = mMusicId;
    }

    public String getFileName() {
        if (mFileName == null) {
            return "Unknown";
        }
        return mFileName;
    }

    public void setFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    public String getMusicName() {
        if (mMusicName == null) {
            return "Unknown";
        }
        return mMusicName;
    }

    public void setMusicName(String mMusicName) {
        this.mMusicName = mMusicName;
    }

    public int getMusicDuration() {
        return mMusicDuration;
    }

    public void setMusicDuration(int mMusicDuration) {
        this.mMusicDuration = mMusicDuration;
    }

    public String getMusicArtist() {
        if (mMusicArtist == null) {
            return "Unknown";
        }
        return mMusicArtist;
    }

    public void setMusicArtist(String mMusicArtist) {
        this.mMusicArtist = mMusicArtist;
    }

    public String getMusicAlbum() {
        if (mMusicAlbum == null) {
            return "Unknown";
        }
        return mMusicAlbum;
    }

    public void setMusicAlbum(String mMusicAlbum) {
        this.mMusicAlbum = mMusicAlbum;
    }

    public String getMusicYear() {
        if (mMusicYear == null) {
            return "Unknown";
        }
        return mMusicYear;
    }

    public void setMusicYear(String mMusicYear) {
        this.mMusicYear = mMusicYear;
    }

    public String getFileType() {
        if (mFileType == null) {
            return "Unknown";
        }
        return mFileType;
    }

    public void setFileType(String mFileType) {
        this.mFileType = mFileType;
    }

    public String getFileSize() {
        if (mFileSize == null) {
            return "Unknown";
        }
        return mFileSize;
    }

    public void setFileSize(String mFileSize) {
        this.mFileSize = mFileSize;
    }

    public String getFilePath() {
        if (mFilePath == null) {
            return "Unknown";
        }
        return mFilePath;
    }

    public void setFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
    }
}
