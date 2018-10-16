package net.smartinnovationtechnology.khotwh;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.devspark.appmsg.AppMsg;
import com.rey.material.widget.ProgressView;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.recyclerview.JazzyRecyclerViewScrollListener;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import adapters.ProductsAdapter;
import datamodels.Cache;
import datamodels.Constants;
import datamodels.Product;
import datamodels.SubCategory;
import json.ProductsHandler;
import utils.InternetUtil;
import utils.ViewUtil;
import views.ProgressFragment;

/**
 * Created by Shamyyoun on 2/24/2015.
 */
public class ProductsFragment extends ProgressFragment {
    public static final String TAG = "ProductsFragment";

    private SubCategory mSubCat;
    private String mKeyword;
    private AppCompatActivity mActivity;
    private NetworkController mNetworkController;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private ProductsAdapter mAdapter;
    private List<Product> mProducts;
    private ProgressView mProgressFooter;
    private boolean mLoadingItems; // flag used to know if loading items from server is running or not
    private int mFooterPosition; // used to hold footer position in recycler view
    private boolean mNoMoreItems; // flag used to know if no more items to load

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
        return R.layout.fragment_products;
    }

    /**
     * method used to initialize components
     */
    private void initComponents(View rootView) {
        // get passed arguments
        mSubCat = (SubCategory) getArguments().getSerializable(Constants.KEY_SUB_CATEGORY);
        if (mSubCat == null)
            mKeyword = getArguments().getString(Constants.KEY_KEYWORD);

        // init objects
        mActivity = (AppCompatActivity) getActivity();
        mNetworkController = NetworkController.getInstance(mActivity);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mProducts = new ArrayList();
        mProgressFooter = (ProgressView) rootView.findViewById(R.id.progress_footer);

        // set title if sub category is not null
        if (mSubCat != null)
            mActivity.setTitle(mSubCat.getTitle());

        // customize recycler view
        mLayoutManager = new GridLayoutManager(mActivity, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        JazzyRecyclerViewScrollListener scrollListener = new JazzyRecyclerViewScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                // try to do jazzy animation
                try {
                    super.onScrolled(recyclerView, dx, dy);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // check if not loading more is running and there is new items
                if (!mLoadingItems && !mNoMoreItems) {
                    // check if reached to end of the list
                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        loadMore();
                    }
                }
            }
        };
        scrollListener.setTransitionEffect(JazzyHelper.SLIDE_IN);
        mRecyclerView.setOnScrollListener(scrollListener);

        // get cached response if sub category is not null
        if (mSubCat != null) {
            String response = Cache.getProductsResponse(mActivity, mSubCat.getId());
            if (response != null) {
                try {
                    // parse items
                    ProductsHandler handler = new ProductsHandler(response);
                    mProducts = handler.handle();

                    // update ui
                    updateUI();
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
        } else {
            // load from server showing msgs
            loadData(true);
        }
    }

    /**
     * method, used to update ui
     */
    private void updateUI() {
        mAdapter = new ProductsAdapter(mActivity, mProducts, R.layout.recycler_products_item);
        mAdapter.setOnItemClickListener(new ProductsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // open product details activity
                Intent intent = new Intent(mActivity, ProductDetailsActivity.class);
                intent.putExtra(Constants.KEY_PRODUCT, mProducts.get(position));
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
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
        mLoadingItems = true;

        // prepare suitable url
        String url = AppController.END_POINT + "/product-ws.php?";
        if (mSubCat != null) {
            url += "id=" + mSubCat.getId();
        } else {
            // encode keyword
            try {
                mKeyword = URLEncoder.encode(mKeyword, "UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
            url += "keyword=" + mKeyword;
        }

        // create & send request
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                // handle items
                ProductsHandler handler = new ProductsHandler(jsonArray);
                mProducts = handler.handle();

                // check items
                if (mProducts != null) {
                    // check data size
                    if (mProducts.size() == 0) {
                        // update flags
                        mNoMoreItems = true;
                        mLoadingItems = false;
                        // show suitable empty msg
                        if (showMsgs) {
                            if (mSubCat != null)
                                showEmpty(R.string.no_products_here);
                            else
                                showEmpty(getString(R.string.no_products_here) + " \"" + mKeyword + "\"");
                        }
                    } else {
                        // update ui
                        updateUI();
                        // cache data if not search operation
                        if (mSubCat != null)
                            Cache.updateProductsResponse(mActivity, jsonArray.toString(), mSubCat.getId());
                        // update flags
                        mNoMoreItems = mProducts.size() < Constants.SERVER_LIMIT_PRODUCTS;
                        mLoadingItems = false;
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

        // prepare suitable request tag
        String requestTag;
        if (mSubCat != null)
            requestTag = Constants.VOLLEY_REQ_PRODUCTS + mSubCat.getId();
        else
            requestTag = Constants.VOLLEY_REQ_SEARCH;

        // add request to request queue
        request.setTag(requestTag);
        request.setRetryPolicy(new DefaultRetryPolicy(Constants.CON_TIMEOUT_PRODUCTS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mNetworkController.addToRequestQueue(request);
    }

    /**
     * method, used to get more shops from server and handle it recycler view
     */
    private void loadMore() {
        // check internet connection
        if (!InternetUtil.isConnected(mActivity)) {
            return;
        }

        // add progress footer
        showProgressFooter(true);

        // prepare suitable url
        String url = AppController.END_POINT + "/product-ws.php?&limit="
                + Constants.SERVER_LIMIT_PRODUCTS + "&offset=" + mProducts.size();
        if (mSubCat != null) {
            url += "&id=" + mSubCat.getId();
        } else {
            // encode keyword
            try {
                mKeyword = URLEncoder.encode(mKeyword, "UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
            url += "&keyword=" + mKeyword;
        }

        // create & send request
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                // handle items
                ProductsHandler handler = new ProductsHandler(jsonArray);
                List<Product> newProducts = handler.handle();

                // check items
                if (newProducts != null) {
                    // check size
                    if (newProducts.size() != 0) {
                        // check if size is less than limit
                        if (newProducts.size() < Constants.SERVER_LIMIT_PRODUCTS) {
                            // no more items in the next time
                            mNoMoreItems = true;
                        }

                        // add new items
                        mProducts.addAll(newProducts);
                        mAdapter.notifyItemRangeInserted(mProducts.size() - newProducts.size() - 1, newProducts.size());
                    } else {
                        // no more items
                        mNoMoreItems = true;
                    }
                }

                // remove progress footer
                showProgressFooter(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                // remove progress footer
                showProgressFooter(false);
            }
        });

        // prepare suitable request tag
        String requestTag;
        if (mSubCat != null)
            requestTag = Constants.VOLLEY_REQ_PRODUCTS + mSubCat.getId();
        else
            requestTag = Constants.VOLLEY_REQ_SEARCH;

        // add request to request queue
        request.setTag(requestTag);
        request.setRetryPolicy(new DefaultRetryPolicy(Constants.CON_TIMEOUT_PRODUCTS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mNetworkController.addToRequestQueue(request);
    }

    /**
     * method, used to remove progress footer from recycler view
     */
    private void showProgressFooter(boolean show) {
        ViewUtil.fadeView(mProgressFooter, show, 100);
        mLoadingItems = show;

        /*
        if (show) {
            // add progress footer
            Product footerItem = new Product(-1);
            mProducts.add(footerItem);
            mFooterPosition = mProducts.size() - 1;
            mAdapter.notifyItemInserted(mFooterPosition);
            mLoadingItems = true;
        } else {
            // remove progress footer
            mProducts.remove(mFooterPosition);
            mAdapter.notifyItemRemoved(mFooterPosition);
            mLoadingItems = false;
            mFooterPosition = 0;
        }
        */
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
