package com.polyvi.xface.extension.trafficstats;

import android.net.TrafficStats;

public abstract class XTrafficStatsBase {
	protected static final String FILENAME = "traffic.txt";
	protected long mLastTotalTraffic;
    protected long mTrafficData;
    protected SavePoint save;
    
    public XTrafficStatsBase() {
    	updateTraffic();
    }

    public void saveTrafficStats(){
    	save.writeToFile(mTrafficData, FILENAME);
    }

    public long getTraffic() {
        return mTrafficData;
    }

    public long getLastTotalTraffic() {
        return mLastTotalTraffic;
    }

    public void updateTotalTraffic(long totalTraffic) {
        mLastTotalTraffic = totalTraffic;
    }

    public void updateTraffic() {
        long totalRev = TrafficStats.getMobileRxBytes()+TrafficStats.getMobileTxBytes();
        mTrafficData += (totalRev - mLastTotalTraffic);
        updateTotalTraffic(totalRev);
    }


    
}
