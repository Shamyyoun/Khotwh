package net.smartinnovationtechnology.khotwh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import adapters.ProductsAdapter;
import database.WishListDAO;
import datamodels.Constants;
import datamodels.Product;
import utils.ViewUtil;

/**
 * Created by Shamyyoun on 7/22/2015.
 */
public class WishListFragment extends Fragment {
    private AppCompatActivity mActivity;
    private WishListDAO mWishListDAO;
    private List<Product> mProducts;
    private View mEmptyView;
    private TextView mTextEmpty;
    private RecyclerView mRecyclerView;
    private ProductsAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_wish_list, container, false);
        initComponents(rootView);

        return rootView;
    }

    /**
     * method, used to initialize components
     */
    private void initComponents(ViewGroup rootView) {
        mActivity = (AppCompatActivity) getActivity();
        mWishListDAO = new WishListDAO(mActivity);
        mEmptyView = rootView.findViewById(R.id.view_empty);
        mTextEmpty = (TextView) mEmptyView.findViewById(R.id.text_empty);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        // set activity title
        mActivity.setTitle(R.string.wishlist);

        // customize empty view
        mTextEmpty.setText(R.string.no_products_in_your_wish_list);

        // get favorite mProducts
        mProducts = mWishListDAO.getAll();

        // check data length to show empty view
        if (mProducts.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

        // customize recycler view
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new ProductsAdapter(mActivity, mProducts, R.layout.recycler_products_item);
        mRecyclerView.setAdapter(mAdapter);

        // add listeners
        mAdapter.setOnItemClickListener(new ProductsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // open product details activity
                Intent intent = new Intent(mActivity, ProductDetailsActivity.class);
                intent.putExtra(Constants.KEY_PRODUCT, mProducts.get(position));
                intent.putExtra(Constants.KEY_POSITION, position);
                startActivityForResult(intent, Constants.REQ_PRODUCT_DETAILS);
            }
        });
        mAdapter.setOnRemoveClickListener(new ProductsAdapter.OnRemoveClickListener() {
            @Override
            public void onRemoveClick(int position) {
                remove(position);
            }
        });
    }

    /**
     * method, used to remove item from wish list
     */
    private void remove(int position) {
        // remove from database
        Product product = mProducts.get(position);
        mWishListDAO.delete(product.getId());

        // remove adapter
        mProducts.remove(position);
        mAdapter.notifyItemRemoved(position);

        // show / hide empty view
        ViewUtil.fadeView(mEmptyView, mProducts.size() == 0);
        ViewUtil.fadeView(mRecyclerView, !(mProducts.size() == 0));
    }

    /**
     * overridden method
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQ_PRODUCT_DETAILS && resultCode == Activity.RESULT_OK) {
            // check if removed
            boolean removed = data.getBooleanExtra(Constants.KEY_REMOVED_FROM_WISH_LIST, false);
            if (removed) {
                // get product position
                int position = data.getIntExtra(Constants.KEY_POSITION, 0);
                // remove
                remove(position);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
