package project.p8.fragmentrfcom;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by nassim on 27/12/2016.
 */

public class BatteryReader implements Runnable {
    private static final String TAG = "BatteryReader";
    private static final boolean DEBUG = true;
    private BluetoothSocket mSocket;
    private ControllerFragment mContext;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private byte[] mRequestBuffer = new byte[1];
    private byte[] mResPonseBuffer = new byte[1];

    public BatteryReader(ControllerFragment context) {
        mContext = context;
        mSocket = mContext.getmSocket();
        try {
            mInputStream = mSocket.getInputStream();
            mOutputStream = mSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRequestBuffer[0] = (byte) 0xee;
        if (DEBUG)
            Log.d(TAG, "Thread created");
    }

    @Override
    public void run() {
        while (!mContext.ismStopTrigger()) {
            while (mContext.ismIsConnected()) {
                synchronized (mOutputStream) {
                    try {
                        mOutputStream.write(mRequestBuffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (DEBUG)
                    Log.d(TAG, "Battery read request sent");
                try {
                    /*blocking call*/
                    mInputStream.read(mResPonseBuffer);
                    updateContext();
                    Thread.sleep(mContext.getmBatteryCheckTime());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateContext() {
        mContext.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ControllerFragment controller = (ControllerFragment) mContext.getActivity()
                        .getFragmentManager()
                        .findFragmentByTag(Controller.TAG_FRAGMENT);
                controller.setmBatteryLevel((int) mResPonseBuffer[0]);
            }
        });
    }
}
