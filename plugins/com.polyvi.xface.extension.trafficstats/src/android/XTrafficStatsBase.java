


package com.polyvi.xface.extension.trafficstats;

import android.net.TrafficStats;

public abstract class XTrafficStatsBase {
    protected long mLastTotalTraffic;
    protected long mTrafficData;
    protected int mUID;

    public XTrafficStatsBase(int uid) {
        mUID = uid;
    }


    public long getTraffic() {
        return mTrafficData;
    }


    public void updateTotalTraffic(long totalTraffic) {
        mLastTotalTraffic = totalTraffic;
    }

    public void updateTraffic() {
        long totalRev = TrafficStats.getUidRxBytes(mUID);
        mTrafficData += (totalRev - mLastTotalTraffic);
        updateTotalTraffic(totalRev);
    }

}
