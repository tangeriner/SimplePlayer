package com.sunny.simple_video.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunny.simple_video.R;
import com.sunny.simple_video.net.ThumbnailLoader;
import com.sunny.simple_video.utils.SDCardUtils;

import java.util.List;

/**
 * Created by Sunny on 2017/8/7.
 */


public class VideoPathAdapter extends BaseSingleAdapter<VideoPathAdapter.VideoPathHolder, List<String>> {

    public VideoPathAdapter(int layoutId, List<List<String>> list) {
        super(layoutId, list);
    }

    @Override
    public VideoPathHolder createViewHolder(View itemView) {
        return new VideoPathHolder(itemView);
    }

    @Override
    public void convert(VideoPathHolder holder, List<String> files) {
        final String file = files.get(0);
        String SDCardPath = SDCardUtils.getSDCardPath();
        String path = file.replace(SDCardPath, "");
        if (path.length() == 1) {
            path = "根目录";
        } else {
            path = path.replaceFirst("/", "");
            int pos = path.indexOf("/");
            if (pos > 0) {
                path = path.substring(0, pos);
            }
        }
        String text = path + "    " + files.size();
        holder.textView.setText(text);
        holder.imageView.setImageResource(R.color.black);
        ThumbnailLoader.create()
                .url(file)
                .size(320, 320)
                .into(holder.imageView, new ThumbnailLoader.LoadListener() {
                    @Override
                    public void completed(String url, ImageView imageView, Bitmap bitmap) {
//                        if (file.equals(imageView.getTag())) {
                            imageView.setImageBitmap(bitmap);
//                        }
                    }
                })
                .load();
    }


    class VideoPathHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        public VideoPathHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_icon);
            textView = (TextView) itemView.findViewById(R.id.tv_file_num);
        }
    }
}