package net.smartinnovationtechnology.khotwh;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.devspark.appmsg.AppMsg;

import org.json.JSONArray;

import adapters.SubCategoriesAdapter;
import datamodels.Cache;
import datamodels.Constants;
import datamodels.SubCategory;
import json.SubCategoriesHandler;
import utils.InternetUtil;
import views.ProgressFragment;

/**
 * Created by Shamyyoun on 2/24/2015.
 */
public class SubCategoriesFragment extends ProgressFragment {
    public static final String TAG = "SubCategoriesFragment";

    private String mCat;
    private AppCompatActivity mActivity;
    private NetworkController mNetworkController;
    private ImageView mImageHeader;
    private TextView mTextHeader;
    private TextView mTextSubCats;
    private GridView mGridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        initComponents(rootView);

        return rootView;
    }

    /**
     * overridden abstract method, used to set content layout resource
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_sub_categories;
    }

    /**
     * method used to initialize components
     */
    private void initComponents(View rootView) {
        mCat = getArguments().getString(Constants.KEY_CATEGORY);
        mActivity = (AppCompatActivity) getActivity();
        mNetworkController = NetworkController.getInstance(mActivity);
        mImageHeader = (ImageView) rootView.findViewById(R.id.image_header);
        mTextHeader = (TextView) rootView.findViewById(R.id.text_main);
        mTextSubCats = (TextView) rootView.findViewById(R.id.text_subCats);
        mGridView = (GridView) rootView.findViewById(R.id.gridView);

        // check cat & customize fragment
        if (mCat.equals(Constants.CAT_MEN)) {
            mActivity.setTitle(R.string.men);
            mImageHeader.setImageResource(R.drawable.men_header_bg);
            mTextHeader.setText(R.string.mens_wear);
        } else if (mCat.equals(Constants.CAT_WOMEN)) {
            mActivity.setTitle(R.string.women);
            mImageHeader.setImageResource(R.drawable.women_header_bg);
            mTextHeader.setText(R.string.womens_wear);
        } else if (mCat.equals(Constants.CAT_KIDS)) {
            mActivity.setTitle(R.string.kids);
            mImageHeader.setImageResource(R.drawable.kids_header_bg);
            mTextHeader.setText(R.string.kids_wear);
        }

        // get cached response
        String response = Cache.getSubCategoriesResponse(mActivity, mCat);
        if (response != null) {
            try {
                // parse items
                SubCategoriesHandler handler = new SubCategoriesHandler(response);
                SubCategory[] subCategories = handler.handle();

                // update ui
                updateUI(subCategories);
                showMain();

                // load from server to update items
                loadData(false);
            } catch (Exception e) {
                // load from server showing msgs
                loadData(true);
                e.printStackTrace();
            }
        } else {
            // load from server showing msgs
            loadData(true);
        }
    }

    /**
     * method, used to update ui
     */
    private void updateUI(final SubCategory[] subCategories) {
        // set sub cats text
        String subCatsString = "";
        for (int i = 0; i < subCategories.length; i++) {
            if (i != 0) {
                subCatsString += ", ";
            }
            subCatsString += subCategories[i].getTitle();
        }
        mTextSubCats.setText(subCatsString);

        // customize gridview
        SubCategoriesAdapter adapter = new SubCategoriesAdapter(mActivity, R.layout.grid_sub_cats_item, subCategories);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // load products fragment
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.KEY_SUB_CATEGORY, subCategories[position]);
                ProductsFragment fragment = new ProductsFragment();
                fragment.setArguments(bundle);
                FragmentManager fm = mActivity.getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.anim.screen_scale_fade_enter, R.anim.screen_scale_fade_exit,
                        R.anim.screen_scale_fade_enter, R.anim.screen_scale_fade_exit);
                ft.replace(R.id.container, fragment);
                ft.addToBackStack(TAG);
                ft.commit();
            }
        });
    }

    /**
     * method, used to get data from server
     */
    private void loadData(final boolean showMsgs) {
        // check internet connection
        if (!InternetUtil.isConnected(mActivity)) {
            if (showMsgs)
                showError(R.string.no_internet_connection);
            return;
        }

        // show progress
        showProgress();

        // create & send request
        String url = AppController.END_POINT + "/subcat-ws.php?name=" + mCat;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                // handle items
                SubCategoriesHandler handler = new SubCategoriesHandler(jsonArray);
                SubCategory[] subCategories = handler.handle();

                // check items
                if (subCategories != null) {
                    // check data length
                    if (subCategories.length == 0) {
                        if (showMsgs)
                            showEmpty(R.string.no_sub_categories_here);
                    } else {
                        // update ui
                        updateUI(subCategories);
                        // cache data
                        Cache.updateSubCategoriesResponse(mActivity, jsonArray.toString(), mCat);
                        // show main view
                        showMain();
                    }
                } else {
                    if (showMsgs)
                        showError(R.string.unexpected_error_try_later);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (showMsgs)
                    showError(R.string.connection_error);
                volleyError.printStackTrace();
            }
        });

        // add request to request queue
        request.setTag(Constants.VOLLEY_REQ_SUB_CATEGORIES + mCat);
        request.setRetryPolicy(new DefaultRetryPolicy(Constants.CON_TIMEOUT_SUB_CATEGORIES,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mNetworkController.getRequestQueue().cancelAll(Constants.VOLLEY_REQ_SUB_CATEGORIES + mCat);
        mNetworkController.addToRequestQueue(request);
    }

    /**
     * overridden method, used to refresh content
     */
    @Override
    protected void onRefresh() {
        loadData(true);
    }

    /*
     * overridden method
     */
    @Override
    public void onDestroy() {
        // cancel all appmsgs
        AppMsg.cancelAll(mActivity);

        super.onDestroy();
    }
}
