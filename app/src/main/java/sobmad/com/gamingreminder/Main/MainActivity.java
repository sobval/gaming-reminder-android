package sobmad.com.gamingreminder.Main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import java.io.IOException;
import java.io.InputStream;

import sobmad.com.gamingreminder.R;
import sobmad.com.gamingreminder.Settings.SettingsActivityApp;


public class MainActivity extends ActionBarActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "w6b4i4uUJRAw0zZaGtbQCAg1a";
    private static final String TWITTER_SECRET = "lynhzhQa9zxkzmqo61sI7mwWlaWRnr9fJD3iigsnsE2eBZlPld";


    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;

    public static ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FF0099CC"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Twitter SDK
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Action Bar's Color
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        // Change Status Bar's Color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#FF0099CC"));
        }

        // first time
        SharedPreferences firstTimePref = getSharedPreferences("FirstBoot", Context.MODE_PRIVATE);
        boolean first = firstTimePref.getBoolean("first", true); // default true at first boot
        // we gonna change it to false in the AppIntroActivity when the users clicks on the last button of the tutorial activity

        if (first) {
            Intent intent = new Intent(this, AppIntroActivity.class);
            startActivity(intent);
        }

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

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivityApp.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);


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
            Fragment fragment = null;
            switch (position){
                case 0:
                    fragment = UpcomingFragment.newInstance("","");
                    break;
                case 1:
                    fragment = HypedFragment.newInstance("","");
                    break;
            }
            return fragment;

        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

    }

}
