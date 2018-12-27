/*
 * Copyright (C) 2017 Samsung Electronics Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.skku.sw3.skkucafeteria;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.util.Log;
import android.widget.Toast;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.TokenResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import cloud.artik.api.MessagesApi;
import cloud.artik.api.UsersApi;
import cloud.artik.client.ApiCallback;
import cloud.artik.client.ApiClient;
import cloud.artik.client.ApiException;
import cloud.artik.model.UserEnvelope;

import static edu.skku.sw3.skkucafeteria.AuthHelper.INTENT_ARTIKCLOUD_AUTHORIZATION_RESPONSE;
import static edu.skku.sw3.skkucafeteria.AuthHelper.USED_INTENT;
import static edu.skku.sw3.skkucafeteria.Config.DEVICE_ID_GIKSIK;
import static edu.skku.sw3.skkucafeteria.Config.DEVICE_ID_GONGSIK;
import static edu.skku.sw3.skkucafeteria.Config.DEVICE_ID_HAKSIK;

class LoginArtik {
    static final String TAG = "LoginActivity";
    private MainActivity mActivity;

    private AuthorizationService mAuthorizationService;
    private AuthStateDAL mAuthStateDAL;

    private UsersApi mUsersApi = null;

    private String mAccessToken;
    private String mUserId;

    private SoldoutManager mSoldoutManager;

    Menu mHaksikMenu, mGongsikMenu, mGiksikMenu;


    LoginArtik(MainActivity activity) {
        mActivity = activity;

        mAuthorizationService = new AuthorizationService(activity);
        mAuthStateDAL = new AuthStateDAL(activity);
    }

    // File OAuth call with Authorization Code with PKCE method
    // https://developer.artik.cloud/documentation/getting-started/authentication.html#authorization-code-with-pkce-method
     void doAuth(Menu haksikmenu, Menu gongsikmenu, Menu giksikmenu) {
            mHaksikMenu = haksikmenu;
            mGongsikMenu = gongsikmenu;
            mGiksikMenu = giksikmenu;
            AuthorizationRequest authorizationRequest = AuthHelper.createAuthorizationRequest();

            PendingIntent authorizationIntent = PendingIntent.getActivity(
                    mActivity,
                    authorizationRequest.hashCode(),
                    new Intent(INTENT_ARTIKCLOUD_AUTHORIZATION_RESPONSE, null, mActivity, MainActivity.class),
                    0);

        /* request sample with custom tabs */
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();

            mAuthorizationService.performAuthorizationRequest(authorizationRequest, authorizationIntent, customTabsIntent);
    }

    void startArtikConnection(int pos, Menu menu, Calendar date) {
        //Intent msgActivityIntent = new Intent(mActivity, MessageActivity.class);
        //mActivity.startActivity(msgActivityIntent);
        Log.v(TAG, "::onCreate get access token = " + mAccessToken);
        mSoldoutManager = new SoldoutManager(mAccessToken, mUserId, mActivity);

        if (pos == 0) {
            try {
                mSoldoutManager.connectHakSikWebSocket(DEVICE_ID_HAKSIK, menu, date);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        } else if (pos == 1) {
            try {
                mSoldoutManager.connectGikSikWebSocket(DEVICE_ID_GIKSIK, menu, date);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            try {
                mSoldoutManager.connectGongSikWebSocket(DEVICE_ID_GONGSIK, menu, date);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    void stopArtikConnection() {
        if (mSoldoutManager != null) {
            mSoldoutManager.stopWebSocket();
        }
    }




    void checkIntent(@Nullable Intent intent) {

        Log.d(TAG, "Entering checkIntent ...");
        if (intent != null) {
            String action = intent.getAction();
            switch (action) {
                case INTENT_ARTIKCLOUD_AUTHORIZATION_RESPONSE:
                    Log.d(TAG, "checkIntent action = " + action
                            + " intent.hasExtra(USED_INTENT) = " + intent.hasExtra(USED_INTENT));
                    if (!intent.hasExtra(USED_INTENT)) {
                        handleAuthorizationResponse(intent);
                        intent.putExtra(USED_INTENT, true);
                    }
                    break;
                default:
                    Log.w(TAG, "checkIntent action = " + action);
                    // do nothing
            }
        } else {
            Log.w(TAG, "checkIntent intent is null!");
        }
    }

    private void handleAuthorizationResponse(@NonNull Intent intent) {
        AuthorizationResponse response = AuthorizationResponse.fromIntent(intent);
        AuthorizationException error = AuthorizationException.fromIntent(intent);
        Log.i(TAG, "Entering handleAuthorizationResponse with response from Intent = " + response.jsonSerialize().toString());

        if (response != null) {

            if (response.authorizationCode != null ) { // Authorization Code method: succeeded to get code

                final Menu gongsik = mGongsikMenu;
                final Menu haksik = mHaksikMenu;
                final Menu giksik = mGiksikMenu;
                final AuthState authState = new AuthState(response, error);
                Log.i(TAG, "Received code = " + response.authorizationCode + "\n make another call to get token ...");

                // File 2nd call to get the token
                mAuthorizationService.performTokenRequest(response.createTokenExchangeRequest(), new AuthorizationService.TokenResponseCallback() {
                    @Override
                    public void onTokenRequestCompleted(@Nullable TokenResponse tokenResponse, @Nullable AuthorizationException exception) {
                        if (tokenResponse != null) {
                            authState.update(tokenResponse, exception);
                            mAuthStateDAL.writeAuthState(authState); //store into persistent storage for use later
                            String text = String.format("Received token response [%s]", tokenResponse.jsonSerializeString());
                            Log.i(TAG, text);

                            mAccessToken = mAuthStateDAL.readAuthState().getAccessToken();
                            setupArtikCloudApi();
                            getUserInfo();
                            /* connect~ */
                        } else {
                            Context context = mActivity.getApplicationContext();
                            Log.w(TAG, "Token Exchange failed", exception);
                            CharSequence text = "Token Exchange failed";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }
                });
            } else { // come here w/o authorization code. For example, signup finish and user clicks "Back to login"
                Log.d(TAG, "additionalParameter = " + response.additionalParameters.toString());

                if (response.additionalParameters.get("status").equalsIgnoreCase("login_request")) {
                    // ARTIK Cloud instructs the app to display a sign-in form
                    doAuth(mHaksikMenu, mGongsikMenu, mGiksikMenu);
                } else {
                    Log.d(TAG, response.jsonSerialize().toString());
                }
            }

        } else {
            Log.w(TAG, "Authorization Response is null ");
            Log.d(TAG, "Authorization Exception = " + error);
        }
    }

    private void setupArtikCloudApi() {
        ApiClient mApiClient = new ApiClient();
        mApiClient.setAccessToken(mAccessToken);

        mUsersApi = new UsersApi(mApiClient);
        MessagesApi mMessagesApi = new MessagesApi(mApiClient);
    }

    private void getUserInfo() {
        final String tag = TAG + " getSelfAsync";
        try {
            mUsersApi.getSelfAsync(new ApiCallback<UserEnvelope>() {
                @Override
                public void onFailure(ApiException exc, int statusCode, Map<String, List<String>> map) {
                    processFailure(tag, exc);
                }

                @Override
                public void onSuccess(UserEnvelope result, int statusCode, Map<String, List<String>> map) {
                    Log.v(TAG, "getSelfAsync::setupArtikCloudApi self name = " + result.getData().getFullName());
                    mUserId = result.getData().getId();
                }


                @Override
                public void onUploadProgress(long bytes, long contentLen, boolean done) {
                }

                @Override
                public void onDownloadProgress(long bytes, long contentLen, boolean done) {
                }
            });
        } catch (ApiException exc) {
            processFailure(tag, exc);
        }
    }

    static void showErrorOnUIThread(final String text, final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(activity.getApplicationContext(), text, duration);
                toast.show();
            }
        });
    }

    private void processFailure(final String context, ApiException exc) {
        String errorDetail = " onFailure with exception" + exc;
        Log.w(context, errorDetail);
        exc.printStackTrace();
        showErrorOnUIThread(context+errorDetail, mActivity);
    }
}
