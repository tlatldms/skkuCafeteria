package edu.skku.sw3.skkucafeteria;

import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import cloud.artik.model.Acknowledgement;
import cloud.artik.model.ActionOut;
import cloud.artik.model.MessageOut;
import cloud.artik.model.WebSocketError;
import cloud.artik.websocket.ArtikCloudWebSocketCallback;
import cloud.artik.websocket.FirehoseWebSocket;
import okhttp3.OkHttpClient;

public class SoldoutManager {
    private static final String TAG = "SoldoutManager";

    private String mAccessToken;
    private String mUserId;

    private MainActivity mActivity;

    private FirehoseWebSocket mHaWS, mGiWS, mGoWS;

    public SoldoutManager(String accessToken,
                           String userId,
                           MainActivity activity) {
        mAccessToken = accessToken;
        mUserId = userId;
        mActivity = activity;
    }

    private void updateListenedResponseOnUIThreadH(final Menu menu, final int soldout, final Calendar date) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mActivity, "soldout value : " + soldout, Toast.LENGTH_LONG).show();
                mActivity.mHaksik_fragment.updateView(menu, date);
            }
        });
    }

    private void updateListenedResponseOnUIThreadGo(final Menu menu, final int soldout, final Calendar date) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mActivity, "soldout value : " + soldout, Toast.LENGTH_LONG).show();
                mActivity.mGongsik_fragment.updateView(menu, date);
            }
        });
    }

    private void updateListenedResponseOnUIThreadGi(final Menu menu, final int soldout, final Calendar date) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mActivity, "soldout value : " + soldout, Toast.LENGTH_LONG).show();
                mActivity.mGiksik_fragment.updateView(menu, date);
            }
        });
    }

    public void stopWebSocket() {
        if (mHaWS != null) {
            try {
                mHaWS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mHaWS = null;
        }
        if (mGiWS != null) {
            try {
                mGiWS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mGiWS = null;
        }
        if (mGoWS != null) {
            try {
                mGoWS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mGoWS = null;
        }
    }

    public void connectHakSikWebSocket(final String device_id,
                                       final Menu menu,
                                       final Calendar date)
            throws IOException, URISyntaxException {
        OkHttpClient client = new OkHttpClient();
        client.retryOnConnectionFailure();

        mHaWS = new FirehoseWebSocket(client, mAccessToken, device_id,
                null, null, mUserId, new ArtikCloudWebSocketCallback() {
            @Override
            public void onOpen(int httpStatus, String httpStatusMessage) {
                Log.d(TAG, "onOpen");
            }

            @Override
            public void onMessage(MessageOut message) {

                Menu haksik_menu = menu;
                ArrayList<MenuItem> breakfast = haksik_menu.getBreakfast();
                ArrayList<MenuItem> lunch = haksik_menu.getLunch();
                ArrayList<MenuItem> dinner = haksik_menu.getDinner();

                Log.d(TAG, "onMessage");
                Map<String, Object> data = message.getData();
                int soldout = -1;
                if (data.containsKey("soldout")) {
                    soldout = ((Double) data.get("soldout")).intValue();
                    if (soldout ==1) {
                        if ( breakfast.get(0) != null) {
                            breakfast.get(0).setAvailabilityFalse();
                        }
                    }
                    else if (soldout >= 2 && soldout <= 6) {
                        if (lunch.get(soldout-2) != null) {
                            lunch.get(soldout - 2).setAvailabilityFalse();
                            //str = deviceName + ": 품절 없음";
                        }
                    }
                    updateListenedResponseOnUIThreadH(menu, soldout, date);
                }
            }

            @Override
            public void onAction(ActionOut action) {
                Log.d(TAG, "onAction");
            }
            @Override
            public void onAck(Acknowledgement ack) {
                Log.d(TAG, "onAck");
            }
            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d(TAG, "onClose");
            }
            @Override
            public void onError(WebSocketError error) {
                Log.d(TAG, "onError : " + error.getMessage());
            }
            @Override
            public void onPing(long timestamp) {
                Log.d(TAG, "onPing");
            }
        });
        mHaWS.connect();
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mActivity, "Soldout started for " + device_id, Toast.LENGTH_LONG)
                        .show();
            }
        });
    }


    public void connectGikSikWebSocket(final String device_id,
                                       final Menu menu,
                                       final Calendar date)
            throws IOException, URISyntaxException {

        OkHttpClient client = new OkHttpClient();
        client.retryOnConnectionFailure();

        mGiWS = new FirehoseWebSocket(client, mAccessToken, device_id,
                null, null, mUserId, new ArtikCloudWebSocketCallback() {
            @Override
            public void onOpen(int httpStatus, String httpStatusMessage) {
                Log.d(TAG, "onOpen");
            }

            @Override
            public void onMessage(MessageOut message) {

                Menu giksik_menu = menu;
                ArrayList<MenuItem> breakfast = giksik_menu.getBreakfast();
                ArrayList<MenuItem> lunch = giksik_menu.getLunch();
                ArrayList<MenuItem> dinner = giksik_menu.getDinner();


                Log.d(TAG, "onMessage");
                Map<String, Object> data = message.getData();
                int soldout = -1;
                if (data.containsKey("soldout")) {
                    soldout = ((Double) data.get("soldout")).intValue();
                    if (soldout >= 1 && soldout <=3 ) {
                        if (breakfast.get(soldout - 1)!=null) {
                            breakfast.get(soldout - 1).setAvailabilityFalse();
                        }
                    }
                    else if (soldout >= 4 && soldout <=6 ) {
                        if (lunch.get(soldout - 4) != null) {
                            lunch.get(soldout - 4).setAvailabilityFalse();
                        }
                    }
                    else if (soldout >= 7 && soldout <= 10 ) {
                        if (dinner.get(soldout-7) != null) {
                            dinner.get(soldout-7).setAvailabilityFalse();
                        }
                    }
                }
                updateListenedResponseOnUIThreadGi(menu, soldout, date);
            }

            @Override
            public void onAction(ActionOut action) {
                Log.d(TAG, "onAction");
            }
            @Override
            public void onAck(Acknowledgement ack) {
                Log.d(TAG, "onAck");
            }
            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d(TAG, "onClose");
            }
            @Override
            public void onError(WebSocketError error) {
                Log.d(TAG, "onError : " + error.getMessage());
            }
            @Override
            public void onPing(long timestamp) {
                Log.d(TAG, "onPing");
            }
        });
        mGiWS.connect();
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mActivity, "Soldout started for " + device_id, Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    public void connectGongSikWebSocket(final String device_id,
                                       final Menu menu,
                                        final Calendar date)
            throws IOException, URISyntaxException {
        OkHttpClient client = new OkHttpClient();
        client.retryOnConnectionFailure();

        mGoWS = new FirehoseWebSocket(client, mAccessToken, device_id,
                null, null, mUserId, new ArtikCloudWebSocketCallback() {
            @Override
            public void onOpen(int httpStatus, String httpStatusMessage) {
                Log.d(TAG, "onOpen");
            }

            @Override
            public void onMessage(MessageOut message) {
                Menu gongsik_menu = menu;
                ArrayList<MenuItem> breakfast = gongsik_menu.getBreakfast();
                ArrayList<MenuItem> lunch = gongsik_menu.getLunch();
                ArrayList<MenuItem> dinner = gongsik_menu.getDinner();

                Log.d(TAG, "onMessage");
                Map<String, Object> data = message.getData();
                int soldout = -1;
                if (data.containsKey("soldout")) {
                    soldout = ((Double) data.get("soldout")).intValue();
                    if (soldout >= 1 && soldout <=3 ) {
                        if (breakfast.get(soldout - 1)!=null) {
                            breakfast.get(soldout - 1).setAvailabilityFalse();
                        }
                    }
                    else if (soldout >= 4 && soldout <= 8) {
                        if (lunch.get(soldout - 4) != null) {
                            lunch.get(soldout - 4).setAvailabilityFalse();
                        }
                    }
                    else if (soldout >= 9 && soldout <= 10 ) {
                        if (dinner.get(soldout-9) != null) {
                            dinner.get(soldout-9).setAvailabilityFalse();
                        }
                    }
                }
                updateListenedResponseOnUIThreadGo(menu, soldout, date);
            }

            @Override
            public void onAction(ActionOut action) {
                Log.d(TAG, "onAction");
            }
            @Override
            public void onAck(Acknowledgement ack) {
                Log.d(TAG, "onAck");
            }
            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d(TAG, "onClose");
            }
            @Override
            public void onError(WebSocketError error) {
                Log.d(TAG, "onError : " + error.getMessage());
            }
            @Override
            public void onPing(long timestamp) {
                Log.d(TAG, "onPing");
            }
        });
        mGoWS.connect();
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mActivity, "Soldout started for " + device_id, Toast.LENGTH_LONG)
                        .show();
            }
        });
    }
}
