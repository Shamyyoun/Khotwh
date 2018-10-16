package com.special.ResideMenu;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.special.ResideMenu.R;

/**
 * User: special
 * Date: 13-12-10
 * Time: 下午11:05
 * Mail: specialcyci@gmail.com
 */
public class ResideMenuItem extends LinearLayout {
    /**
     * menu item  title
     */
    private TextView tv_title;
    private Typeface typeface;

    public ResideMenuItem(Context context) {
        super(context);
        initViews(context);
    }

    public ResideMenuItem(Context context, int title) {
        super(context);
        initViews(context);
        tv_title.setText(title);
    }

    public ResideMenuItem(Context context, int title, Typeface typeface) {
        super(context);
        this.typeface = typeface;
        initViews(context);
        tv_title.setText(title);
    }

    public ResideMenuItem(Context context, String title) {
        super(context);
        initViews(context);
        tv_title.setText(title);
    }

    private void initViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.residemenu_item, this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        if (typeface != null) {
            tv_title.setTypeface(typeface);
        }
    }

    /**
     * set the title with resource
     * ;
     *
     * @param title
     */
    public void setTitle(int title) {
        tv_title.setText(title);
    }

    /**
     * set the title with string;
     *
     * @param title
     */
    public void setTitle(String title) {
        tv_title.setText(title);
    }

    public void setTypeface(Typeface typeface) {
        if (typeface != null) {
            this.typeface = typeface;
            tv_title.setTypeface(typeface);
        }
    }
}
