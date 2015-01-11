package com.bytefuel.eva;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;
import com.google.android.glass.widget.CardBuilder;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.Random;

/**
 * A {@link Service} that publishes a {@link LiveCard} in the timeline.
 */
public class LiveCardService extends Service {

    private static final String LIVE_CARD_TAG = "LiveCardService";
    private static final int DELAY_MILLIS = 4500;
    private static final String[] ALERTS = {"Fog Alert !", "Accident ahead !", "Road closure !"};
    private static final String[] ALERT_FOOTNOTE = {"Fog 50 feet ahead.",
    "Accident at the next intersection", "Road closed 200 feet ahead."};
    private static final int[] ALERT_IMAGE = {R.drawable.road_side_glass,
    R.drawable.road_side_glass, R.drawable.road_side_glass};
    private int mAlertIndex = 0, mPreviousAlert = 0;

    private LiveCard mLiveCard;
    private final Handler mHandler = new Handler();
    private final SecondScreenRunnable mSecondScreenRunnable = new SecondScreenRunnable();
    private RemoteViews mLiveCardView;
    private static LiveCardService mStaticInstance;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mLiveCard == null) {
            mLiveCard = new LiveCard(this, LIVE_CARD_TAG);

            RemoteViews views = new RemoteViews(getPackageName(), R.layout.live_card);
            mLiveCard.setViews(views);

            // Display the options menu when the live card is tapped.
            Intent menuIntent = new Intent(this, LiveCardMenuActivity.class);
            mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));
            mLiveCard.setVoiceActionEnabled(true);
            mLiveCard.publish(PublishMode.REVEAL);
            mHandler.postDelayed(mSecondScreenRunnable, DELAY_MILLIS);
        } else {
            mLiveCard.navigate();
        }
        mStaticInstance = this;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mLiveCard != null && mLiveCard.isPublished()) {
            mLiveCard.unpublish();
            mLiveCard = null;
        }
        super.onDestroy();
    }

    private class SecondScreenRunnable implements Runnable {

        public void run() {
            int alertIndex = getAlertNumber();
            renderAlert(alertIndex);
        }

    }

    private int getAlertNumber() {
        mPreviousAlert = mAlertIndex;
        mAlertIndex = (mAlertIndex + 1) % ALERT_FOOTNOTE.length;
        return mPreviousAlert;
    }

    public static void showAlert() {
        mStaticInstance.renderAlert(mStaticInstance.getPreviousAlert());
    }

    public static void dismissAlert() {
        mStaticInstance.dismissAlertImpl();
    }

    public void dismissAlertImpl() {
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.live_card);
        views.setTextViewText(R.id.eva_textbox, "No Alerts");
        mLiveCard.setViews(views);
        mHandler.postDelayed(mSecondScreenRunnable, 2 * DELAY_MILLIS);
    }

    public int getPreviousAlert() {
        return mPreviousAlert;
    }

    public void renderAlert(int alertIndex) {
        mLiveCardView = new CardBuilder(LiveCardService.this, CardBuilder.Layout.CAPTION)
                .setText(ALERTS[alertIndex])
                .setFootnote(ALERT_FOOTNOTE[alertIndex])
                .setTimestamp("just now")
                .addImage(ALERT_IMAGE[alertIndex])
                .setIcon(R.drawable.eve)
                .setAttributionIcon(R.drawable.alert_icon)
                .getRemoteViews();
        mLiveCard.setViews(mLiveCardView);
    }
}
