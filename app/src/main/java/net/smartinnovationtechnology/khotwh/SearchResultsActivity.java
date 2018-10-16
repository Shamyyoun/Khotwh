package net.smartinnovationtechnology.khotwh;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import datamodels.Constants;

public class SearchResultsActivity extends AppCompatActivity {
    private String mKeyword;
    private TextView mTextTitle;
    private ImageButton mButtonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        initComponents();
    }

    /**
     * method, used to init components
     */
    private void initComponents() {
        mKeyword = getIntent().getStringExtra(Constants.KEY_KEYWORD);
        mTextTitle = (TextView) findViewById(R.id.text_title);
        mButtonBack = (ImageButton) findViewById(R.id.button_back);

        // customize actionbar
        if (!mKeyword.isEmpty()) {
            mTextTitle.setText("\"" + mKeyword + "\"");
        }
        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // load products fragment
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ProductsFragment fragment = new ProductsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_KEYWORD, mKeyword);
        fragment.setArguments(bundle);
        ft.replace(R.id.container, fragment);
        ft.commit();
    }
}
