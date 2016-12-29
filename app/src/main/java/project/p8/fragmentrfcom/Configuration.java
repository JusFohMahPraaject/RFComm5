package project.p8.fragmentrfcom;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import project.p8.fragmentrfcom.R;

/**
 * Created by nassim on 26/12/2016.
 */

public class Configuration extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_layout);
        Fragment configurationFragment = ConfigurationFragment.newInstance();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_holder, configurationFragment);
        transaction.commit();

    }
}
