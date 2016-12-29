package project.p8.fragmentrfcom;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by nassim on 27/12/2016.
 */

public class CommandWriter implements Runnable {
    private static final String TAG = "CommandWriter";
    private static final boolean DEBUG = true;
    private ControllerFragment mContext;
    private BluetoothSocket mSocket;
    private int mMonitoredTriggerID;
    private byte mValueToSend;
    private OutputStream mOutputStream;

    public CommandWriter(ControllerFragment context, int monitoredTriggerID, byte valueToSend) {
        mContext = context;
        mMonitoredTriggerID = monitoredTriggerID;
        mValueToSend = valueToSend;
        mSocket = mContext.getmSocket();
        try {
            synchronized (mContext) {
                mOutputStream = mContext.getmSocket().getOutputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!mContext.ismStopTrigger()) {
            while (mContext.ismTrigger(mMonitoredTriggerID)) {
                try {
                    synchronized (mSocket) {
                        mOutputStream.write(new byte[]{mValueToSend});
                        if (DEBUG)
                            Log.d(TAG, "Thread No." + mMonitoredTriggerID + " sent a byte");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
