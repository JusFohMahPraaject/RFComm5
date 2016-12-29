package project.p8.fragmentrfcom;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by nassim on 27/12/2016.
 */

public class ConnectionEstablisher implements Runnable {
    private static final boolean DEBUG = true;
    private static String TAG = "ConnectionEstablisher";
    private ControllerFragment mContext = null;
    private BluetoothDevice mDevice = null;
    private BluetoothSocket mSocket = null;

    public ConnectionEstablisher(ControllerFragment context) {
        mContext = context;
        mDevice = mContext.getmDevice();
        if (DEBUG)
            Log.d(TAG, "Thread created");
    }

    @Override
    public void run() {
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        if (mDevice != null) {
            try {
                Method method;
                method = mDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                mSocket = (BluetoothSocket) method.invoke(mDevice, 1);
                mSocket.connect();
                mContext.setmSocket(mSocket);
                mContext.setmIsConnected(true);
                if (DEBUG)
                    Log.d(TAG, "connection with RFCOMM socket established");
                mContext.setmIsConnected(true);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            new Thread(new CommandWriter(mContext, 1, (byte) 0x1)).start();
            new Thread(new CommandWriter(mContext, 2, (byte) 0x2)).start();
            new Thread(new CommandWriter(mContext, 3, (byte) 0x3)).start();
            new Thread(new CommandWriter(mContext, 4, (byte) 0x4)).start();
            new Thread(new BatteryReader(mContext)).start();
            if (DEBUG)
                Log.d(TAG, "Leaving connection Thread");
        }
    }
}
