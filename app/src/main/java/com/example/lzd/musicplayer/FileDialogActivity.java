package com.example.lzd.musicplayer;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class FileDialogActivity extends ListActivity implements View.OnClickListener {
    private static final String START_PATH = "START_PATH";
    private static final String ROOT = "/sdcard";

    private String currentPath;
    private String parentPath;

    private TextView fd_tv_path;
    private FloatingActionButton fab;
    private String PATH = null;

    private List<String> mPath = null;
    private ArrayList<HashMap<String, Object>> mList;
    private HashMap<String, Integer> list_position = new HashMap<String, Integer>();

    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_file_dialog);
        } catch (Exception e) {
            Log.i("Shit!", e.getMessage());
        }
        init();
        String startPath = PATH;
//        String startPath = getIntent().getStringExtra(START_PATH);
        getDir(startPath == null ? ROOT : startPath);
    }

    private void getDir(String dirPath) {
        Integer position = list_position.get(parentPath);
        getDirImpl(dirPath);
        if (position != null) {
            getListView().setSelection(position);
        }
    }

    private void getDirImpl(String dirPath) {
        currentPath = dirPath;
        List<String> item = new ArrayList<String>();
        mPath = new ArrayList<String>();
        mList = new ArrayList<HashMap<String, Object>>();
        File f = new File(currentPath);
        File[] files = f.listFiles();
        if (files == null) {
            currentPath = ROOT;
            f = new File(currentPath);
            files = f.listFiles();
        }
        fd_tv_path.setText(currentPath);
        if (!currentPath.equals(ROOT)) {
//            item.add(ROOT);
//            addItem(ROOT, R.drawable.dir);
//            mPath.add(ROOT);
            item.add("..");
            addItem("..", R.drawable.dir);
            mPath.add(parentPath = f.getParent());
        }
        TreeMap<String, String> dirMap = new TreeMap<String, String>();
        TreeMap<String, String> dirPathMap = new TreeMap<String, String>();
//        TreeMap<String, String> fileMap = new TreeMap<String, String>();
//        TreeMap<String, String> filePathMap = new TreeMap<String, String>();
        for (File file : files) {
            String dirOrfileName = file.getName();
            String dirOrfilePath = file.getPath();
            if (file.isDirectory()) {
                dirMap.put(dirOrfileName, dirOrfileName);
                dirPathMap.put(dirOrfileName, dirOrfilePath);
            }
//            else if (file.isFile()) {
//                fileMap.put(dirOrfileName, dirOrfileName);
//                filePathMap.put(dirOrfileName, dirOrfilePath);
//            }
        }
        item.addAll(dirMap.tailMap("").values());
//        item.addAll(fileMap.tailMap("").values());
        mPath.addAll(dirPathMap.tailMap("").values());
//        mPath.addAll(filePathMap.tailMap("").values());
        SimpleAdapter simpleAdapter = new SimpleAdapter(
                FileDialogActivity.this,
                mList,
                R.layout.item_file_dialog,
                new String[]{"KEY", "IMAGE"},
                new int[]{R.id.fd_tv, R.id.fd_iv}
        );
        for (String dir : dirMap.tailMap("").values()) {
            addItem(dir, R.drawable.dir);
        }
//        for (String file : fileMap.tailMap("").values()) {
//            addItem(file, R.drawable.file);
//        }
        simpleAdapter.notifyDataSetChanged();
        setListAdapter(simpleAdapter);
    }

    private void addItem(String fileName, int imageId) {
        HashMap<String, Object> item = new HashMap<String, Object>();
        item.put("KEY", fileName);
        item.put("IMAGE", imageId);
        mList.add(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
        File file = new File(mPath.get(position));
        if (file.isDirectory()) {
            if (file.canRead()) {
                list_position.put(currentPath, position);
                PATH = file.getPath();
//                Toast.makeText(FileDialogActivity.this, file.getPath(), Toast.LENGTH_SHORT).show();
                getDir(mPath.get(position));
            } else {
                Toast.makeText(FileDialogActivity.this, "没有权限", Toast.LENGTH_SHORT).show();
            }
        }
//        } else if (file.isFile()) {
//            Intent intent = getIntent();
//            intent.putExtra("PATH", file.getPath());
//            setResult(1, intent);
//            Toast.makeText(FileDialogActivity.this, file.getPath(), Toast.LENGTH_SHORT).show();
//            finish();
//        }
    }

    public void init() {
        PATH = getIntent().getStringExtra("PATH");
        fd_tv_path = (TextView) findViewById(R.id.path);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                getIntent().putExtra("PATH", PATH);
                setResult(1, getIntent());
                finish();
                break;
            default:
                break;
        }
    }
}
