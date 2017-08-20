package com.sunny.simple_video;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.sunny.simple_video.logger.Logger;

/**
 * Created by Sunny on 2017/8/6.
 */

public class BaseActivity extends AppCompatActivity {
    protected final String TAG = this.getClass().getSimpleName();
    protected BaseActivity mMyself;

    protected boolean mIsDestroy;

    protected ProgressDialog mProgressDialog;

    public BaseActivity() {
        this.mMyself = this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i(TAG, "onCreate");
        mProgressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.i(TAG, "onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.i(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.i(TAG, "onDestroy");
        mIsDestroy = true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Logger.i(TAG, "onNewIntent");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Logger.i(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Logger.i(TAG, "onRestoreInstanceState");
    }
}
