// Copyright (c) Microsoft Corporation.
// All rights reserved.
//
// This code is licensed under the MIT License.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files(the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions :
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package com.microsoft.aad.adal;

import android.content.Context;
import android.content.pm.PackageManager;
//import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


class DefaultEvent implements IEvents {
    private final List<Pair<String, String>> mEventList;

    private static String sApplicationName = null;

    private static String sApplicationVersion = null;

    private static String sClientId = null;

    private static String sClientIp = null;

    private static String sDeviceId = null;

    private String mRequestId;

    private int mDefaultEventCount;

    DefaultEvent() {
        mEventList = new ArrayList<>();

        // Keying off Application name not being null to decide if the defaults have been set
        if (sApplicationName != null) {
            mEventList.add(new Pair<>(EventStrings.APPLICATION_NAME, sApplicationName));
            mEventList.add(new Pair<>(EventStrings.APPLICATION_VERSION, sApplicationVersion));
            mEventList.add(new Pair<>(EventStrings.CLIENT_ID, sClientId));
            mEventList.add(new Pair<>(EventStrings.CLIENT_IP, sClientIp));
            mEventList.add(new Pair<>(EventStrings.DEVICE_ID, sDeviceId));
            mDefaultEventCount = mEventList.size();
        }
    }

    @Override
    public int getDefaultEventCount() {
        return mDefaultEventCount;
    }

    @Override
    public void setEvent(final String name, final String value) {
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Telemetry setEvent on null name");
        }
        mEventList.add(Pair.create(name, value));
    }

    @Override
    public List<Pair<String, String>> getEvents() {
        return Collections.unmodifiableList(mEventList);
    }

    void setDefaults(final Context context, final String clientId) {

        sClientId = clientId;
        sApplicationName = context.getPackageName();
        try {
            sApplicationVersion = context.getPackageManager().getPackageInfo(sApplicationName, 0).versionName;
        } catch (PackageManager.NameNotFoundException nnfe) {
            sApplicationVersion = "NA";
        }

        //TODO: Getting IP will require network permissions do we want to do it?
        sClientIp = "NA";
        //sDeviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

        if (mDefaultEventCount == 0) {
            mEventList.add(new Pair<>(EventStrings.APPLICATION_NAME, sApplicationName));
            mEventList.add(new Pair<>(EventStrings.APPLICATION_VERSION, sApplicationVersion));
            mEventList.add(new Pair<>(EventStrings.CLIENT_ID, sClientId));
            mEventList.add(new Pair<>(EventStrings.CLIENT_IP, sClientIp));
            mEventList.add(new Pair<>(EventStrings.DEVICE_ID, sDeviceId));
            mDefaultEventCount = mEventList.size();
        }
    }

    // Sets the correlation id to the top of the list
    void setCorrelationId(final String correlationId) {
        mEventList.add(0, new Pair<>(EventStrings.CORRELATION_ID, correlationId));
        mDefaultEventCount++;
    }

    void setRequestId(final String requestId) {
        mRequestId = requestId;
        mEventList.add(0, new Pair<>(EventStrings.REQUEST_ID, requestId));
        mDefaultEventCount++;
    }

    List<Pair<String, String>> getEventList() {
        return mEventList;
    }

    String getRequestId() {
        return mRequestId;
    }
}