package views;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.devspark.appmsg.AppMsg;
import com.rey.material.widget.ProgressView;

import net.smartinnovationtechnology.khotwh.R;

import utils.ViewUtil;

public abstract class ProgressFragment extends Fragment {
    // constants for view states
    protected static final int VIEW_STATE_MAIN = 1;
    protected static final int VIEW_STATE_PROGRESS = 2;
    protected static final int VIEW_STATE_ERROR = 3;
    protected static final int VIEW_STATE_EMPTY = 4;

    protected int viewState; // used to save current visible view state
    private Activity activity;

    // main views
    private View mainView;
    private View progressView;
    private View errorView;
    private View emptyView;

    // error view components
    private TextView textError;
    private ImageButton buttonRefresh;
    private String errorMsg;

    // empty view components
    private TextView textEmpty;
    private String emptyMsg;

    // progress view components
    private ProgressView progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutResource(), container, false);

        // init components
        activity = getActivity();
        mainView = rootView.findViewById(R.id.view_main);
        progressView = rootView.findViewById(R.id.view_progress);
        errorView = rootView.findViewById(R.id.view_error);
        emptyView = rootView.findViewById(R.id.view_empty);
        textError = (TextView) errorView.findViewById(R.id.text_error);
        buttonRefresh = (ImageButton) errorView.findViewById(R.id.button_refresh);
        textEmpty = (TextView) emptyView.findViewById(R.id.text_empty);
        progressBar = (ProgressView) progressView.findViewById(R.id.progressBar);

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });


        return rootView;
    }

    /**
     * abstract method to pass layout resource id from children
     */
    protected abstract int getLayoutResource();

    /*
    * method used to show main viewState
    */
    protected void showMain() {
        // hide error viewState if it is visible
        ViewUtil.fadeView(errorView, false);
        // hide empty viewState if it is visible
        ViewUtil.fadeView(emptyView, false);
        // hide all AppMsgs
        AppMsg.cancelAll(activity);

        // hide progress viewState if it is visible
        ViewUtil.fadeView(progressView, false);

        // show main viewState
        ViewUtil.fadeView(mainView, true);
        // update viewState state
        viewState = VIEW_STATE_MAIN;
    }

    /*
     * method used to show progress if it is possible
     */
    protected void showProgress() {
        // check to ensure main view is not visible
        if (viewState != VIEW_STATE_MAIN) {
            // not visible, so hide all views
            ViewUtil.fadeView(mainView, false);
            ViewUtil.fadeView(errorView, false);
            ViewUtil.fadeView(emptyView, false);
            // and show progress view
            progressBar.start();
            ViewUtil.fadeView(progressView, true);

            // update view state
            viewState = VIEW_STATE_PROGRESS;
        } else {
            // main view is visible >> hide all AppMsgs
            AppMsg.cancelAll(activity);
        }
    }

    /*
     * overloaded method used to show error with default msg
     */
    protected void showError() {
        errorMsg = getString(R.string.error_loading_data);
        // set error text
        textError.setText(errorMsg);

        // show the suitable error style
        showTheError();
    }

    /*
     * overloaded method used to show error with String msg
     */
    protected void showError(String errorMsg) {
        this.errorMsg = errorMsg;
        // set error text
        textError.setText(errorMsg);

        // show the suitable error style
        showTheError();
    }

    /*
     * overloaded method used to show error with msg resource id
     */
    protected void showError(int errorMsgResource) {
        errorMsg = getString(errorMsgResource);
        // set error text
        textError.setText(errorMsgResource);

        // show the suitable error style
        showTheError();
    }

    /*
     * method used to show the suitable error msg
     */
    protected void showTheError() {
        // check if main view is visible
        if (viewState == VIEW_STATE_MAIN) {
            // visible and hide all other AppMsgs
            AppMsg.cancelAll(activity);
            // and just show error in AppMsg
            AppMsg appMsg = AppMsg.makeText(activity, errorMsg, AppMsg.STYLE_CONFIRM);
            appMsg.setParent((ViewGroup) mainView);
            appMsg.show();
        } else {
            // main view is not visible, so hide all views
            ViewUtil.fadeView(mainView, false);
            ViewUtil.fadeView(progressView, false);
            ViewUtil.fadeView(emptyView, false);

            // and show error view
            ViewUtil.fadeView(errorView, true);

            // update view state
            viewState = VIEW_STATE_ERROR;
        }
    }

    /*
     * overloaded method used to show empty with default msg
     */
    protected void showEmpty() {
        emptyMsg = getString(R.string.no_data_found);
        // set empty text
        textEmpty.setText(emptyMsg);

        // show the suitable empty style
        showTheEmpty();
    }

    /*
     * overloaded method used to show empty with String msg
     */
    protected void showEmpty(String emptyMsg) {
        this.emptyMsg = emptyMsg;
        // set empty text
        textEmpty.setText(emptyMsg);

        // show the suitable empty style
        showTheEmpty();
    }

    /*
     * overloaded method used to show empty with msg resource id
     */
    protected void showEmpty(int emptyMsgResource) {
        emptyMsg = getString(emptyMsgResource);
        // set empty text
        textEmpty.setText(emptyMsgResource);

        // show the suitable empty style
        showTheEmpty();
    }

    /*
     * method used to show the suitable empty msg
     */
    private void showTheEmpty() {
        // hide all views
        ViewUtil.fadeView(mainView, false);
        ViewUtil.fadeView(progressView, false);
        ViewUtil.fadeView(errorView, false);

        // and show empty view
        ViewUtil.fadeView(emptyView, true);

        // update view state
        viewState = VIEW_STATE_EMPTY;
    }

    /**
     * abstract method to override slide_in children to do refresh operation
     */
    protected abstract void onRefresh();
}
