package kln.android.shapedetection.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import kln.android.shapedetection.MainActivity;
import kln.android.shapedetection.R;

/**
 * Fragment to host blurred image
 */
public class BlurImageFragment extends BaseFragment {

    private static final String TAG = BlurImageFragment.class.getSimpleName();

    /**
     * Reference to our instance
     */
    private static BlurImageFragment sIntance = null;

    // private constructor
    @SuppressLint("ValidFragment")
    private BlurImageFragment() {
        super();
    }

    /**
     * Provides the singleton instance of this fragment
     *
     * @return
     */
    public static BlurImageFragment getInstance() {
        if (sIntance == null) {
            sIntance = new BlurImageFragment();
        }
        return sIntance;
    }

    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // set our tab title
        setTitle(getString(R.string.fragment_title_blur_image));
    }

    /**
     * Displays this fragment on the view pager
     */
    @Override
    protected void show() {
        ((MainActivity) getActivity()).showFragmentTab(this);
    }
}
