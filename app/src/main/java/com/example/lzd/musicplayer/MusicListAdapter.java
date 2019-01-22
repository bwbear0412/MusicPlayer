package com.example.lzd.musicplayer;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class MusicListAdapter extends BaseAdapter {
    private List<MusicInfo> mMusicList = new ArrayList<MusicInfo>();
    private Context mContext;
    private int mSelectedIndex = -1;

    public List<MusicInfo> getMusicList() {
        return mMusicList;
    }

    public MusicListAdapter(Context mContext, List<MusicInfo> mMusicDatas) {
        this.mContext = mContext;
        this.mMusicList = mMusicDatas;
    }



    @Override
    public int getCount() {
        return mMusicList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMusicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_music_list, null, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.music_name);
            viewHolder.author = (TextView) convertView.findViewById(R.id.music_author);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(mMusicList.get(position).getMusicName());
        viewHolder.author.setText(mMusicList.get(position).getMusicArtist());
        if (position == mSelectedIndex) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.list_selected));
            viewHolder.title.setSelected(true);
            viewHolder.title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        } else {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            viewHolder.title.setEllipsize(TextUtils.TruncateAt.END);
        }
        return convertView;
    }

    public void setSelectedIndex(int mSelectedIndex) {
        this.mSelectedIndex = mSelectedIndex;
    }

    private class ViewHolder {
        public TextView title;
        public TextView author;
    }
}