package com.novoda.merlin.receiver;

import android.annotation.TargetApi;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

import com.novoda.merlin.service.MerlinService;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class ConnectivityCallbacks extends ConnectivityManager.NetworkCallback {

    private final ConnectivityManager connectivityManager;
    private final MerlinService merlinService;

    ConnectivityCallbacks(ConnectivityManager connectivityManager, MerlinService merlinService) {
        this.connectivityManager = connectivityManager;
        this.merlinService = merlinService;
    }

    @Override
    public void onAvailable(Network network) {
        notifyMerlinService(network);
    }

    @Override
    public void onLosing(Network network, int maxMsToLive) {
        notifyMerlinService(network);
    }

    @Override
    public void onLost(Network network) {
        notifyMerlinService(network);
    }

    private void notifyMerlinService(Network network) {
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
        if (null != networkInfo) {
            boolean connected = networkInfo.isConnected();
            String reason = networkInfo.getReason();
            String extraInfo = networkInfo.getExtraInfo();

            merlinService.onConnectivityChanged(ConnectivityChangeEvent.createWithNetworkInfoChangeEvent(connected, extraInfo, reason));
        } else {
            merlinService.onConnectivityChanged(ConnectivityChangeEvent.createWithoutConnection());
        }
    }

}
