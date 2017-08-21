package com.sunny.player;

import android.media.MediaPlayer;
import android.view.Surface;
import android.view.SurfaceHolder;

/**
 * Created by Sunny on 2017/8/21.
 */

public interface Player {
    void prepare();

    void prepareAsync();

    void start();

    void stop();

    void pause();

    void seekTo(int msec);

    int getCurrentPosition();

    int getDuration();

    void release();

    void reset();

    void setSurface(Surface surface);

    void setDisplay(SurfaceHolder sh);

    int getVideoWidth();

    int getVideoHeight();

    void setDataSource(String path);

    boolean isPlaying();

    void setOnPreparedListener(MediaPlayer.OnPreparedListener onPreparedListener);

    void setOnBufferingUpdateListener(MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener);

    void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener);

    void setOnErrorListener(MediaPlayer.OnErrorListener onErrorListener);

    void setOnInfoListener(MediaPlayer.OnInfoListener onInfoListener);

    void setOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener onSeekCompleteListener);
}
