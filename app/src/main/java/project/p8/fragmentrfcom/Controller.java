package project.p8.fragmentrfcom;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class Controller extends AppCompatActivity {
    public static String TAG_FRAGMENT = "controller";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_layout);
        if (checkForPermissions(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
                && checkForPermissions(Manifest.permission.BLUETOOTH_ADMIN)
                == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN}, ControllerFragment
                    .PERMISSION_REQUEST_CODE);
        } else {
            Toast.makeText(this, "we need the permissions. Goodbye", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private int checkForPermissions(String permission) {
        return ContextCompat.checkSelfPermission(this, permission);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ControllerFragment.PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if (adapter != null) {
                    if (!adapter.isEnabled()) {
                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableIntent, ControllerFragment.ENABLE_REQUEST_CODE);
                    } else {
                        loadFragments();
                    }
                } else {
                    Toast.makeText(this, "your device doesn't support Bluetooth. Goodbye",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ControllerFragment.ENABLE_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, "we need you to enable bluetooth", Toast.LENGTH_SHORT).show();
                finish();
            } else
                loadFragments();
        }
    }

    private void loadFragments() {
        ControllerFragment controllerFragment = new ControllerFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_holder, controllerFragment, TAG_FRAGMENT);
        transaction.commit();
    }
}
