package com.sunny.player;

import android.media.MediaPlayer;
import android.view.Surface;
import android.view.SurfaceHolder;

/**
 * Created by Sunny on 2017/8/21.
 */

public class SPlayer implements Player {
    private AndroidMediaPlayer mPlayer;

    public SPlayer() {
        mPlayer = new AndroidMediaPlayer();
    }

    @Override
    public void prepare() {
        mPlayer.prepare();
    }

    @Override
    public void prepareAsync() {
        mPlayer.prepareAsync();
    }

    @Override
    public void start() {
        mPlayer.start();
    }

    @Override
    public void stop() {
        mPlayer.stop();
    }

    @Override
    public void pause() {
        mPlayer.pause();
    }

    @Override
    public void seekTo(int msec) {
        mPlayer.seekTo(msec);
    }

    @Override
    public int getCurrentPosition() {
        return mPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return mPlayer.getDuration();
    }

    @Override
    public void release() {
        mPlayer.release();
    }

    @Override
    public void reset() {
        mPlayer.reset();
    }

    @Override
    public void setSurface(Surface surface) {
        mPlayer.setSurface(surface);
    }

    @Override
    public void setDisplay(SurfaceHolder sh) {
        mPlayer.setDisplay(sh);
    }

    @Override
    public int getVideoWidth() {
        return mPlayer.getVideoWidth();
    }

    @Override
    public int getVideoHeight() {
        return mPlayer.getVideoHeight();
    }

    @Override
    public void setDataSource(String path) {
        mPlayer.setDataSource(path);
    }

    @Override
    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    @Override
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener onPreparedListener) {
        mPlayer.setOnPreparedListener(onPreparedListener);
    }

    @Override
    public void setOnBufferingUpdateListener(MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener) {
        mPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
    }

    @Override
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener) {
        mPlayer.setOnCompletionListener(onCompletionListener);
    }

    @Override
    public void setOnErrorListener(MediaPlayer.OnErrorListener onErrorListener) {
        mPlayer.setOnErrorListener(onErrorListener);
    }

    @Override
    public void setOnInfoListener(MediaPlayer.OnInfoListener onInfoListener) {
        mPlayer.setOnInfoListener(onInfoListener);
    }

    @Override
    public void setOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener onSeekCompleteListener) {
        mPlayer.setOnSeekCompleteListener(onSeekCompleteListener);
    }
}
