package kln.android.shapedetection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
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

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private final int REQ_CODE_PICK_IMAGE = 1;
    private final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private CoordinatorLayout mBaseActivityLayout = null;

    private static boolean sOpenCVAvailable = true;

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

    private FloatingActionButton mActionButton;
    /**
     * Number of tabs available
     */
    private final int NUM_TAB_VIEWS = 4;

    private Uri mBaseImageUri = null;

    private enum FratmentTabPosition {
        BASE_IMAGE_FRAGMENT,
        BLUR_IMAGE_FRAGMENT,
        CANNY_EDGE_FRAGMENT,
        CONTOURS_FRAGMENT
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBaseActivityLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermissions()) {
                    launchImagePicker();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case REQ_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    mBaseImageUri = imageReturnedIntent.getData();
                    start();
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchImagePicker();
                } else {
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
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mBaseLoaderCallback);
    }

    private BaseLoaderCallback mBaseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    sOpenCVAvailable = true;
                    Log.i(TAG, "OpenCV successfully loaded!");
                    break;
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

    private String getPathFromUri(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        return filePath;
    }

    private void start() {
        if (!sOpenCVAvailable) {
            Snackbar.make(mBaseActivityLayout, "OpenCV not available", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            start();
                        }
                    }).show();
            return;
        }
        Thread cvThread = new Thread(new OpenCvRunnable(getPathFromUri(mBaseImageUri)));
        cvThread.start();
    }

    private void launchImagePicker() {

        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQ_CODE_PICK_IMAGE);
    }

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

    public void showFragmentTab(final Object fragmentInstance) {

        if (fragmentInstance instanceof BaseImageFragment) {
            if (mViewPager.getCurrentItem() != 0){
                mViewPager.setCurrentItem(0, true);
            }
            return;
        }
        if (fragmentInstance instanceof BlurImageFragment) {
            if (mViewPager.getCurrentItem() != 1) {
                mViewPager.setCurrentItem(1, true);
            }
            return;
        }
        if (fragmentInstance instanceof CannyEdgesFragment) {
            if (mViewPager.getCurrentItem() != 2) {
                mViewPager.setCurrentItem(2, true);
            }
            return;
        }
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
