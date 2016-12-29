package project.p8.fragmentrfcom;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Created by nassim on 27/12/2016.
 */

public class ControllerFragment extends Fragment {
    static final UUID BASE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    static final int PERMISSION_REQUEST_CODE = 2016;
    static final int ENABLE_REQUEST_CODE = 2017;
    static final String RASPBERRYPI_NAME = "raspberrypi";
    private static final boolean DEBUG = true;
    private static String TAG = "ControllerFragment";
    private Button mForwardButton = null;
    private volatile boolean mForwardPressed = false;
    private Button mBackwardButton = null;
    private volatile boolean mBackwardPressed = false;
    private Button mRightButton = null;
    private volatile boolean mRightPressed = false;
    private Button mLeftButton = null;
    private volatile boolean mLeftPressed = false;
    private BluetoothDevice mDevice = null;
    private int mBatteryLevel = 0;
    private BluetoothSocket mSocket = null;
    private boolean mStopTrigger = false;
    private TextView mBatteryLevelText = null;
    private boolean mIsConnected = false;
    private int mBatteryCheckTime = 10000;
    private Thread mFragmentConnectionEstablisher = null;

    public static ControllerFragment newInstance() {
        return new ControllerFragment();
    }

    public synchronized static UUID getBaseUuid() {
        return BASE_UUID;
    }

    public synchronized static int getPermissionRequestCode() {
        return PERMISSION_REQUEST_CODE;
    }

    public synchronized static int getEnableRequestCode() {
        return ENABLE_REQUEST_CODE;
    }

    public synchronized static String getRaspberrypiName() {
        return RASPBERRYPI_NAME;
    }

    public synchronized boolean ismTrigger(int id) {
        boolean result = false;
        switch (id) {
            case 1:
                result = mForwardPressed;
                break;
            case 2:
                result = mBackwardPressed;
                break;
            case 3:
                result = mLeftPressed;
                break;
            case 4:
                result = mRightPressed;
                break;
        }
        return result;
    }

    public synchronized int getmBatteryCheckTime() {
        return mBatteryCheckTime;
    }

    public synchronized void setmBatteryCheckTime(int mBatteryCheckTime) {
        this.mBatteryCheckTime = mBatteryCheckTime;
    }

    public synchronized boolean ismIsConnected() {
        return mIsConnected;
    }

    public synchronized void setmIsConnected(boolean mIsConnected) {
        this.mIsConnected = mIsConnected;
        if (DEBUG)
            Log.d(TAG, "Application connected to Raspberry");
    }

    public synchronized boolean ismForwardPressed() {
        return mForwardPressed;
    }

    public synchronized void setmForwardPressed(boolean mForwardPressed) {

        this.mForwardPressed = mForwardPressed;
        if (DEBUG) {
            if (mForwardPressed)
                Log.d(TAG, "Forward button pressed");
            else
                Log.d(TAG, "Forward button released");
        }
    }

    public synchronized boolean ismBackwardPressed() {
        return mBackwardPressed;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public synchronized void setmBackwardPressed(boolean mBackwardPressed) {
        this.mBackwardPressed = mBackwardPressed;
        if (DEBUG) {
            if (mBackwardPressed)
                Log.d(TAG, "Backward button pressed");
            else
                Log.d(TAG, "Backward button released");
        }
    }


    public TextView getmBatteryLevelText() {
        return mBatteryLevelText;
    }

    public void setmBatteryLevelText(TextView mBatteryLevelText) {
        this.mBatteryLevelText = mBatteryLevelText;
    }

    void establishConnectionWithPi() {
        mFragmentConnectionEstablisher = new Thread(new ConnectionEstablisher(this));
        mFragmentConnectionEstablisher.start();
        mForwardButton.setOnTouchListener(new MyOnTouchListener(1));
        mBackwardButton.setOnTouchListener(new MyOnTouchListener(2));
        mLeftButton.setOnTouchListener(new MyOnTouchListener(3));
        mRightButton.setOnTouchListener(new MyOnTouchListener(4));

    }

    public synchronized BluetoothDevice getmDevice() {
        return mDevice;
    }

    public Button getmForwardButton() {
        return mForwardButton;
    }

    public void setmForwardButton(Button mForwardButton) {
        this.mForwardButton = mForwardButton;
    }

    public synchronized boolean ismRightPressed() {
        return mRightPressed;
    }

    public synchronized void setmRightPressed(boolean mRightPressed) {
        this.mRightPressed = mRightPressed;
        if (DEBUG) {
            if (mRightPressed)
                Log.d(TAG, "Right button pressed");
            else
                Log.d(TAG, "Right button released");
        }
    }

    public synchronized boolean ismLeftPressed() {
        return mLeftPressed;
    }

    public synchronized void setmLeftPressed(boolean mLeftPressed) {
        this.mLeftPressed = mLeftPressed;
        if (DEBUG) {
            if (mLeftPressed)
                Log.d(TAG, "Left button pressed");
            else
                Log.d(TAG, "Left button released");
        }
    }

    public synchronized boolean ismStopTrigger() {
        return mStopTrigger;
    }

    public synchronized void setmIsStopTrigger(boolean mStopTrigger) {
        this.mStopTrigger = mStopTrigger;
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized int getmBatteryLevel() {
        return mBatteryLevel;
    }

    public synchronized void setmBatteryLevel(int mBatteryLevel) {
        this.mBatteryLevel = mBatteryLevel;
        this.mBatteryLevelText.setText(mBatteryLevel + "%");
        if (DEBUG)
            Log.d(TAG, "Battery level updated");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.controller_layout, container, false);
    }

    private int checkForPermissions(String permission) {
        return ContextCompat.checkSelfPermission(getActivity(), permission);

    }

    @Override
    public void onStop() {
        super.onStop();
        setmIsStopTrigger(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences preferences = getActivity().getSharedPreferences(ConfigurationFragment
                .CONFIGURATION_IDENTIFIER, Context.MODE_PRIVATE);
        mBatteryCheckTime = preferences.getInt(ConfigurationFragment.SAMPLING_IDENTIFIER, 10000);
        mForwardButton = (Button) getActivity().findViewById(R.id.forward_button);
        mBackwardButton = (Button) getActivity().findViewById(R.id.backward_button);
        mRightButton = (Button) getActivity().findViewById(R.id.turn_right_button);
        mLeftButton = (Button) getActivity().findViewById(R.id.turn_left_button);
        mBatteryLevelText = (TextView) getActivity().findViewById(R.id.battery_level_textView);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bondedDevices =
                BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        for (BluetoothDevice device : bondedDevices) {
            if (device.getName().equals(ControllerFragment.RASPBERRYPI_NAME)) {
                mDevice = device;
                break;
            }
        }
        if (mDevice != null)
            establishConnectionWithPi();
    }


    public synchronized BluetoothSocket getmSocket() {
        return mSocket;
    }

    public synchronized void setmSocket(BluetoothSocket mSocket) {
        this.mSocket = mSocket;
    }

    private class MyOnTouchListener implements View.OnTouchListener {
        private int mmButtonNumber = 0;

        public MyOnTouchListener(int buttonNumber) {
            this.mmButtonNumber = buttonNumber;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                switch (mmButtonNumber) {
                    case 1://forward
                        ControllerFragment.this.setmForwardPressed(true);
                        break;
                    case 2://backward
                        ControllerFragment.this.setmBackwardPressed(true);
                        break;
                    case 3://left
                        ControllerFragment.this.setmLeftPressed(true);
                        break;
                    case 4://right
                        ControllerFragment.this.setmRightPressed(true);
                        break;
                }
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                switch (mmButtonNumber) {
                    case 1://forward
                        ControllerFragment.this.setmForwardPressed(false);
                        break;
                    case 2://backward
                        ControllerFragment.this.setmBackwardPressed(false);
                        break;
                    case 3://left
                        ControllerFragment.this.setmLeftPressed(false);
                        break;
                    case 4://right
                        ControllerFragment.this.setmRightPressed(false);
                        break;
                }
            }
            return false;
        }
    }
}
