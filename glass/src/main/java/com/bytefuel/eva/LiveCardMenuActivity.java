package com.bytefuel.eva;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.view.WindowUtils;

/**
 * A transparent {@link Activity} displaying a "Stop" options menu to remove the {@link LiveCard}.
 */
public class LiveCardMenuActivity extends Activity {
    private boolean mFromLiveCardVoice;
    private boolean mIsFinishing;
    @Override
    protected void onCreate(Bundle bundle) {

        super.onCreate(bundle);
        mFromLiveCardVoice =
                getIntent().getBooleanExtra(LiveCard.EXTRA_FROM_LIVECARD_VOICE, false);
        if (mFromLiveCardVoice) {
            // When activated by voice from a live card, enable voice commands. The menu
            // will automatically "jump" ahead to the items (skipping the guard phrase
            // that was already said at the live card).
            getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!mFromLiveCardVoice) {
            openOptionsMenu();
        }
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (isMyMenu(featureId)) {
            getMenuInflater().inflate(R.menu.live_card, menu);
            return true;
        }
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        if (isMyMenu(featureId)) {
            // Don't reopen menu once we are finishing. This is necessary
            // since voice menus reopen themselves while in focus.
            return !mIsFinishing;
        }
        return super.onPreparePanel(featureId, view, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.live_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_dismiss_alert:
                LiveCardService.dismissAlert();
                return true;
            case R.id.action_show_alerts:
                LiveCardService.showAlert();
                return true;
            case R.id.action_stop:
                // Stop the service which will unpublish the live card.
                stopService(new Intent(this, LiveCardService.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (isMyMenu(featureId)) {
            // Handle item selection.
            switch (item.getItemId()) {
                case R.id.action_dismiss_alert:
                    LiveCardService.dismissAlert();
                    return true;
                case R.id.action_show_alerts:
                    LiveCardService.showAlert();
                    return true;
                case R.id.action_stop:
                    stopService(new Intent(this, LiveCardService.class));
                    return true;
            }
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
        // Nothing else to do, finish the Activity.
        finish();
    }

    @Override
    public void onPanelClosed(int featureId, Menu menu) {
        super.onPanelClosed(featureId, menu);
        if (isMyMenu(featureId)) {
            // When the menu panel closes, either an item is selected from the menu or the
            // menu is dismissed by swiping down. Either way, we end the activity.
            mIsFinishing = true;
            finish();
        }
    }

    /**
     * Returns {@code true} when the {@code featureId} belongs to the options menu or voice
     * menu that are controlled by this menu activity.
     */
    private boolean isMyMenu(int featureId) {
        return featureId == Window.FEATURE_OPTIONS_PANEL ||
                featureId == WindowUtils.FEATURE_VOICE_COMMANDS;
    }
}
