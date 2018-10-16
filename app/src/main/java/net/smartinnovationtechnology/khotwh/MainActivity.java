package net.smartinnovationtechnology.khotwh;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

import datamodels.Constants;
import utils.InternetUtil;
import utils.ViewUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int STATUS_BAR_COLOR_ANIM_DURATION = 300;

    private String mCategory;
    private ResideMenu mResideMenu;
    private ResideMenuItem mItemProfile;
    private ResideMenuItem mItemMen;
    private ResideMenuItem mItemWomen;
    private ResideMenuItem mItemKids;
    private ResideMenuItem mItemWishList;
    private ResideMenuItem mItemMyCart;
    private TextView mTextTitle;
    private EditText mTextSearch;
    private ImageButton mButtonMenu;
    private ImageButton mButtonSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
    }

    /**
     * method, used to initialize components
     */
    private void initComponents() {
        // get selected category
        mCategory = getIntent().getStringExtra(Constants.KEY_CATEGORY);

        // setup reside menu
        setupMenu();

        // find views
        mTextTitle = (TextView) findViewById(R.id.text_title);
        mTextSearch = (EditText) findViewById(R.id.text_search);
        mButtonMenu = (ImageButton) findViewById(R.id.button_menu);
        mButtonSearch = (ImageButton) findViewById(R.id.button_search);

        // add click listeners
        mButtonMenu.setOnClickListener(this);
        mButtonSearch.setOnClickListener(this);

        // add search listener to search edit text
        mTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // validate keyword
                    String keyword = mTextSearch.getText().toString().trim();
                    if (keyword.isEmpty()) {
                        return true;
                    }

                    // check internet connection
                    if (!InternetUtil.isConnected(MainActivity.this)) {
                        // show error toast
                        Toast.makeText(MainActivity.this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    // open search results activity with keyword
                    Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
                    intent.putExtra(Constants.KEY_KEYWORD, keyword);
                    startActivity(intent);

                    return true;
                }
                return false;
            }
        });

        // load sub category fragment according to passed category
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_CATEGORY, mCategory);
        SubCategoriesFragment fragment = new SubCategoriesFragment();
        fragment.setArguments(bundle);
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    /**
     * method, used to initialize reside menu and set up it
     */
    private void setupMenu() {
        // create reside menu
        mResideMenu = new ResideMenu(this);
        mResideMenu.setUse3D(true);
        mResideMenu.setBackground(R.drawable.nav_bg);
        mResideMenu.setLeftMenuLogo(R.drawable.white_logo);
        mResideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        mResideMenu.setMenuListener(new ResideMenu.OnMenuListener() {
            private int darkColor = getResources().getColor(R.color.primary_dark);
            private int navDarkColor = getResources().getColor(R.color.nav_primary_dark);

            @Override
            public void openMenu() {
                changeStatusBarColor(darkColor, navDarkColor);
            }

            @Override
            public void closeMenu() {
                changeStatusBarColor(navDarkColor, darkColor);
            }

            /**
             * method, used to change status bar color with nice animation
             */
            private void changeStatusBarColor(int fromColor, int toColor) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // get window object and add flags
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                    // create and start object animator
                    ObjectAnimator animator = ObjectAnimator.ofArgb(window, "statusBarColor", fromColor, toColor);
                    animator.setDuration(STATUS_BAR_COLOR_ANIM_DURATION);
                    animator.setInterpolator(new DecelerateInterpolator());
                    animator.start();
                }
            }
        });
        mResideMenu.attachToActivity(this);

        // create menu items
        Typeface boldTypeface = Typeface.createFromAsset(getAssets(), "font_bold.ttf");
        mItemProfile = new ResideMenuItem(this, R.string.profile, boldTypeface);
        mItemMen = new ResideMenuItem(this, R.string.men, boldTypeface);
        mItemWomen = new ResideMenuItem(this, R.string.women, boldTypeface);
        mItemKids = new ResideMenuItem(this, R.string.kids, boldTypeface);
        mItemWishList = new ResideMenuItem(this, R.string.wishlist, boldTypeface);
        mItemMyCart = new ResideMenuItem(this, R.string.my_bag, boldTypeface);

        // add reside click listeners to items
        mItemProfile.setOnClickListener(new ResideItemClickListener(R.string.profile));
        mItemMen.setOnClickListener(new ResideItemClickListener(R.string.men));
        mItemWomen.setOnClickListener(new ResideItemClickListener(R.string.women));
        mItemKids.setOnClickListener(new ResideItemClickListener(R.string.kids));
        mItemWishList.setOnClickListener(new ResideItemClickListener(R.string.wishlist));
        mItemMyCart.setOnClickListener(new ResideItemClickListener(R.string.my_bag));

        // add items to the menu
        mResideMenu.addMenuItem(mItemProfile, ResideMenu.DIRECTION_LEFT);
        mResideMenu.addMenuItem(mItemMen, ResideMenu.DIRECTION_LEFT);
        mResideMenu.addMenuItem(mItemWomen, ResideMenu.DIRECTION_LEFT);
        mResideMenu.addMenuItem(mItemKids, ResideMenu.DIRECTION_LEFT);
        mResideMenu.addMenuItem(mItemWishList, ResideMenu.DIRECTION_LEFT);
        mResideMenu.addMenuItem(mItemMyCart, ResideMenu.DIRECTION_LEFT);
    }

    /**
     * overridden method, used to handle click listeners
     */
    @Override
    public void onClick(View v) {
        // prepare fragment objects
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle bundle = new Bundle();
        Fragment fragment = null;

        if (v.getId() == R.id.button_menu) {
            // open / close menu
            if (mResideMenu.isOpened()) {
                mResideMenu.closeMenu();
            } else {
                // hide keyboard & open menu
                InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mTextSearch.getWindowToken(), 0);
                mResideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        } else if (v.getId() == R.id.button_search) {
            if (mTextTitle.getVisibility() == View.VISIBLE) {
                // show search edit text and hide title
                mTextSearch.setText("");
                ViewUtil.fadeView(mTextTitle, false, 70);
                mTextSearch.setVisibility(View.VISIBLE);
                // change search button to close
                mButtonSearch.setImageResource(R.drawable.close_icon);
                // request focus & show keyboard
                mTextSearch.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mTextSearch, InputMethodManager.SHOW_IMPLICIT);
            } else {
                // show title and hide search edit text
                ViewUtil.fadeView(mTextTitle, true, 70);
                mTextSearch.setVisibility(View.GONE);
                // change search button to search
                mButtonSearch.setImageResource(R.drawable.search_icon);
                // hide keyboard
                InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mTextSearch.getWindowToken(), 0);
            }
        } else if (v.getId() == mItemMen.getId()) {
            // load men sub category fragment
            bundle.putString(Constants.KEY_CATEGORY, Constants.CAT_MEN);
            fragment = new SubCategoriesFragment();
        } else if (v.getId() == mItemWomen.getId()) {
            // load women sub category fragment
            bundle.putString(Constants.KEY_CATEGORY, Constants.CAT_WOMEN);
            fragment = new SubCategoriesFragment();
        } else if (v.getId() == mItemKids.getId()) {
            // load kids sub category fragment
            bundle.putString(Constants.KEY_CATEGORY, Constants.CAT_KIDS);
            fragment = new SubCategoriesFragment();
        }

        // check fragment
        if (fragment != null) {
            // load fragment
            fragment.setArguments(bundle);
            ft.replace(R.id.container, fragment);
            ft.commit();

            // close menu
            mResideMenu.closeMenu();
        }
    }

    /**
     * overridden method
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check result & request codes
        if (requestCode == Constants.REQ_USER_REGISTRATION && resultCode == RESULT_OK) {
            // load profile activity
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.container, new ProfileFragment());
            ft.commitAllowingStateLoss();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * overridden method, used to set activity title
     */
    @Override
    public void setTitle(int titleId) {
        mTextTitle.setText(titleId);
    }

    /**
     * overridden method, used to set activity title
     */
    @Override
    public void setTitle(CharSequence title) {
        mTextTitle.setText(title);
    }

    /**
     * overridden method
     */
    @Override
    public void onBackPressed() {
        if (mResideMenu.isOpened()) {
            // close it
            mResideMenu.closeMenu();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * *** ResideItemClickListener ***
     */
    private class ResideItemClickListener implements View.OnClickListener {
        private int titleResId;

        public ResideItemClickListener(int titleResId) {
            this.titleResId = titleResId;
        }

        @Override
        public void onClick(View v) {
            // prepare fragment objects
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Bundle bundle = new Bundle();
            Fragment fragment = null;

            // check item
            if (titleResId == R.string.profile) {
                // check active user
                if (AppController.getActiveUser(MainActivity.this) != null) {
                    // load profile fragment
                    fragment = new ProfileFragment();
                } else {
                    // open user registration activity
                    Intent intent = new Intent(MainActivity.this, UserUpdateActivity.class);
                    startActivityForResult(intent, Constants.REQ_USER_REGISTRATION);
                    // close menu after static time
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mResideMenu.closeMenu();
                        }
                    }, 500);
                }
            } else if (titleResId == R.string.men) {
                // load men sub category fragment
                bundle.putString(Constants.KEY_CATEGORY, Constants.CAT_MEN);
                fragment = new SubCategoriesFragment();
            } else if (titleResId == R.string.women) {
                // load women sub category fragment
                bundle.putString(Constants.KEY_CATEGORY, Constants.CAT_WOMEN);
                fragment = new SubCategoriesFragment();
            } else if (titleResId == R.string.kids) {
                // load kids sub category fragment
                bundle.putString(Constants.KEY_CATEGORY, Constants.CAT_KIDS);
                fragment = new SubCategoriesFragment();
            } else if (titleResId == R.string.wishlist) {
                // load wish list fragment
                fragment = new WishListFragment();
            } else if (titleResId == R.string.my_bag) {
                // load cart fragment
                fragment = new CartFragment();
            }

            // check fragment
            if (fragment != null) {
                // remove stored fragments from back stack
                fm .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                // load new fragment
                fragment.setArguments(bundle);
                ft.replace(R.id.container, fragment);
                ft.commit();

                // close menu
                mResideMenu.closeMenu();
            }
        }
    }
}
