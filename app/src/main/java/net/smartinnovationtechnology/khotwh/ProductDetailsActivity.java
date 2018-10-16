package net.smartinnovationtechnology.khotwh;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import adapters.ColorsAdapter;
import adapters.SizesAdapter;
import database.CartDAO;
import database.WishListDAO;
import datamodels.Constants;
import datamodels.Product;
import utils.ViewUtil;

public class ProductDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private Product mProduct;
    private WishListDAO mWishListDAO;
    private CartDAO mCartDAO;
    private TextView mTextTitle;
    private ImageButton mButtonBack;
    private ImageButton mButtonFavorite;
    private View mLayoutDefImage;
    private ViewPager mViewPager;
    private TextView mTextProductTitle;
    private TextView mTextPrice;
    private Spinner mSpinnerSizes;
    private Spinner mSpinnerColors;
    private Button mButtonAddToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        initComponents();
    }

    /**
     * method, used to init components
     */
    private void initComponents() {
        mProduct = (Product) getIntent().getSerializableExtra(Constants.KEY_PRODUCT);
        mWishListDAO = new WishListDAO(this);
        mCartDAO = new CartDAO(this);
        mTextTitle = (TextView) findViewById(R.id.text_title);
        mButtonBack = (ImageButton) findViewById(R.id.button_back);
        mButtonFavorite = (ImageButton) findViewById(R.id.button_favorite);
        mLayoutDefImage = findViewById(R.id.layout_defImage);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mTextProductTitle = (TextView) findViewById(R.id.text_productTitle);
        mTextPrice = (TextView) findViewById(R.id.text_price);
        mSpinnerSizes = (Spinner) findViewById(R.id.spinner_sizes);
        mSpinnerColors = (Spinner) findViewById(R.id.spinner_colors);
        mButtonAddToCart = (Button) findViewById(R.id.button_addToCart);

        // set activity title
        mTextTitle.setText(mProduct.getTitle());

        // customize favorite button
        if (mWishListDAO.existsInDB(mProduct.getId())) {
            mButtonFavorite.setImageResource(R.drawable.red_heart_icon);
        } else {
            mButtonFavorite.setImageResource(R.drawable.blue_heart_icon);
        }

        // customize view pager
        // check colors array length
        if (mProduct.getColors().length >= 1) {
            // get first color in the array & update view pager images
            Product.Color color = mProduct.getColors()[0];
            updateImages(color);
        } else {
            // show def image
            mLayoutDefImage.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.GONE);
        }

        // set basic data
        mTextProductTitle.setText(mProduct.getTitle());
        mTextPrice.setText(mProduct.getPrice() + " " + AppController.CURRENCY_UNIT);

        // check sizes length
        if (mProduct.getSizes().length == 0) {
            // hide sizes spinner
            mSpinnerSizes.setVisibility(View.GONE);
        } else {
            // customize sizes spinner
            SizesAdapter sizesAdapter = new SizesAdapter(this, R.layout.spinner_item, mProduct.getSizes());
            mSpinnerSizes.setAdapter(sizesAdapter);

            // check selected size
            if (mProduct.getSelectedSize() != null) {
                // loop to select it in spinner
                for (int i = 0; i < mProduct.getSizes().length; i++) {
                    String productSize = mProduct.getSizes()[i];
                    String selectedSize = mProduct.getSelectedSize();
                    if (productSize.equals(selectedSize)) {
                        mSpinnerSizes.setSelection(i);
                    }
                }
            }
        }

        // check colors length
        if (mProduct.getColors().length == 0) {
            // hide colors spinner
            mSpinnerColors.setVisibility(View.GONE);
        } else {
            // customize colors spinner
            ColorsAdapter colorsAdapter = new ColorsAdapter(this, R.layout.spinner_colors_item,
                    R.id.textView, mProduct.getColors());
            mSpinnerColors.setAdapter(colorsAdapter);
            mSpinnerColors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // get selected color & update view pager images
                    Product.Color color = mProduct.getColors()[position];
                    updateImages(color);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            // check selected color
            if (mProduct.getSelectedColor() != null) {
                // loop to select it in spinner
                for (int i = 0; i < mProduct.getColors().length; i++) {
                    String productColor = mProduct.getColors()[i].getTitle();
                    String selectedColor = mProduct.getSelectedColor();
                    if (productColor.equals(selectedColor)) {
                        mSpinnerColors.setSelection(i);
                    }
                }
            }
        }

        // add click listeners
        mButtonBack.setOnClickListener(this);
        mButtonFavorite.setOnClickListener(this);
        mButtonAddToCart.setOnClickListener(this);
    }

    /**
     * method, used to check & update view pager images
     */
    private void updateImages(Product.Color color) {
        if (color.getImages().length >= 1) {
            // load images in the view pager
            ImagesAdapter adapter = new ImagesAdapter(getSupportFragmentManager(), color.getImages());
            mViewPager.setAdapter(adapter);

            // show view pager if not visible
            if (mViewPager.getVisibility() != View.VISIBLE) {
                mViewPager.setVisibility(View.VISIBLE);
                mLayoutDefImage.setVisibility(View.GONE);
            }
        } else {
            // hide view pager if visible
            if (mViewPager.getVisibility() == View.VISIBLE) {
                mViewPager.setVisibility(View.GONE);
                mLayoutDefImage.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * overridden method, used to handle click listeners
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_back:
                onBackPressed();
                break;

            case R.id.button_favorite:
                Intent resultIntent = getIntent();
                // check if product is already in wish list
                if (mWishListDAO.existsInDB(mProduct.getId())) {
                    // exists
                    // remove it from db
                    mWishListDAO.delete(mProduct.getId());

                    // update UI
                    Toast.makeText(this, R.string.removed_from_wish_list, Toast.LENGTH_SHORT).show();
                    mButtonFavorite.setImageResource(R.drawable.blue_heart_icon);

                    // update result intent
                    resultIntent.putExtra(Constants.KEY_REMOVED_FROM_WISH_LIST, true);
                } else {
                    // not exists
                    // add it to db
                    mWishListDAO.add(mProduct);

                    // update UI
                    Toast.makeText(this, R.string.added_to_wish_list, Toast.LENGTH_SHORT).show();
                    mButtonFavorite.setImageResource(R.drawable.red_heart_icon);

                    // update result intent
                    resultIntent.putExtra(Constants.KEY_REMOVED_FROM_WISH_LIST, false);
                }
                setResult(RESULT_OK, resultIntent);
                break;

            case R.id.button_addToCart:
                // check id already in cart
                if (mCartDAO.existsInDB(mProduct.getId())) {
                    // show msg
                    Toast.makeText(this, R.string.already_exists_in_your_bag, Toast.LENGTH_SHORT).show();
                } else {
                    // set selected color if possible
                    Product.Color selectedColor = (Product.Color) mSpinnerColors.getSelectedItem();
                    if (selectedColor != null) {
                        mProduct.setSelectedColor(selectedColor.getTitle());
                    }
                    // set selected size if possible
                    String selectedSize = (String) mSpinnerSizes.getSelectedItem();
                    if (selectedSize != null) {
                        mProduct.setSelectedSize(selectedSize);
                    }
                    // add to cart in database
                    mCartDAO.add(mProduct);
                    // show msg
                    Toast.makeText(this, R.string.added_to_your_bag, Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    /**
     * --Images Adapter--
     */
    private class ImagesAdapter extends FragmentStatePagerAdapter {
        private String[] images;

        public ImagesAdapter(FragmentManager fm, String[] images) {
            super(fm);
            this.images = images;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.KEY_IMAGE, images[position]);
            ImageFragment fragment = new ImageFragment();
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return images.length;
        }
    }

    /**
     * --Image Fragment--
     */
    public static class ImageFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.pager_images_item, container, false);

            String image = getArguments().getString(Constants.KEY_IMAGE);
            final View layoutDefImage = rootView.findViewById(R.id.layout_defImage);
            ImageView imageImage = (ImageView) rootView.findViewById(R.id.image_image);

            // load image
            if (!image.isEmpty()) {
                Picasso.with(getActivity()).load(image).into(imageImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        ViewUtil.fadeView(layoutDefImage, false, 150);
                    }

                    @Override
                    public void onError() {
                        ViewUtil.fadeView(layoutDefImage, true, 150);
                    }
                });
            }
            return rootView;
        }
    }
}
