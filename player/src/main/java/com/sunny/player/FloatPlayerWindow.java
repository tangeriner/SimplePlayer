package com.sunny.player;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * Created by Sunny on 2017/8/17.
 */

public class FloatPlayerWindow {
    public static final int TYPE_PLAY = 1;
    public static final int TYPE_CLOSE = 2;
    public static final int TYPE_WINDOW_SINGLE = 3;
    public static final int TYPE_WINDOW_DOUBLE = 4;
    private static final String TAG = "FloatPlayer";
    private Context mContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private RelativeLayout mFloatLayout;
    private TextureView mSurface;
    private ImageButton mIbPlay, mIbClose;

    private MediaPlayer mMediaPlay;
    private int mWidth, mHeight;
    private OnClickListener mOnClickListener;

    private AnimatorSet mAnimatorSet;


    private boolean mIsDestroy;

    public static Builder with(Context context) {
        return new Builder(context);
    }

    private FloatPlayerWindow(Context mContext, MediaPlayer mMediaPlay, int mWidth, int mHeight, OnClickListener
            mOnClickListener) {
        this.mContext = mContext;
        this.mMediaPlay = mMediaPlay;
        this.mWidth = mWidth;
        this.mHeight = mHeight;
        this.mOnClickListener = mOnClickListener;
    }

    public void init() {
        initView();
        initEventListener();
        mWindowManager.addView(mFloatLayout, mLayoutParams);
    }

    private void initView() {
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.gravity = Gravity.START | Gravity.TOP;
        mLayoutParams.x = 0;
        mLayoutParams.y = 0;
        mLayoutParams.width = mWidth;
        mLayoutParams.height = mHeight;

        //mContext为Application获取CompatModeWrapper对象 否则为LocalWindowManager
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mFloatLayout = (RelativeLayout) inflater.inflate(R.layout.layout_float_content, null);

        mSurface = (TextureView) mFloatLayout.findViewById(R.id.ttv_player);
        mIbPlay = (ImageButton) mFloatLayout.findViewById(R.id.ib_play);
        mIbClose = (ImageButton) mFloatLayout.findViewById(R.id.ib_close);
    }

    private void initEventListener() {
        mSurface.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Log.i(TAG, "onSurfaceTextureAvailable: size=" + width + "x" + height + " destroy=" + mIsDestroy);
                if (!mIsDestroy) {
                    mMediaPlay.setSurface(new Surface(surface));
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });
        // 设置悬浮窗的Touch监听
        mSurface.setOnTouchListener(new View.OnTouchListener() {
            int lastX, lastY;
            int paramX, paramY;

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        paramX = mLayoutParams.x;
                        paramY = mLayoutParams.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        mLayoutParams.x = paramX + dx;
                        mLayoutParams.y = paramY + dy;
                        // 更新悬浮窗位置
                        mWindowManager.updateViewLayout(mFloatLayout, mLayoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        int x = Math.abs(mLayoutParams.x - paramX);
                        int y = Math.abs(mLayoutParams.y - paramY);
                        return Math.max(Math.abs(x), Math.abs(y)) > 20;//监听移动距离 触发点击监听
                }
                return false;
            }
        });
        mSurface.setOnClickListener(new View.OnClickListener() {
            private long mClickCut;
            private Handler mHandler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (!mIsDestroy) {
                        if (mIbClose.getVisibility() == View.VISIBLE) {
                            hideButton();
                        } else {
                            showButton();
                        }
                        mOnClickListener.onClick(mSurface, TYPE_WINDOW_SINGLE);
                    }
                    super.handleMessage(msg);
                }
            };

            @Override
            public void onClick(final View v) {
                long systemCut = System.currentTimeMillis();
                if (systemCut - mClickCut > 300) {//单双击判断，emmmmmm... GestureDetector不知为啥监听不到滑动事件
                    Message m = new Message();
                    m.obj = v;
                    m.what = 0;
                    mHandler.sendEmptyMessageDelayed(0, 300);
                } else {
                    mHandler.removeMessages(0);
                    mOnClickListener.onClick(v, TYPE_WINDOW_DOUBLE);
                }
                mClickCut = systemCut;
            }
        });

        mIbClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destroy();
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(v, TYPE_CLOSE);
                }
            }
        });
        mIbPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton playButton = (ImageButton) v;
                if (mMediaPlay.isPlaying()) {
                    playButton.setImageResource(R.mipmap.play);
                    mMediaPlay.pause();
                } else {
                    playButton.setImageResource(R.mipmap.pause);
                    mMediaPlay.start();
                }
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(v, TYPE_PLAY);
                }
            }
        });
    }


    public void showButton() {
        mIbClose.setVisibility(View.VISIBLE);
        mIbPlay.setVisibility(View.VISIBLE);
        cancelControlAnimator();
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(
                ObjectAnimator.ofFloat(mIbClose, "alpha", 0, 1),
                ObjectAnimator.ofFloat(mIbClose, "translationY", -50, 0),
                ObjectAnimator.ofFloat(mIbPlay, "alpha", 0, 1),
                ObjectAnimator.ofFloat(mIbPlay, "translationY", 50, 0)
        );
        mAnimatorSet.setDuration(300).start();
    }

    public void hideButton() {
        cancelControlAnimator();
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(
                ObjectAnimator.ofFloat(mIbClose, "alpha", 1, 0),
                ObjectAnimator.ofFloat(mIbClose, "translationY", 0, -50),
                ObjectAnimator.ofFloat(mIbPlay, "alpha", 1, 0),
                ObjectAnimator.ofFloat(mIbPlay, "translationY", 0, 50)
        );
        mAnimatorSet.addListener(new CustomAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIbClose.setVisibility(View.INVISIBLE);
                mIbPlay.setVisibility(View.INVISIBLE);
            }
        });
        mAnimatorSet.setDuration(300).start();
    }

    private void cancelControlAnimator() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }
    }

    public void destroy() {
        mMediaPlay.setSurface(null);
        mFloatLayout.setVisibility(View.GONE);
        mWindowManager.removeView(mFloatLayout);
        mIsDestroy = true;
    }

    public static class Builder {
        private Context mContext;
        private MediaPlayer mPlayer;
        private int mWidth, mHeight;

        private OnClickListener mOnClickListener;

        private Builder(Context mContext) {
            this.mContext = mContext;
        }

        public Builder player(MediaPlayer mPlayer) {
            this.mPlayer = mPlayer;
            return this;
        }

        public Builder size(int mWidth, int mHeight) {
            this.mWidth = mWidth;
            this.mHeight = mHeight;
            return this;
        }

        public Builder listener(OnClickListener onClickListener) {
            this.mOnClickListener = onClickListener;
            return this;
        }

        public FloatPlayerWindow builder() {
            return new FloatPlayerWindow(mContext, mPlayer, mWidth, mHeight, mOnClickListener);
        }
    }

    interface OnClickListener {
        void onClick(View v, int type);
    }
}
