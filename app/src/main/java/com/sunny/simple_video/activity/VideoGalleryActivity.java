package com.sunny.simple_video.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sunny.player.PlayerActivity;
import com.sunny.simple_video.BaseActivity;
import com.sunny.simple_video.R;
import com.sunny.simple_video.adapter.BaseSingleAdapter;
import com.sunny.simple_video.adapter.VideoGalleryAdapter;
import com.sunny.simple_video.logger.Logger;
import com.sunny.simple_video.utils.SDCardUtils;

import java.util.ArrayList;
import java.util.List;

public class VideoGalleryActivity extends BaseActivity {
    public static final String EXTRA_FILES = "files";
    private List<String> mListFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_gallery);
        List<String> list = getIntent().getStringArrayListExtra(EXTRA_FILES);
        mListFiles = new ArrayList<>();
        if (list != null) {
            mListFiles.addAll(list);
        }
        initView();
    }

    private void initView() {
        if (mListFiles.size() > 0) {
            String title = mListFiles.get(0);
            title = title.replace(SDCardUtils.getSDCardPath()+"/", "");
            title = title.substring(0, title.lastIndexOf("/"));
            setTitle(title);
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_video_gallery);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        final VideoGalleryAdapter adapter = new VideoGalleryAdapter(R.layout.item_video_gallery, mListFiles);
        adapter.setOnItemClickListener(new BaseSingleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                Uri uri = Uri.parse(mListFiles.get(pos));
                Intent intent = new Intent(VideoGalleryActivity.this, PlayerActivity.class);
                intent.setDataAndType(uri, "video/mp4");
                startActivity(intent);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 0) {
                    Logger.i(TAG, "stop");
                    adapter.setScrolling(false);
                } else {
                    Logger.i(TAG, "scroll");
                    adapter.setScrolling(true);
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
