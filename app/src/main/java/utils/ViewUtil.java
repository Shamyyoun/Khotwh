package utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

public class ViewUtil {
    public static final int DEFAULT_DURATION = 250;

    /**
     * method used to show or hide view in default duration
     */
    public static void fadeView(View view, boolean show) {
        fadeView(view, show, DEFAULT_DURATION, View.GONE);
    }

    /**
     * method used to show or hide view
     */
    public static void fadeView(View view, boolean show, int duration) {
        fadeView(view, show, duration, View.GONE);
    }

    /**
     * method used to show or hide view with INVISIBLE constant in default duration
     */
    public static void hideView(final View view, int invisibleConstant) {
        fadeView(view, false, DEFAULT_DURATION, invisibleConstant);
    }

    /**
     * method used to show or hide view with INVISIBLE constant
     */
    public static void hideView(final View view, int duration, int invisibleConstant) {
        fadeView(view, false, duration, invisibleConstant);
    }

    /**
     * method used to shows or hides a view with a smooth animation in specific duration
     */
    private static void fadeView(final View view, final boolean show, int duration, final int invisibleConstant) {
        if (view != null) {
            view.animate().setDuration(duration).alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            if (show)
                                view.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (!show)
                                view.setVisibility(invisibleConstant);
                        }
                    });
        }
    }
}
