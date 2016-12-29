package project.p8.fragmentrfcom;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by nassim on 26/12/2016.
 */

public class ConfigurationFragment extends Fragment {
    static final String SAMPLING_IDENTIFIER = "sampling";
    static final String CONFIGURATION_IDENTIFIER = "configuration";
    static int CONFIGURATION_REQUEST_CODE = 1234;
    private EditText mSamplingValueEditText = null;
    private Button mSave = null;
    private TextView mSamplingValue = null;

    public static ConfigurationFragment newInstance() {
        return new ConfigurationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.configuration_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final SharedPreferences sharedPreferences = getContext().getSharedPreferences(CONFIGURATION_IDENTIFIER, Context.MODE_PRIVATE);
        mSamplingValueEditText = (EditText) getActivity().findViewById(R.id.sampling_value_editText);
        mSave = (Button) getActivity().findViewById(R.id.return_config);
        mSamplingValue = (TextView) getActivity().findViewById(R.id.battery_level_textView);
        mSamplingValueEditText.setText(String.valueOf(sharedPreferences.getInt(SAMPLING_IDENTIFIER,
                10000)));
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences.edit().putInt(SAMPLING_IDENTIFIER, Integer
                        .parseInt(mSamplingValueEditText
                                .getText().toString())).commit();
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
