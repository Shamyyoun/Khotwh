package net.smartinnovationtechnology.khotwh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import datamodels.Constants;
import datamodels.User;

/**
 * Created by Shamyyoun on 2/24/2015.
 */
public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";

    private AppCompatActivity mActivity;
    private TextView mTextFirstName;
    private TextView mTextLastName;
    private TextView mTextMobile;
    private TextView mTextEmail;
    private TextView mTextAddress;
    private TextView mTextCity;
    private Button mButtonUpdate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_profile, container, false);
        initComponents(rootView);

        return rootView;
    }

    /**
     * method used to initialize components
     */
    private void initComponents(View rootView) {
        mActivity = (MainActivity) getActivity();
        mTextFirstName = (TextView) rootView.findViewById(R.id.text_firstName);
        mTextLastName = (TextView) rootView.findViewById(R.id.text_lastName);
        mTextMobile = (TextView) rootView.findViewById(R.id.text_mobile);
        mTextEmail = (TextView) rootView.findViewById(R.id.text_email);
        mTextAddress = (TextView) rootView.findViewById(R.id.text_address);
        mTextCity = (TextView) rootView.findViewById(R.id.text_city);
        mButtonUpdate = (Button) rootView.findViewById(R.id.button_update);

        // set title
        mActivity.setTitle(R.string.profile);

        // update ui
        updateUI();

        // add click listener
        mButtonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open user update activity
                Intent intent = new Intent(mActivity, UserUpdateActivity.class);
                startActivityForResult(intent, Constants.REQ_USER_UPDATE);
            }
        });
    }

    /**
     * method, used to set values in text views
     */
    private void updateUI() {
        // set data
        User user = AppController.getActiveUser(mActivity);
        mTextFirstName.setText(user.getFirstName());
        mTextLastName.setText(user.getLastName());
        mTextMobile.setText(user.getMobile());
        mTextEmail.setText(user.getEmail());
        mTextAddress.setText(user.getAddress());
        mTextCity.setText(user.getCity());
    }

    /**
     * overridden method
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check request & result codes
        if (requestCode == Constants.REQ_USER_UPDATE && resultCode == Activity.RESULT_OK) {
            // update ui
            updateUI();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
