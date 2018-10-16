package net.smartinnovationtechnology.khotwh;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import database.UserDAO;
import datamodels.Constants;
import datamodels.User;
import utils.InternetUtil;

public class UserUpdateActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    private User mUser;
    private NetworkController mNetworkController;
    private TextView mTextTitle;
    private ImageButton mButtonBack;
    private ScrollView mScrollView;
    private EditText mTextFirstName;
    private EditText mTextLastName;
    private EditText mTextMobile;
    private EditText mTextEmail;
    private EditText mTextAddress;
    private EditText mTextCity;
    private Button mButtonSubmit;
    private boolean mIsRegistration; // used to check if operation is registration or update
    private boolean mChanged; // used to check if user changed its data or not

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update);
        initComponents();
    }

    /**
     * method, used to init components
     */
    private void initComponents() {
        mUser = AppController.getActiveUser(this);
        mNetworkController = NetworkController.getInstance(this);
        mTextTitle = (TextView) findViewById(R.id.text_title);
        mButtonBack = (ImageButton) findViewById(R.id.button_back);
        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        mTextFirstName = (EditText) findViewById(R.id.text_firstName);
        mTextLastName = (EditText) findViewById(R.id.text_lastName);
        mTextMobile = (EditText) findViewById(R.id.text_mobile);
        mTextEmail = (EditText) findViewById(R.id.text_email);
        mTextAddress = (EditText) findViewById(R.id.text_address);
        mTextCity = (EditText) findViewById(R.id.text_city);
        mButtonSubmit = (Button) findViewById(R.id.button_submit);

        // check operation
        if (mUser == null) {
            // registration, set title
            mIsRegistration = true;
            mTextTitle.setText(R.string.user_registration);
        } else {
            // update, set data in fields
            mTextFirstName.setText(mUser.getFirstName());
            mTextLastName.setText(mUser.getLastName());
            mTextMobile.setText(mUser.getMobile());
            mTextEmail.setText(mUser.getEmail());
            mTextAddress.setText(mUser.getAddress());
            mTextCity.setText(mUser.getCity());

            // add change listener to edit texts
            mTextFirstName.addTextChangedListener(this);
            mTextLastName.addTextChangedListener(this);
            mTextMobile.addTextChangedListener(this);
            mTextEmail.addTextChangedListener(this);
            mTextAddress.addTextChangedListener(this);
            mTextCity.addTextChangedListener(this);
        }

        // add done key listener to country edit text
        mTextCity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    updateUser();
                    return true;
                }

                return false;
            }
        });

        // add click listeners
        mButtonBack.setOnClickListener(this);
        mButtonSubmit.setOnClickListener(this);
    }

    /**
     * overridden method, works when change text in edit texts
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mChanged = true;
    }

    /**
     * overridden method, used to handle click listener
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_back:
                onBackPressed();
                break;

            case R.id.button_submit:
                updateUser();
                break;
        }
    }

    /**
     * method, used to validate, send register / update user request to server
     */
    private void updateUser() {
        final String firstName = mTextFirstName.getText().toString().trim();
        final String lastName = mTextLastName.getText().toString().trim();
        final String mobile = mTextMobile.getText().toString().trim();
        final String email = mTextEmail.getText().toString().trim();
        final String address = mTextAddress.getText().toString().trim();
        final String city = mTextCity.getText().toString().trim();

        // validate inputs
        if (firstName.isEmpty()) {
            mTextFirstName.setText("");
            mTextFirstName.setError(getString(R.string.required));
            scrollTo(mTextFirstName);
            return;
        }
        if (lastName.isEmpty()) {
            mTextLastName.setText("");
            mTextLastName.setError(getString(R.string.required));
            scrollTo(mTextLastName);
            return;
        }
        if (mobile.isEmpty()) {
            mTextMobile.setText("");
            mTextMobile.setError(getString(R.string.required));
            scrollTo(mTextMobile);
            return;
        }
        if (email.isEmpty()) {
            mTextEmail.setText("");
            mTextEmail.setError(getString(R.string.required));
            scrollTo(mTextEmail);
            return;
        }
        String pattern = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(email);
        if (!m.find()) {
            mTextEmail.setError(getString(R.string.enter_valid_email));
            return;
        }
        if (address.isEmpty()) {
            mTextAddress.setText("");
            mTextAddress.setError(getString(R.string.required));
            scrollTo(mTextAddress);
            return;
        }
        if (city.isEmpty()) {
            mTextCity.setText("");
            mTextCity.setError(getString(R.string.required));
            scrollTo(mTextCity);
            return;
        }

        // hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mTextCity.getWindowToken(), 0);

        // check internet connection
        if (!InternetUtil.isConnected(this)) {
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        // create progress dialog
        final SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.primary));
        dialog.setCancelable(false);
        dialog.setTitleText(getString(R.string.please_wait));

        // create suitable url
        String url = AppController.END_POINT
                + (mIsRegistration ? "/userregister-ws.php?t=" : "/userupdate-ws.php?t=") + System.currentTimeMillis();

        // create & send request
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // parse response
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");
                    if (Constants.JSON_OK.equals(result)) {
                        // result ok
                        // check operation
                        if (mIsRegistration) {
                            // --registration--
                            // create user object
                            int userId = jsonObject.getInt("user_id");
                            mUser = new User(userId);
                        }

                        // set user data in object
                        mUser.setFirstName(firstName);
                        mUser.setLastName(lastName);
                        mUser.setMobile(mobile);
                        mUser.setEmail(email);
                        mUser.setCity(city);
                        mUser.setAddress(address);

                        // set in runtime
                        AppController.setActiveUser(UserUpdateActivity.this, mUser);
                        // insert / update in database
                        UserDAO userDAO = new UserDAO(UserUpdateActivity.this);
                        userDAO.open();
                        if (mIsRegistration)
                            userDAO.add(mUser);
                        else
                            userDAO.update(mUser);
                        userDAO.close();

                        // finish
                        Toast.makeText(UserUpdateActivity.this, mIsRegistration ? R.string.registered_successfully : R.string.updated_successfully, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        // error
                        // show error
                        dialog.setTitleText(getString(R.string.unexpected_error_try_later))
                                .setConfirmText(getString(R.string.close))
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        dialog.setCancelable(true);
                    }
                } catch (JSONException e) {
                    // show error
                    dialog.setTitleText(getString(R.string.unexpected_error_try_later))
                            .setConfirmText(getString(R.string.close))
                            .setConfirmClickListener(null)
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    dialog.setCancelable(true);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // show error
                dialog.setTitleText(getString(R.string.connection_error))
                        .setConfirmText(getString(R.string.close))
                        .setConfirmClickListener(null)
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                dialog.setCancelable(true);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // add post parameters to the request
                Map<String, String> params = new HashMap<>();
                if (!mIsRegistration)
                    params.put("id", "" + mUser.getId());
                params.put("name", firstName + " " + lastName);
                params.put("phone", mobile);
                params.put("email", email);
                params.put("address",city + "\n" + address);

                return params;
            }
        };

        // add request to request queue
        request.setTag(Constants.VOLLEY_REQ_UPDATE_USER);
        request.setRetryPolicy(new DefaultRetryPolicy(Constants.CON_TIMEOUT_UPDATE_USER,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mNetworkController.getRequestQueue().cancelAll(Constants.VOLLEY_REQ_UPDATE_USER);
        mNetworkController.addToRequestQueue(request);
        // show progress
        dialog.show();
    }

    /**
     * method, used to scroll scrollView to passed view
     */
    private void scrollTo(final View view) {
        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.smoothScrollTo(0, view.getTop());
            }
        });
    }

    /**
     * overridden method
     */
    @Override
    public void onBackPressed() {
        // check operation & changed flag
        if (!mIsRegistration && mChanged) {
            // show confirm dialog
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.discard_changes))
                    .setConfirmText(getString(R.string.ok))
                    .setCancelText(getString(R.string.cancel))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                            finish();
                        }
                    }).show();
        } else {
            finish();
        }
    }

    /**
     * overridden unused method
     */
    @Override
    public void afterTextChanged(Editable s) {
    }

    /**
     * overridden unused method
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }
}
