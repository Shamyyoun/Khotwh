package net.smartinnovationtechnology.khotwh;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.balysv.materialripple.MaterialRippleLayout;

import datamodels.Constants;

public class CategoriesActivity extends AppCompatActivity {
    private View mLayoutMen;
    private View mLayoutWomen;
    private View mLayoutKids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        initComponents();
    }

    /**
     * method, used to initialize components
     */
    private void initComponents() {
        mLayoutMen = findViewById(R.id.layout_men);
        mLayoutWomen = findViewById(R.id.layout_women);
        mLayoutKids = findViewById(R.id.layout_kids);

        // add click listeners
        mLayoutMen.setOnClickListener(new CategoryClickListener(Constants.CAT_MEN));
        mLayoutWomen.setOnClickListener(new CategoryClickListener(Constants.CAT_WOMEN));
        mLayoutKids.setOnClickListener(new CategoryClickListener(Constants.CAT_KIDS));
    }

    /**
     * *** CategoryClickListener ***
     */
    private class CategoryClickListener implements View.OnClickListener {
        private String category;

        public CategoryClickListener(String category) {
            this.category = category;
        }

        @Override
        public void onClick(View v) {
            // start main activity with passed category
            Intent intent = new Intent(CategoriesActivity.this, MainActivity.class);
            intent.putExtra(Constants.KEY_CATEGORY, category);
            startActivity(intent);
            finish();
        }
    }
}
