package com.dieam.reactnativepushnotification.modules;

import android.os.Build;
import android.os.Bundle;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

/**
 * Created by lambert on 2016/10/09.
 */

public class RNPushNotificationJsDelivery {
    private static ReactContext mReactContext;

    public RNPushNotificationJsDelivery(ReactContext reactContext) {
        mReactContext = reactContext;
    }

    public static void callEventFromBridge(Bundle bundle){
        if (mReactContext != null) {
            notifyNotification(bundle);
        }
    }

    static void sendEvent(String eventName, Object params) {
        if (mReactContext.hasActiveCatalystInstance()) {
            mReactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, params);
        }
    }

    void notifyRemoteFetch(Bundle bundle) {
        String bundleString = convertJSON(bundle);
        WritableMap params = Arguments.createMap();
        params.putString("dataJSON", bundleString);
        sendEvent("remoteFetch", params);
    }

    static void notifyNotification(Bundle bundle) {
        String bundleString = convertJSON(bundle);

        WritableMap params = Arguments.createMap();
        params.putString("dataJSON", bundleString);

        sendEvent("remoteNotificationReceived", params);
    }

    void notifyNotificationAction(Bundle bundle) {
        String bundleString = convertJSON(bundle);

        WritableMap params = Arguments.createMap();
        params.putString("dataJSON", bundleString);

        sendEvent("notificationActionReceived", params);
    }

    static String convertJSON(Bundle bundle) {
        try {
            JSONObject json = convertJSONObject(bundle);
            return json.toString();
        } catch (JSONException e) {
            return null;
        }
    }
    
    // a Bundle is not a map, so we have to convert it explicitly
    static private JSONObject convertJSONObject(Bundle bundle) throws JSONException {
        JSONObject json = new JSONObject();
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            Object value = bundle.get(key);
            if (value instanceof Bundle) {
                json.put(key, convertJSONObject((Bundle)value));
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                json.put(key, JSONObject.wrap(value));
            } else {
                json.put(key, value);
            }
        }
        return json;
    }

}
