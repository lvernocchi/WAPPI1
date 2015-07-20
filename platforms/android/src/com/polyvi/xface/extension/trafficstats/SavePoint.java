package com.polyvi.xface.extension.trafficstats;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.cordova.CordovaActivity;

import android.content.Context;
import android.util.Log;

public class SavePoint extends CordovaActivity{

	public void writeToFile(long mTrafficData, String FILENAME) {
		
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(FILENAME, Context.MODE_PRIVATE));
            outputStreamWriter.write(String.valueOf(mTrafficData));
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("", "File write failed: " + e.toString());
        }

    }

	public long readFromFile(String FILENAME) {

        String ret = "";

        try {
            InputStream inputStream = openFileInput(FILENAME);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            return -1;
        } catch (IOException e) {
            return -1;
        }

        return Long.parseLong(ret);
    }

}
