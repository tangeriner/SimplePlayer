package com.sunny.simple_video.net;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Sunny on 2017/8/8.
 */

public class ThumbnailLoader {
    private static Map<String, Bitmap> cache = new HashMap<>();
    private String url;
    private ImageView imageView;
    private int width = -1;
    private int height = -1;
    private LoadListener listener;

    public static ThumbnailLoader create() {
        return new ThumbnailLoader();
    }

    public ThumbnailLoader url(String url) {
        this.url = url;
        return this;
    }

    public ThumbnailLoader into(ImageView imageView) {
        this.imageView = imageView;
        return this;
    }

    public ThumbnailLoader into(ImageView imageView, LoadListener listener) {
        this.imageView = imageView;
        this.listener = listener;
        return this;
    }

    public ThumbnailLoader size(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public void load() {
        if (listener == null) {
            imageView.setTag(url);
        }
        Bitmap bitmap = cache.get(url);
        if (bitmap != null) {
            intoView(bitmap);
            return;
        }
        Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Bitmap> e) throws Exception {
                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(url, MediaStore.Images.Thumbnails.MINI_KIND);
                if (bitmap != null) {
                    e.onNext(bitmap);
                }
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(@NonNull Bitmap bitmap) throws Exception {
                        if (width == -1 && height == -1) {
                            width = imageView.getWidth();
                            height = imageView.getHeight();
                        }
                        if (width == 0 || height == 0) {
                            throw new RuntimeException("width or height != 0");
                        }
                        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                        intoView(bitmap);
                    }
                });
    }

    private void intoView(Bitmap bitmap) {
        cache.put(url, bitmap);
        if (listener != null) {
            listener.completed(url, imageView, bitmap);
        } else if (url.equals(imageView.getTag())) {
            imageView.setImageBitmap(bitmap);
        }
    }

    public interface LoadListener {
        void completed(String url, ImageView imageView, Bitmap bitmap);
    }
}
