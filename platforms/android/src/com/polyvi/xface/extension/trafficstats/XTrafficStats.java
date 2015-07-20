
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

    private static final int CONST_NUM = 1024*1024;
    private Context mContext;
    private XMobileTraffic mMobileTraffic;
    private BroadcastReceiver mConnectionMonitor;
    XTrafficStatsBase mCurrentNetworkState;

    public XTrafficStats(Context context) {
        mContext = context;
        PackageManager pm = mContext.getPackageManager();
        try {
            @SuppressWarnings("unused")
			ApplicationInfo info = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
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
        mMobileTraffic = new XMobileTraffic();
        updateCurrentNetwork(mContext);
        long totalTraffic = TrafficStats.getMobileTxBytes()+TrafficStats.getMobileRxBytes();
        mCurrentNetworkState.updateTotalTraffic(totalTraffic);
    }


    private void stateTransition(Context context) {
        XTrafficStatsBase previousState = updateCurrentNetwork(context);
        previousState.updateTraffic();
        mCurrentNetworkState.updateTotalTraffic(TrafficStats.getMobileTxBytes()+TrafficStats.getMobileRxBytes());
    }

    private XTrafficStatsBase updateCurrentNetwork(Context context) {
        XTrafficStatsBase previousState = mCurrentNetworkState;
        State mobileState = null;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo mobileNetworkInfo = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (null != mobileNetworkInfo) {
            mobileState = mobileNetworkInfo.getState();
        }
        if (State.CONNECTED == mobileState) {
            mCurrentNetworkState = mMobileTraffic;
        } 
        return previousState;
    }

    public long getMobileTraffic() {
        mCurrentNetworkState.updateTraffic();
        return mMobileTraffic.getTraffic() / CONST_NUM;
    }



    class XMobileTraffic extends XTrafficStatsBase {
        public XMobileTraffic() {
            super();
        }
    }


}
