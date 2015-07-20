
package com.polyvi.xface.extension.trafficstats;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.TrafficStats;

public class XTrafficStats {

    private static final int CONST_NUM = 1024;
    private Context mContext;

    private int mUID;

    private XWifiTraffic mWifiTraffic;

    private XMobileTraffic mMobileTraffic;
    private XNoNetworkTraffic mNoNetworkTraffic;
    private BroadcastReceiver mConnectionMonitor;
    XTrafficStatsBase mCurrentNetworkState;

    public XTrafficStats(Context context) {
        mContext = context;
        PackageManager pm = mContext.getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            mUID = info.uid;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        init();
    }

  
    private void init() {
        initTrafficStats();
        initConnectionMonitor();
    }

    public void destroy()
    {
        mContext.unregisterReceiver(mConnectionMonitor);
    }


    private void initConnectionMonitor() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        if (mConnectionMonitor == null) {
            mConnectionMonitor = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    stateTransition(context);
                }
            };
            mContext.registerReceiver(mConnectionMonitor, intentFilter);
        }
    }

    private void initTrafficStats() {
        mWifiTraffic = new XWifiTraffic(mUID);
        mMobileTraffic = new XMobileTraffic(mUID);
        mNoNetworkTraffic = new XNoNetworkTraffic(mUID);
        updateCurrentNetwork(mContext);
        long totalTraffic = TrafficStats.getUidRxBytes(mUID);
        mCurrentNetworkState.updateTotalTraffic(totalTraffic);
    }


    private void stateTransition(Context context) {
        XTrafficStatsBase previousState = updateCurrentNetwork(context);
        previousState.updateTraffic();
        mCurrentNetworkState.updateTotalTraffic(TrafficStats
                .getUidRxBytes(mUID));
    }

    private XTrafficStatsBase updateCurrentNetwork(Context context) {
        XTrafficStatsBase previousState = mCurrentNetworkState;
        State mobileState = null;
        State wifiState = null;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiNetworkInfo) {
            wifiState = wifiNetworkInfo.getState();
        }
        NetworkInfo mobileNetworkInfo = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (null != mobileNetworkInfo) {
            mobileState = mobileNetworkInfo.getState();
        }
        if (State.CONNECTED != wifiState && State.CONNECTED == mobileState) {
            mCurrentNetworkState = mMobileTraffic;
        } else if (State.CONNECTED != wifiState
                && State.CONNECTED != mobileState) {
            
            mCurrentNetworkState = mNoNetworkTraffic;
        } else if (State.CONNECTED == wifiState) {

            mCurrentNetworkState = mWifiTraffic;
        }
        return previousState;
    }

    public long getWifiTraffic() {
        mCurrentNetworkState.updateTraffic();
        return mWifiTraffic.getTraffic() / CONST_NUM;
    }

    public long getMobileTraffic() {
        mCurrentNetworkState.updateTraffic();
        return mMobileTraffic.getTraffic() / CONST_NUM;
    }


    class XWifiTraffic extends XTrafficStatsBase {
        public XWifiTraffic(int uid) {
            super(uid);
        }
    }

    class XMobileTraffic extends XTrafficStatsBase {
        public XMobileTraffic(int uid) {
            super(uid);
        }
    }

    class XNoNetworkTraffic extends XTrafficStatsBase {
        public XNoNetworkTraffic(int uid) {
            super(uid);
        }
    }

}
