package net.smartinnovationtechnology.khotwh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.List;
import java.util.Map;

import adapters.CartAdapter;
import cn.pedant.SweetAlert.SweetAlertDialog;
import database.CartDAO;
import database.WishListDAO;
import datamodels.Constants;
import datamodels.Product;
import utils.InternetUtil;
import utils.ViewUtil;
import views.DividerItemDecoration;

/**
 * Created by Shamyyoun on 7/22/2015.
 */
public class CartFragment extends Fragment {
    private AppCompatActivity mActivity;
    private CartDAO mCartDAO;
    private WishListDAO mWishListDAO;
    private NetworkController mNetworkController;
    private List<Product> mProducts;
    private View mEmptyView;
    private TextView mTextEmpty;
    private View mMainView;
    private RecyclerView mRecyclerView;
    private CartAdapter mAdapter;
    private TextView mTextTotal;
    private int mTotal;
    private Button mButtonOrder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_cart, container, false);
        initComponents(rootView);

        return rootView;
    }

    /**
     * method, used to initialize components
     */
    private void initComponents(ViewGroup rootView) {
        mActivity = (AppCompatActivity) getActivity();
        mCartDAO = new CartDAO(mActivity);
        mWishListDAO = new WishListDAO(mActivity);
        mNetworkController = NetworkController.getInstance(mActivity);
        mEmptyView = rootView.findViewById(R.id.view_empty);
        mTextEmpty = (TextView) mEmptyView.findViewById(R.id.text_empty);
        mMainView = rootView.findViewById(R.id.view_main);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mTextTotal = (TextView) rootView.findViewById(R.id.text_total);
        mButtonOrder = (Button) rootView.findViewById(R.id.button_order);

        // set activity title
        mActivity.setTitle(R.string.my_bag);

        // customize empty view
        mTextEmpty.setText(R.string.no_products_in_your_bag);

        // get favorite mProducts
        mProducts = mCartDAO.getAll();

        // check data length to show empty view
        if (mProducts.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mMainView.setVisibility(View.GONE);
        }

        // get & set total
        for (Product product : mProducts) {
            mTotal += product.getPrice();
        }
        mTextTotal.setText(mTotal + " " + AppController.CURRENCY_UNIT);

        // customize recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new CartAdapter(mActivity, mProducts, R.layout.recycler_cart_item);
        mRecyclerView.setAdapter(mAdapter);

        // add adapter listeners
        mAdapter.setOnItemClickListener(new CartAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // open product details activity
                Intent intent = new Intent(mActivity, ProductDetailsActivity.class);
                intent.putExtra(Constants.KEY_PRODUCT, mProducts.get(position));
                intent.putExtra(Constants.KEY_POSITION, position);
                startActivityForResult(intent, Constants.REQ_PRODUCT_DETAILS);
            }
        });
        mAdapter.setOnFavoriteClickListener(new CartAdapter.OnFavoriteClickListener() {
            @Override
            public void onFavoriteClick(int position) {
                addToWishList(position);
            }
        });
        mAdapter.setOnRemoveClickListener(new CartAdapter.OnRemoveClickListener() {
            @Override
            public void onRemoveClick(int position) {
                remove(position);
            }
        });

        // add order click listener
        mButtonOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order();
            }
        });
    }

    /**
     * method, used to add item to wish list and remove it from cart
     */
    private void addToWishList(int position) {
        // add to wish list table in database
        Product product = mProducts.get(position);
        mWishListDAO.add(product);

        // show msg
        Toast.makeText(mActivity, R.string.added_to_wish_list, Toast.LENGTH_SHORT).show();

        // remove from cart
        remove(position);
    }

    /**
     * method, used to remove item from cart
     */
    private void remove(int position) {
        // remove from database
        Product product = mProducts.get(position);
        mCartDAO.delete(product.getId());

        // remove from adapter
        mProducts.remove(position);
        mAdapter.notifyItemRemoved(position);

        // update total
        mTotal -= product.getPrice();
        mTextTotal.setText(mTotal + " " + AppController.CURRENCY_UNIT);

        // show / hide empty view
        ViewUtil.fadeView(mEmptyView, mProducts.size() == 0);
        ViewUtil.fadeView(mMainView, !(mProducts.size() == 0));
    }

    /**
     * method, used to send order to server
     */
    private void order() {
        // check user
        if (AppController.getActiveUser(mActivity) == null) {
            // show msg
            Toast.makeText(mActivity, R.string.please_register_before_order, Toast.LENGTH_SHORT).show();
            // open user registration activity
            startActivity(new Intent(mActivity, UserUpdateActivity.class));
            return;
        }

        // check internet connection
        if (!InternetUtil.isConnected(mActivity)) {
            Toast.makeText(mActivity, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        // create progress dialog
        final SweetAlertDialog dialog = new SweetAlertDialog(mActivity, SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.primary));
        dialog.setCancelable(false);
        dialog.setTitleText(getString(R.string.please_wait));

        // create & send request
        String url = AppController.END_POINT + "/order-ws.php?t=" + System.currentTimeMillis();
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // parse response
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");
                    if (Constants.JSON_OK.equals(result)) {
                        // result ok
                        // clear cart items from database
                        mCartDAO.clear();

                        // finish
                        dialog.dismiss();
                        Toast.makeText(mActivity, R.string.successful_order, Toast.LENGTH_LONG).show();

                        // load men fragment as default fragment
                        SubCategoriesFragment fragment = new SubCategoriesFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.KEY_CATEGORY, Constants.CAT_MEN);
                        fragment.setArguments(bundle);
                        FragmentManager fm = mActivity.getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.container, fragment);
                        ft.commit();
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
                params.put("user_id", "" + AppController.getActiveUser(mActivity).getId());
                params.put("total", "" + mTotal);

                // add products param
                String productsParam = "";
                for (int i = 0; i < mProducts.size(); i++) {
                    Product product = mProducts.get(i);
                    if (i != 0) {
                        productsParam += ",";
                    }
                    productsParam += product.getId() + ";" + product.getCount() + ";";
                    // check selected size to add
                    if (product.getSelectedSize() != null) {
                        productsParam += product.getSelectedSize();
                    }
                    productsParam += ";";
                    // check to add selected color
                    if (product.getSelectedColor() != null) {
                        productsParam += product.getSelectedColor();
                    }
                }
                params.put("products", productsParam);

                return params;
            }
        };

        // add request to request queue
        request.setTag(Constants.VOLLEY_REQ_ORDER);
        request.setRetryPolicy(new DefaultRetryPolicy(Constants.CON_TIMEOUT_ORDER,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mNetworkController.getRequestQueue().cancelAll(Constants.VOLLEY_REQ_ORDER);
        mNetworkController.addToRequestQueue(request);
        // show progress
        dialog.show();
    }

    /**
     * overridden method
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQ_PRODUCT_DETAILS && resultCode == Activity.RESULT_OK) {
            // get product position
            int position = data.getIntExtra(Constants.KEY_POSITION, 0);
            // refresh adapter
            mAdapter.notifyItemChanged(position);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
