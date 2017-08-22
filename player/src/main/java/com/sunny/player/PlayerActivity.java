package com.sunny.player;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sunny.player.utils.ScreenRotateUtil;
import com.sunny.player.utils.StatusBarUtil;
import com.sunny.player.utils.StringUtil;
import com.sunny.player.utils.TimeUtil;

import java.io.File;

import static com.sunny.player.FloatPlayerWindow.TYPE_WINDOW_DOUBLE;

public class PlayerActivity extends Activity {
    private static final String TAG = "PlayerActivity";
    private static final int SEEKBAR_MAX = Integer.MAX_VALUE;
    private String mURL;
    private Player mPlayer;

    private TextureView mSurfaceView;
    private SeekBar mSeekBar;
    private TextView mTvStartTime, mTvEndTime, mTvName, mTvGestureDisplay;
    private ImageButton mIbRotate, mIbPlay;
    private RelativeLayout mLayoutControl, mLayoutControlTop;
    private LinearLayout mLayoutControlBottom;
    private ViewGroup mViewRoot;
    private View mControlView;
    private ProgressDialog mProgressDialog;

    private FloatPlayerWindow mFloatPlayerWindow;

    private AnimatorSet mControlAnimatorSet;

    private ContentObserver mRotateObserver;

    private int mUpdateTimes = 0;

    private boolean isOpenFloatPlayer;

    private boolean mIsFirstFitVideo = true;

    private boolean mUpdateAlive = false;

    private Handler mUpdateHandler = new UpdateHandler();

    @SuppressLint("HandlerLeak")
    private class UpdateHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            mUpdateTimes++;
            if (mUpdateTimes == 8) {
                mUpdateTimes = 0;
                hideControlContent();
                if (mFloatPlayerWindow != null) {
                    mFloatPlayerWindow.hideButton();//利用计时隐藏mini窗口UI
                }
            }
            if (mSeekBar != null && mPlayer != null) {
                if (mPlayer.getDuration() != 0) {
                    float p = mPlayer.getCurrentPosition() * 1.0f / mPlayer.getDuration();
                    int progress = (int) (p * SEEKBAR_MAX);
                    if (progress != mSeekBar.getProgress() && !mSeekBar.isPressed()) {
                        mSeekBar.setProgress(progress);
                    }
                    mTvStartTime.setText(TimeUtil.convert(mPlayer.getCurrentPosition()));
                }
            }
            mUpdateHandler.sendEmptyMessageDelayed(0, 500);
            super.handleMessage(msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Intent intent = getIntent();
        mURL = intent.getDataString();
//        mURL = "http://192.168.0.10:8080/video/1.mp4";//test
        initPlayer();
        initView();
        initRotateListener();
    }

    /**
     * 屏幕旋转开关监听
     */
    private void initRotateListener() {
        mRotateObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                refreshRotate();
                super.onChange(selfChange);
            }
        };
        getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), false, mRotateObserver);
    }

    private void initPlayer() {
        mPlayer = new SPlayer();
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.i(TAG, "onPrepared");
                fitVideo();
                mProgressDialog.dismiss();
                playVideo();
            }
        });
        mPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                float p = percent * 1.0f / 100;
                mSeekBar.setSecondaryProgress((int) (p * SEEKBAR_MAX));
                Log.i(TAG, "onBufferingUpdate percent=" + percent);
            }
        });
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.i(TAG, "onError what=" + what + " extra=" + extra);
                mPlayer.reset();
                return false;
            }
        });
        mPlayer.setDataSource(mURL);
        mPlayer.prepareAsync();
    }

    private void initView() {
        mViewRoot = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);//获取ViewRoot方式之一
        mViewRoot.setOnClickListener(new View.OnClickListener() {
            private long mClickCut;
            private Handler mHandler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (mControlView.getVisibility() == View.VISIBLE) {
                        hideControlContent();
                    } else {
                        showControlContent();
                    }
                    super.handleMessage(msg);
                }
            };

            @Override
            public void onClick(final View v) {
                long systemCut = System.currentTimeMillis();
                if (systemCut - mClickCut > 300) {
                    Message m = new Message();
                    m.obj = v;
                    m.what = 0;
                    mHandler.sendEmptyMessageDelayed(0, 300);
                } else {
                    mHandler.removeMessages(0);
                    togglePlayOrPause();
                }
                mClickCut = systemCut;
            }
        });
        mTvGestureDisplay = (TextView) findViewById(R.id.tv_gesture_display);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.show();
        initSurface();
        initControlContent();
    }

    private void initSurface() {
        mSurfaceView = (TextureView) findViewById(R.id.ttv_player);
        mSurfaceView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, final int height) {
                Log.i(TAG, "onSurfaceTextureAvailable: wh=" + width + "x" + height);
                mPlayer.setSurface(new Surface(surface));
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                Log.i(TAG, "onSurfaceTextureSizeChanged: width=" + width + " height=" + height);
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                Log.i(TAG, "onSurfaceTextureDestroyed");
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });
    }

    private void initControlContent() {
       /*初始化动态布局块*/
        if (mControlView != null) {
            mViewRoot.removeView(mControlView);
        }
        /*bool参数attachToRoot 建议false 然后手动添加到view root 使用true自动添加后暂时没找到移除方法*/
        mControlView = LayoutInflater.from(this).inflate(R.layout.layout_player_control, mViewRoot, false);
        mViewRoot.addView(mControlView);

        mTvStartTime = (TextView) findViewById(R.id.tv_start_time);
        mTvEndTime = (TextView) findViewById(R.id.tv_end_time);
        mLayoutControl = (RelativeLayout) findViewById(R.id.rl_control_content);
        mLayoutControlTop = (RelativeLayout) findViewById(R.id.rl_control_top);
        mLayoutControlBottom = (LinearLayout) findViewById(R.id.rl_control_bottom);
        mIbPlay = (ImageButton) findViewById(R.id.ib_play);
        mIbRotate = (ImageButton) findViewById(R.id.ib_rotate);

        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvName.setText(new File(mURL).getName());

        refreshRotate();
        initSeekBar();
    }

    private void initSeekBar() {
        if (mSeekBar != null) {
            mSeekBar.setOnSeekBarChangeListener(null);//移除上一个进度条的监听
        }
        mSeekBar = (SeekBar) findViewById(R.id.sb_play);
        mSeekBar.setMax(SEEKBAR_MAX);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float percent = seekBar.getProgress() * 1.0f / SEEKBAR_MAX;
                int position = (int) (mPlayer.getDuration() * percent);
                mPlayer.seekTo(position);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i(TAG, "onStartTrackingTouch: ");
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }


        });
    }

    private void refreshPlayStatusUI() {
        mIbPlay.setImageResource(mPlayer.isPlaying() ? R.mipmap.pause : R.mipmap.play);
        mTvStartTime.setText(TimeUtil.convert(mPlayer.getCurrentPosition()));
        mTvEndTime.setText(TimeUtil.convert(mPlayer.getDuration()));
    }

    /**
     * 刷新屏幕旋转按钮
     */
    private void refreshRotate() {
        if (ScreenRotateUtil.rotateIsOn(this)) {
            ScreenRotateUtil.setUnspecified(this);//解决手动切换横竖屏后，自动切换失效问题
        }
        mIbRotate.setEnabled(!ScreenRotateUtil.rotateIsOn(this));
    }

    /**
     * surface适配屏幕
     */
    private void fitVideo() {
        int videoW = mPlayer.getVideoWidth();
        int videoH = mPlayer.getVideoHeight();
        if (videoH == 0 || videoW == 0) {
            return;
        }
        double videoRatio = videoW * 1.0 / videoH;
        /*初始化为视频适配合适的场景*/
        if (mIsFirstFitVideo) {
            mIsFirstFitVideo = false;
            boolean isLandscape = ScreenRotateUtil.isLandscape(this);
            if (!ScreenRotateUtil.rotateIsOn(this)) {
                if (videoRatio > 1) {
                    if (!isLandscape) {
                        ScreenRotateUtil.setLandscape(this);
                        return;
                    }
                } else if (isLandscape) {
                    ScreenRotateUtil.setPortrait(this);
                    return;
                }
            }
        }
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        int screenH = dm.heightPixels;
        Log.i(TAG, "fitVideo: screen=" + screenW + "x" + screenH + " Video=" + videoW + "x" + videoH);
        double screenRatio = screenW * 1.0 / screenH;
        if (videoRatio / screenRatio > 1) {
            videoW = screenW;
            videoH = (int) (screenW / videoRatio);
        } else {
            videoH = screenH;
            videoW = (int) (screenH * videoRatio);
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(videoW, videoH);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mSurfaceView.setLayoutParams(layoutParams);
    }

    private void initMiniPlayer() {
        int w, h;
        float ratio = mPlayer.getVideoWidth() * 1.0f / mPlayer.getVideoHeight();
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        final int temp = Math.min(dm.widthPixels, dm.heightPixels);
        if (ratio > 1) {
            w = (int) (temp * 0.65);
            h = (int) (w / ratio);
        } else {
            w = (int) (temp * 0.35);
            h = (int) (w / ratio);
        }
        mPlayer.setSurface(null);
        mFloatPlayerWindow = FloatPlayerWindow.with(this)
                .player(mPlayer)
                .size(w, h)
                .listener(new FloatPlayerWindow.OnClickListener() {
                    @Override
                    public void onClick(View v, int type) {
                        Log.i(TAG, "mini player onClick type=" + type);
                        mUpdateTimes = 0;
                        switch (type) {
                            case FloatPlayerWindow.TYPE_CLOSE:
                                PlayerActivity.this.finish();
                                break;
                            case TYPE_WINDOW_DOUBLE:
                                mFloatPlayerWindow.destroy();
                                mFloatPlayerWindow = null;
                                ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE))
                                        .moveTaskToFront(getTaskId(), 0);
                                refreshPlayStatusUI();
                                break;
                        }
                    }
                })
                .builder();
        mFloatPlayerWindow.init();
        isOpenFloatPlayer = true;
    }

    private void togglePlayOrPause() {
        if (mPlayer.isPlaying()) {
            pauseVideo();
        } else {
            playVideo();
        }
    }

    private void playVideo() {
        mPlayer.start();
        mIbPlay.setImageResource(R.mipmap.pause);
    }

    private void pauseVideo() {
        mPlayer.pause();
        mIbPlay.setImageResource(R.mipmap.play);
    }

    /**
     * 事件接收
     *
     * @param view ,
     */
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ib_back) {
            releasePlayer();
            PlayerActivity.this.finish();
        } else if (id == R.id.ib_play) {
            togglePlayOrPause();
        } else if (id == R.id.ib_rotate) {
            ScreenRotateUtil.toggle(this);
        } else if (id == R.id.ib_mini_play) {
            initMiniPlayer();
            moveTaskToBack(true);
        }
    }

    private float[] mMovePosition = new float[2];
    private float[] mLastMovePosition = new float[2];
    private int mEventType = -1;
    private boolean mIsSeekOutValue;
    private static final int SEEK = 1;
    private static final int VOLUME = 2;
    private static final int BRIGHTNESS = 3;
    private int mCurrentPosition = 0;
    private static final int PIXEL_VALUE = 100;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mMovePosition[0] = x;
                mMovePosition[1] = y;
                mEventType = -1;
                break;
            case MotionEvent.ACTION_MOVE:
                float absX = Math.abs(x - mMovePosition[0]);
                float absY = Math.abs(y - mMovePosition[1]);
                if (mEventType == -1 && absX > 5 && absY > 5) {
                    mTvGestureDisplay.setVisibility(View.VISIBLE);
                    if (absX >= absY) {//进度手势
                        mEventType = SEEK;
                        mCurrentPosition = mPlayer.getCurrentPosition();
                    } else {
                        DisplayMetrics dm = new DisplayMetrics();
                        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
                        if (x < dm.widthPixels / 2) {//音量手势
                            mEventType = VOLUME;//TODO
                        } else {//亮度手势
                            mEventType = BRIGHTNESS;//TODO
                        }
                    }
                }
                if (mEventType == SEEK) {
                    if (mIsSeekOutValue) {
                        mMovePosition[0] = mLastMovePosition[0];
                        mMovePosition[1] = mLastMovePosition[1];
                    }
                    int diff = (int) (x - mMovePosition[0]) * PIXEL_VALUE;
                    int value = mCurrentPosition + diff;
                    mIsSeekOutValue = true;
                    if (value < 0) {//移动到返回外立即重置初始位置
                        mCurrentPosition = 0;
                    } else if (value > mPlayer.getDuration()) {//移动到返回外立即重置初始位置
                        mCurrentPosition = mPlayer.getDuration();
                    } else {
                        mIsSeekOutValue = false;
                        String afterMove = TimeUtil.convert(value);
                        String sDiff = (diff < 0 ? "- " : "+ ") + TimeUtil.convert(Math.abs(diff));
                        String text = StringUtil.stick("move to ", afterMove, " / ", sDiff);
                        mTvGestureDisplay.setText(text);
                    }
                } else if (mEventType == VOLUME) {
                    //TODO
                } else if (mEventType == BRIGHTNESS) {
                    //TODO
                }
                mLastMovePosition[0] = x;
                mLastMovePosition[1] = y;
                break;
            case MotionEvent.ACTION_UP:
                float diffX = x - mMovePosition[0];
                float diffY = y - mMovePosition[1];
                mTvGestureDisplay.setVisibility(View.INVISIBLE);
                if (mEventType == SEEK) {
                    int msec = mCurrentPosition + (int) diffX * PIXEL_VALUE;
                    Log.i(TAG, "onTouchEvent: c=" + msec + " d=" + mPlayer.getDuration());
                    mPlayer.seekTo(msec);
                } else if (mEventType == VOLUME) {
                } else if (mEventType == BRIGHTNESS) {
                } else {
                    return false;
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void releasePlayer() {
        mPlayer.release();
    }

    /**
     * 更新任务开关
     *
     * @param start true 开启 false 关闭
     */
    private void setUpdateHandlerAlive(boolean start) {
        if (start && !mUpdateAlive) {
            mUpdateHandler.sendEmptyMessageDelayed(0, 500);
        }
        if (!start) {
            mUpdateHandler.removeMessages(0);
        }
        mUpdateAlive = start;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mUpdateTimes = 0;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.i(TAG, "onConfigurationChanged");
        fitVideo();
        initControlContent();
        refreshPlayStatusUI();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        setUpdateHandlerAlive(true);
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        if (!isOpenFloatPlayer) {
            mPlayer.setSurface(null);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        getContentResolver().unregisterContentObserver(mRotateObserver);
        releasePlayer();
        setUpdateHandlerAlive(false);
        super.onDestroy();
    }

    private void showControlContent() {
        mLayoutControl.setVisibility(View.VISIBLE);
        StatusBarUtil.showStatusBar(getWindow());
        cancelControlAnimator();
        mControlAnimatorSet = new AnimatorSet();
        mControlAnimatorSet.playTogether(
                ObjectAnimator.ofFloat(mLayoutControlTop, "alpha", 0, 1),
                ObjectAnimator.ofFloat(mLayoutControlTop, "translationY", -50, 0),
                ObjectAnimator.ofFloat(mLayoutControlBottom, "alpha", 0, 1),
                ObjectAnimator.ofFloat(mLayoutControlBottom, "translationY", 50, 0)
        );
        mControlAnimatorSet.setDuration(300).start();
    }

    private void hideControlContent() {
        cancelControlAnimator();
        mControlAnimatorSet = new AnimatorSet();
        mControlAnimatorSet.playTogether(
                ObjectAnimator.ofFloat(mLayoutControlTop, "alpha", 1, 0),
                ObjectAnimator.ofFloat(mLayoutControlTop, "translationY", 0, -50),
                ObjectAnimator.ofFloat(mLayoutControlBottom, "alpha", 1, 0),
                ObjectAnimator.ofFloat(mLayoutControlBottom, "translationY", 0, 50)
        );
        mControlAnimatorSet.addListener(new CustomAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLayoutControl.setVisibility(View.INVISIBLE);
                StatusBarUtil.hideStatusBar(getWindow());
            }
        });
        mControlAnimatorSet.setDuration(300).start();
    }

    private void cancelControlAnimator() {
        if (mControlAnimatorSet != null) {
            mControlAnimatorSet.cancel();
        }
    }

}
