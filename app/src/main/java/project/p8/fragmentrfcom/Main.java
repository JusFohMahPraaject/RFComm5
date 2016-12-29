package project.p8.fragmentrfcom;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_layout);
        Fragment firstFragment = MainMenuFragment.newInstance();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_holder, firstFragment);
        transaction.addToBackStack("empty_layout");
        transaction.commit();
        SharedPreferences sharedPreferences = getSharedPreferences(ConfigurationFragment
                .CONFIGURATION_IDENTIFIER, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(ConfigurationFragment.SAMPLING_IDENTIFIER, 10000);
    }


}
