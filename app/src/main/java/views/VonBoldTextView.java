package views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Dahman on 9/16/2015.
 */
public class VonBoldTextView extends TextView {

    public VonBoldTextView(Context context) {
        super(context);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "von_bold.ttf"));
    }

    public VonBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "von_bold.ttf"));
    }

    public VonBoldTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "von_bold.ttf"));
    }
}
