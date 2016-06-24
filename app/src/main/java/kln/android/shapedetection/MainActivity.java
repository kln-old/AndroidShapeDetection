package kln.android.shapedetection;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import kln.android.shapedetection.fragments.BaseImageFragment;
import kln.android.shapedetection.fragments.BlurImageFragment;
import kln.android.shapedetection.fragments.CannyEdgesFragment;
import kln.android.shapedetection.fragments.ContoursFragment;

/**
 * Main Activity for our Applications
 */
public class MainActivity extends AppCompatActivity {


    public static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Request codes to handle on activity result
     */
    private final int REQ_CODE_PICK_IMAGE = 1;

    /**
     * Request code to handle on permission result
     */
    private final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    /**
     * Flag to notify if OpenCV libs are available on the device
     */
    private static boolean sOpenCVAvailable = true;

    /**
     * Base layout for the activity
     */
    private CoordinatorLayout mBaseActivityLayout = null;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    /**
     * The floating action buttion to perform several actions
     */
    private FloatingActionButton mActionButton;

    /**
     * Number of tabs/pages available in our {@link #mViewPager}
     */
    private final int NUM_TAB_VIEWS = 4;

    /**
     * URI of our original image picked by the user
     */
    private Uri mBaseImageUri = null;

    /**
     * Inidialize our object on activity created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get & set app's toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bind our base layer
        mBaseActivityLayout = (CoordinatorLayout) findViewById(R.id.main_content);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Get & bind our action button with corresponding action
        mActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermissions()) {
                    launchImagePicker();
                }
            }
        });

        // set default values to our preferences settings
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }


    /**
     * Show options menu when options btn is clicked
     * @param menu menu generated from our menu_main.xml
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Handle menu options selected/clicked
     * @param item menu item clicked
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // start our preferences settings activity
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handle start activity return result
     * @param requestCode request code we had provided to startActivity()
     * @param resultCode
     * @param imageReturnedIntent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case REQ_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    // extract image Uri
                    mBaseImageUri = imageReturnedIntent.getData();
                    // start our analysis
                    start();
                }
        }
    }

    /**
     * Handle permission request results
     * @param requestCode request code provided requestPermission()
     * @param permissions granted permission details
     * @param grantResults granted permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // request grated to access & write to external storage. start our actions
                    launchImagePicker();
                } else {
                    // notify permission denial & setup retry
                    Snackbar.make(mBaseActivityLayout, "Permission Denied.", Snackbar.LENGTH_LONG)
                            .setAction("Request", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    checkPermissions();
                                }
                            }).show();
                }
                return;
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        // check if opencv is available every time the app is refreshed/resumed
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mBaseLoaderCallback);
    }

    /**
     * Callback to handle opencv loader
     */
    private BaseLoaderCallback mBaseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                // OpenCV available
                case LoaderCallbackInterface.SUCCESS:
                    sOpenCVAvailable = true;
                    Log.i(TAG, "OpenCV successfully loaded!");
                    break;
                // all other failure cases
                case LoaderCallbackInterface.INCOMPATIBLE_MANAGER_VERSION:
                case LoaderCallbackInterface.INIT_FAILED:
                case LoaderCallbackInterface.INSTALL_CANCELED:
                case LoaderCallbackInterface.MARKET_ERROR:
                    sOpenCVAvailable = false;
                    Log.i(TAG, "OpenCV Loader error code = " + status);
                default:
                    super.onManagerConnected(status);
            }
        }
    };

    /**
     * Converts a Uri to string file path
     * @param uri uri to convert
     * @return a string of file path in Uri
     */
    private String getPathFromUri(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        return filePath;
    }

    /**
     * Start our opencv analysis.
     */
    private void start() {
        // check if opencv is available
        if (!sOpenCVAvailable) {
            // if not availalbe, notify user & setup retry
            Snackbar.make(mBaseActivityLayout, "OpenCV not available", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            start();
                        }
                    }).show();
            return;
        }
        // opencv is available & successfully loaded. Continue to analyze
        // Get our preferences
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final int blurKernelSize = Integer.parseInt(preferences.getString(getString(R.string.preference_key_blur_kernel),
                getString(R.string.preference_default_value_blur_kernel)));
        final boolean useOtsu = preferences.getBoolean(getString(R.string.preference_key_canny_otsu), true);
        final int cannyMinThreshold = Integer.parseInt(preferences.getString(getString(R.string.preference_key_canny_threshold_min),
                getString(R.string.preference_default_value_canny_threshold_min)));
        final int cannyMaxThreshold = Integer.parseInt(preferences.getString(getString(R.string.preference_key_canny_threshold_max),
                getString(R.string.preference_default_value_canny_threshold_max)));
        final int contourType = Integer.parseInt(preferences.getString(getString(R.string.preference_key_contours_type),
                getString(R.string.preference_entry_value_contours_type_external)));

        // instantiate our opencv runnable
        OpenCvRunnable cvRunnable = new OpenCvRunnable(getPathFromUri(mBaseImageUri), blurKernelSize, cannyMinThreshold, cannyMaxThreshold, useOtsu, contourType);
        // opencv actions are cpu demanding !!
        // so, run all opencv actions on a separate thread so that our main thread is not blocked.
        Thread cvThread = new Thread(cvRunnable);
        cvThread.start();
    }

    /**
     * Launches system image picker to select an image
     */
    private void launchImagePicker() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQ_CODE_PICK_IMAGE);
    }

    /**
     * Checks if we have required permission for our app
     * @return true if we have permissions, false otherwise
     */
    private boolean checkPermissions() {
        // permission to write to external storage
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            return false;
        }
        return true;
    }

    /**
     * Displays the requested fragment on our viewpager
     * @param fragmentInstance fragment to be displayed
     */
    public void showFragmentTab(final Object fragmentInstance) {

        // first tab is base image
        if (fragmentInstance instanceof BaseImageFragment) {
            if (mViewPager.getCurrentItem() != 0){
                mViewPager.setCurrentItem(0, true);
            }
            return;
        }
        // second tab is blurred image
        if (fragmentInstance instanceof BlurImageFragment) {
            if (mViewPager.getCurrentItem() != 1) {
                mViewPager.setCurrentItem(1, true);
            }
            return;
        }
        // third tab is edges detected
        if (fragmentInstance instanceof CannyEdgesFragment) {
            if (mViewPager.getCurrentItem() != 2) {
                mViewPager.setCurrentItem(2, true);
            }
            return;
        }
        // fourth tab is the contours detected
        if (fragmentInstance instanceof ContoursFragment) {
            if (mViewPager.getCurrentItem() != 3) {
                mViewPager.setCurrentItem(3, true);
            }
            return;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position) {
                case 0:
                    return BaseImageFragment.getInstance();
                case 1:
                    return BlurImageFragment.getInstance();
                case 2:
                    return CannyEdgesFragment.getInstance();
                case 3:
                    return ContoursFragment.getInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return NUM_TAB_VIEWS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.fragment_title_base_image);
                case 1:
                    return getString(R.string.fragment_title_blur_image);
                case 2:
                    return getString(R.string.fragment_title_canny_edges);
                case 3:
                    return getString(R.string.fragment_title_contours);
            }
            return null;
        }
    }
}
