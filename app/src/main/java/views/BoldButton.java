package views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Dahman on 9/16/2015.
 */
public class BoldButton extends Button {

    public BoldButton(Context context) {
        super(context);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "font_bold.ttf"));
    }

    public BoldButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "font_bold.ttf"));
    }

    public BoldButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "font_bold.ttf"));
    }
}
