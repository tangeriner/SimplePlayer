package com.sunny.player;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * Created by Sunny on 2017/8/21.
 */

public class AndroidMediaPlayer {
    private static final String TAG = "AndroidMediaPlayer";
    private static final int ERROR = 1;
    private static final int IDLE = 2;
    private static final int INITIALIZED = 4;
    private static final int PREPARED = 8;
    private static final int PREPARING = 16;
    private static final int STARTED = 32;
    private static final int STOPPED = 64;
    private static final int PAUSED = 128;
    private static final int COMPLETED = 256;
    private int mState = IDLE;

    private MediaPlayer mPlayer;
    private MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener;
    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private MediaPlayer.OnErrorListener mOnErrorListener;
    private MediaPlayer.OnInfoListener mOnInfoListener;
    private MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener;
    private MediaPlayer.OnCompletionListener mOnCompletionListener;

    public AndroidMediaPlayer() {
        mPlayer = new MediaPlayer();
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.i(TAG, "onPrepared");
                mState = PREPARED;
                if (mOnPreparedListener != null) {
                    mOnPreparedListener.onPrepared(mp);
                }
            }
        });
        mPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if (mOnBufferingUpdateListener != null) {
                    mOnBufferingUpdateListener.onBufferingUpdate(mp, percent);
                }
            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i(TAG, "onCompletion");
                mState = COMPLETED;
                if (mOnCompletionListener != null) {
                    mOnCompletionListener.onCompletion(mp);
                }
            }
        });
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.i(TAG, "onError what=" + what + " extra=" + extra);
                mState = ERROR;
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(mp, what, extra);
                }
                return false;
            }
        });
        mPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                Log.i(TAG, "onInfo what=" + what + " extra=" + extra);
                if (mOnInfoListener != null) {
                    mOnInfoListener.onInfo(mp, what, extra);
                }
                return false;
            }
        });
        mPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                Log.i(TAG, "onSeekComplete");
                if (mOnSeekCompleteListener != null) {
                    mOnSeekCompleteListener.onSeekComplete(mp);
                }
            }
        });
    }

    /**
     * Successful invoke of this method in a valid state transfers the object to the Prepared state.
     * Calling this method in an invalid state throws an IllegalStateException.
     */
    public void prepare() {
        Log.i(TAG, "prepare: state=" + mState);
        if (checkStates(INITIALIZED, STOPPED)) {
            try {
                mPlayer.prepare();
                mState = PREPARED;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.w(TAG, "prepare in a invalid state");
        }
    }

    /**
     * Successful invoke of this method in a valid state transfers the object to the Preparing state.
     * Calling this method in an invalid state throws an IllegalStateException.
     */
    public void prepareAsync() {
        Log.i(TAG, "prepareAsync: state=" + mState);
        if (checkStates(INITIALIZED, STOPPED)) {
            mState = PREPARING;
            mPlayer.prepareAsync();
        } else {
            Log.w(TAG, "prepareAsync in a invalid state");
        }
    }

    /**
     * Successful invoke of this method in a valid state transfers the object to the Started state.
     * Calling this method in an invalid state transfers the object to the Error state.
     */
    public void start() {
        Log.i(TAG, "start: state=" + mState);
        if (checkStates(PREPARED, STARTED, PAUSED, COMPLETED)) {
            mPlayer.start();
            mState = STARTED;
        } else {
            Log.w(TAG, "start in a invalid state");
        }
    }

    /**
     * Successful invoke of this method in a valid state transfers the object to the Stopped state.
     * Calling this method in an invalid state transfers the object to the Error state.
     */
    public void stop() {
        Log.i(TAG, "stop: state=" + mState);
        if (checkStates(PREPARED, STARTED, STOPPED, PAUSED, COMPLETED)) {
            mPlayer.stop();
            mState = STOPPED;
        } else {
            Log.w(TAG, "stop in a invalid state");
        }
    }

    /**
     * Successful invoke of this method in a valid state transfers the object to the Paused state.
     * Calling this method in an invalid state transfers the object to the Error state.
     */
    public void pause() {
        Log.i(TAG, "pause: state=" + mState);
        if (checkStates(STARTED, PAUSED, COMPLETED)) {
            mPlayer.pause();
            mState = PAUSED;
        } else {
            Log.w(TAG, "pause in a invalid state");
        }
    }

    /**
     * Successful invoke of this method in a valid state does not change the state.
     * Calling this method in an invalid state transfers the object to the Error state.
     *
     * @param msec
     */

    public void seekTo(int msec) {
        Log.i(TAG, "seekTo: state=" + mState);
        if (checkStates(PREPARED, STARTED, PAUSED, COMPLETED)) {
            mPlayer.seekTo(msec);
        } else {
            Log.w(TAG, "seekTo: in a invalid state");
        }
    }

    /**
     * Successful invoke of this method in a valid state does not change the state.
     * Calling this method in an invalid state transfers the object to the Error state.
     *
     * @return
     */
    public int getCurrentPosition() {
        if (checkStates(ERROR)) {
            Log.w(TAG, "getCurrentPosition: state=" + mState);
            return 0;
        }
        return mPlayer.getCurrentPosition();
    }

    /**
     * Successful invoke of this method in a valid state does not change the state.
     * Calling this method in an invalid state transfers the object to the Error state.
     *
     * @return
     */
    public int getDuration() {
        if (checkStates(PREPARED, STARTED, PAUSED, STOPPED, COMPLETED)) {
            return mPlayer.getDuration();
        }
        Log.w(TAG, "getDuration: state=" + mState);
        return 0;
    }

    /**
     * After release(), the object is no longer available.
     */
    public void release() {
        Log.i(TAG, "release");
        mPlayer.release();
    }

    /**
     * After reset(), the object is like being just created.
     */
    public void reset() {
        Log.i(TAG, "reset");
        mState = IDLE;
        mPlayer.reset();
    }

    /**
     * This method can be called in any state and calling it does not change the object state.
     *
     * @param surface
     */
    public void setSurface(Surface surface) {
        mPlayer.setSurface(surface);
    }

    /**
     * This method can be called in any state and calling it does not change the object state.
     *
     * @param sh
     */
    public void setDisplay(SurfaceHolder sh) {
        mPlayer.setDisplay(sh);
    }

    /**
     * Successful invoke of this method in a valid state does not change the state.
     * Calling this method in an invalid state transfers the object to the Error state.
     *
     * @return
     */
    public int getVideoWidth() {
        if (!checkStates(ERROR)) {
            return mPlayer.getVideoWidth();
        }
        Log.w(TAG, "getVideoWidth: state=" + mState);
        return 0;
    }

    /**
     * Successful invoke of this method in a valid state does not change the state.
     * Calling this method in an invalid state transfers the object to the Error state.
     *
     * @return
     */
    public int getVideoHeight() {
        if (!checkStates(ERROR)) {
            return mPlayer.getVideoHeight();
        }
        Log.w(TAG, "getVideoHeight: state=" + mState);
        return 0;
    }

    /**
     * Successful invoke of this method in a valid state transfers the object to the Initialized state.
     * Calling this method in an invalid state throws an IllegalStateException.
     *
     * @param path
     */
    public void setDataSource(String path) {
        Log.i(TAG, "setDataSource: state=" + mState);
        if (checkStates(IDLE)) {
            try {
                mPlayer.setDataSource(path);
                mState = INITIALIZED;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.w(TAG, "setDataSource: in a invalid state");
        }
    }

    /**
     * Successful invoke of this method in a valid state does not change the state.
     * Calling this method in an invalid state transfers the object to the Error state.
     *
     * @return is stated
     */
    public boolean isPlaying() {
        if (!checkStates(ERROR)) {
            return mPlayer.isPlaying();
        }
        Log.w(TAG, "isPlaying: int a invalid state");
        return false;
    }


    public void setOnPreparedListener(MediaPlayer.OnPreparedListener mOnPreparedListener) {
        this.mOnPreparedListener = mOnPreparedListener;
    }

    public void setOnBufferingUpdateListener(MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener) {
        this.mOnBufferingUpdateListener = mOnBufferingUpdateListener;
    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener mOnErrorListener) {
        this.mOnErrorListener = mOnErrorListener;
    }

    public void setOnInfoListener(MediaPlayer.OnInfoListener mOnInfoListener) {
        this.mOnInfoListener = mOnInfoListener;
    }

    public void setOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener) {
        this.mOnSeekCompleteListener = mOnSeekCompleteListener;
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener mOnCompletionListener) {
        this.mOnCompletionListener = mOnCompletionListener;
    }

    private boolean checkStates(long... states) {
        if (states == null) {
            return false;
        }
        for (long state : states) {
            if (mState == state) {
                return true;
            }
        }
        return false;
    }
}
