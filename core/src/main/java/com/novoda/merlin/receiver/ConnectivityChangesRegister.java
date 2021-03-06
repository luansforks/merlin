package com.novoda.merlin.receiver;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkRequest;
import android.os.Build;

import com.novoda.merlin.service.AndroidVersion;
import com.novoda.merlin.service.MerlinService;

public class ConnectivityChangesRegister {

    private final Context context;
    private final ConnectivityManager connectivityManager;
    private final AndroidVersion androidVersion;
    private final MerlinService merlinService;

    private ConnectivityReceiver connectivityReceiver;
    private ConnectivityCallbacks connectivityCallbacks;

    public ConnectivityChangesRegister(Context context,
                                       ConnectivityManager connectivityManager,
                                       AndroidVersion androidVersion,
                                       MerlinService merlinService) {
        this.context = context;
        this.connectivityManager = connectivityManager;
        this.androidVersion = androidVersion;
        this.merlinService = merlinService;
    }

    public void register() {
        if (androidVersion.isLollipopOrHigher()) {
            registerNetworkCallbacks();
        } else {
            registerBroadcastReceiver();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void registerNetworkCallbacks() {
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        connectivityManager.registerNetworkCallback(builder.build(), getConnectivityCallbacks());
    }

    private ConnectivityCallbacks getConnectivityCallbacks() {
        if (connectivityCallbacks == null) {
            connectivityCallbacks = new ConnectivityCallbacks(connectivityManager, merlinService);
        }
        return connectivityCallbacks;
    }

    private void registerBroadcastReceiver() {
        context.registerReceiver(getConnectivityReceiver(), getConnectivityActionIntentFilter());
    }

    private ConnectivityReceiver getConnectivityReceiver() {
        if (connectivityReceiver == null) {
            connectivityReceiver = new ConnectivityReceiver();
        }
        return connectivityReceiver;
    }

    private IntentFilter getConnectivityActionIntentFilter() {
        return new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    }

    public void unregister() {
        if (androidVersion.isLollipopOrHigher()) {
            unregisterNetworkCallbacks();
        } else {
            unregisterBroadcastReceiver();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void unregisterNetworkCallbacks() {
        connectivityManager.unregisterNetworkCallback(getConnectivityCallbacks());
    }

    private void unregisterBroadcastReceiver() {
        context.unregisterReceiver(getConnectivityReceiver());
    }

}
