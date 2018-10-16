package views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Dahman on 9/16/2015.
 */
public class LightTextView extends TextView {

    public LightTextView(Context context) {
        super(context);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "font_light.ttf"));
    }

    public LightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "font_light.ttf"));
    }

    public LightTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "font_light.ttf"));
    }
}
