package com.sunny.simple_video.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sunny.simple_video.BaseActivity;
import com.sunny.simple_video.R;
import com.sunny.simple_video.adapter.BaseSingleAdapter;
import com.sunny.simple_video.adapter.VideoPathAdapter;
import com.sunny.simple_video.helper.ToastHelper;
import com.sunny.simple_video.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity {
    private VideoPathAdapter mVideoPathAdapter;
    private List<List<String>> mFileList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
//        findVideoFilePath();
        find();
    }

    private void find() {
        Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<String>> e) throws Exception {
                String[] projection = new String[]{MediaStore.Video.VideoColumns.DATA};
                Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null,
                        MediaStore.Files.FileColumns.DATA + " asc");
                if (cursor != null) {
                    List<String> files = new ArrayList<>();
                    String pathTemp = "null";
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
                        if (data != null) {
                            String path = data.substring(0, data.lastIndexOf("/"));
                            Logger.i(TAG, "data=" + data + " size=" + files.size());
                            if (data.startsWith(pathTemp)) {
                                files.add(data);
                            } else {
                                pathTemp = path;
                                if (files.size() > 0) {
                                    Logger.i(TAG, files.size() + "");
                                    e.onNext(files);
                                    files = new ArrayList<>();
                                }
                                files.add(data);
                            }
                        }
                    }
                    if (files.size() > 0) {
                        e.onNext(files);
                    }
                    cursor.close();
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        ToastHelper.show("开始");
                    }

                    @Override
                    public void onNext(@NonNull List<String> files) {
                        mFileList.add(files);
                        mVideoPathAdapter.notifyItemInserted(mFileList.size() - 1);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        ToastHelper.show(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        if (mFileList.size() == 0) {
                            ToastHelper.show("未找到视频文件");
                        }
                    }
                });

    }

    private void initView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager gridDivider = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridDivider);
        mVideoPathAdapter = new VideoPathAdapter(R.layout.item_video_directory, mFileList);
        mVideoPathAdapter.setOnItemClickListener(new BaseSingleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                Intent intent = new Intent(MainActivity.this, VideoGalleryActivity.class);
                ArrayList<String> list = (ArrayList<String>) mFileList.get(pos);
                intent.putStringArrayListExtra(VideoGalleryActivity.EXTRA_FILES, list);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(mVideoPathAdapter);
    }
}

