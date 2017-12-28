package com.segway.loomo.vlssample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.segway.robot.algo.Pose2D;
import com.segway.robot.sdk.base.bind.ServiceBinder;
import com.segway.robot.sdk.locomotion.sbv.Base;
import com.segway.robot.sdk.locomotion.sbv.StartVLSListener;

/**
 * @author jacob
 * This sample shows developers how to use visual localization system for navigation
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Loomo";
    private Button mStart;
    private Button mStop;
    private Base mBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStart = (Button) findViewById(R.id.start);
        mStop = (Button) findViewById(R.id.stop);
        mStart.setOnClickListener(this);
        mStop.setOnClickListener(this);

        mBase = Base.getInstance();
        mBase.bindService(this, mBaseBindStateListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                startNavigation();
                break;
            case R.id.stop:
                mBase.clearCheckPointsAndStop();
                break;
        }
    }

    private void startNavigation() {
        // set navigation data source
        mBase.setNavigationDataSource(Base.NAVIGATION_SOURCE_TYPE_VLS);
        mBase.cleanOriginalPoint();
        Pose2D pose2D = mBase.getOdometryPose(-1);
        mBase.setOriginalPoint(pose2D);
        mBase.addCheckPoint(1f, 0, (float) (Math.PI / 2));
        mBase.addCheckPoint(1f, 1f, (float) (Math.PI));
        mBase.addCheckPoint(0f, 1f, (float) (-Math.PI / 2));
        mBase.addCheckPoint(0, 0, 0);
    }


    private StartVLSListener mStartVLSListener = new StartVLSListener() {
        @Override
        public void onOpened() {
            Log.d(TAG, "onOpened() called");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mStart.setEnabled(true);
                    mStop.setEnabled(true);
                }
            });
        }

        @Override
        public void onError(String errorMessage) {
            Log.d(TAG, "onError() called with: errorMessage = [" + errorMessage + "]");
        }
    };

    private ServiceBinder.BindStateListener mBaseBindStateListener = new ServiceBinder.BindStateListener() {
        @Override
        public void onBind() {
            Log.d(TAG, "onBind() called");
            // set base control mode
            mBase.setControlMode(Base.CONTROL_MODE_NAVIGATION);
            // start VLS
            mBase.startVLS(true, true, mStartVLSListener);

        }

        @Override
        public void onUnbind(String reason) {
            Log.d(TAG, "onUnbind() called with: reason = [" + reason + "]");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "failed when binding service!", Toast.LENGTH_LONG).show();
                }
            });

        }
    };
}
