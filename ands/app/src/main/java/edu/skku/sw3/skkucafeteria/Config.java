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

class Config {
    static final String CLIENT_ID = "21186f2febba44e1be807c499c4725d4";
    static final String DEVICE_ID_GIKSIK = "158c6851510745239a592dcb54efb5ef";
    static final String DEVICE_ID_GONGSIK = "3f3b44ac21ac495bb022079ced76b8c8";
    static final String DEVICE_ID_HAKSIK = "0d761d8e6db84201ad32e8b70a54a4f5";
    static final String ACTION_NAME_TURN_ON = "turnOn";
    static final String ACTION_NAME_TURN_OFF = "tunOff";

    // MUST be consistent with "AUTH REDIRECT URL" of your application
    // set up at the developer.artik.cloud
    static final String REDIRECT_URI = "cloud.artik.example.hellocloud://oauth2callback";

}
