package com.bytefuel.eva;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by arunesh on 1/10/15.
 */
public class StartupActivity extends Activity {
    private static final long ACTIVITY_DURATION_MS = 4500L;
    private static final int LAUNCH_MAIN_ACTIVITY = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup_layout);
        View mainView = findViewById(R.id.startup_view);
        hideSystemUI(mainView);
        mHandler.sendEmptyMessageDelayed(LAUNCH_MAIN_ACTIVITY, ACTIVITY_DURATION_MS);
    }


    // This snippet hides the system bars.
    private void hideSystemUI(View view) {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private Handler mHandler = new Handler () {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == LAUNCH_MAIN_ACTIVITY) {
                Intent goToMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                goToMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Will clear out your activity history stack till now
                startActivity(goToMainActivity);
            }
        }
    };
}
