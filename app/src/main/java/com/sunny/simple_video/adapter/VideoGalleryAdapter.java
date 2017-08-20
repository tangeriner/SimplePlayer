package com.sunny.simple_video.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.sunny.simple_video.R;
import com.sunny.simple_video.net.ThumbnailLoader;

import java.util.List;

/**
 * Created by Sunny on 2017/8/8.
 */

public class VideoGalleryAdapter extends BaseSingleAdapter<VideoGalleryAdapter.VideoGalleryHolder, String> {

    public VideoGalleryAdapter(int layoutId, List<String> list) {
        super(layoutId, list);
    }

    @Override
    public VideoGalleryHolder createViewHolder(View itemView) {
        return new VideoGalleryHolder(itemView);
    }

    @Override
    public void convert(VideoGalleryHolder holder, final String string) {
        holder.mImageView.setTag(string);
        holder.mImageView.setImageResource(R.color.black);
        ThumbnailLoader.create()
                .url(string)
                .size(320, 320)
                .into(holder.mImageView, new ThumbnailLoader.LoadListener() {
                    @Override
                    public void completed(String url, ImageView imageView, Bitmap bitmap) {
                        if (string.equals(imageView.getTag())) {
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                })
                .load();
    }

    class VideoGalleryHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;

        public VideoGalleryHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_icon);
        }
    }
}
