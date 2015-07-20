
package com.example.wappi;

import android.os.Bundle;


import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.cordova.*;


public class MainActivity extends CordovaActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Set by <content src="index.html" /> in config.xml	
        loadUrl(launchUrl);
    }
    
}

