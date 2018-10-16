package views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Dahman on 9/16/2015.
 */
public class BoldTextView extends TextView {

    public BoldTextView(Context context) {
        super(context);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "font_bold.ttf"));
    }

    public BoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "font_bold.ttf"));
    }

    public BoldTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "font_bold.ttf"));
    }
}
