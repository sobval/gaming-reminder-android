package sobmad.com.gamingreminder.GamePage;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import sobmad.com.gamingreminder.Database.Game;
import sobmad.com.gamingreminder.Database.MyDBHandler;
import sobmad.com.gamingreminder.Database.MyDBVideos;
import sobmad.com.gamingreminder.Database.VideoDB;
import sobmad.com.gamingreminder.HTTP.VolleySingleton;
import sobmad.com.gamingreminder.Main.ReminderDialog;
import sobmad.com.gamingreminder.Main.UpcomingFragment;
import sobmad.com.gamingreminder.Main.VideoGame;
import sobmad.com.gamingreminder.Notification.AlarmManagerBroadcastReceiver;
import sobmad.com.gamingreminder.R;
import sobmad.com.gamingreminder.Settings.SettingsActivityApp;


public class PageActivity extends ActionBarActivity {

    private ArrayList<String> images = new ArrayList<>();

    // UI
    private LinearLayout mVideoLayout;
    private RecyclerView mTagLayout;
    private ViewPager mViewPager;
    private TextView mDescriptionView;
    private TextView mCreditsView;
    private TextView mReleaseDateTxtView;
    private ScrollView mScrollView;

    // Floating Action Button Credits: https://github.com/Clans/FloatingActionButton
    private FloatingActionButton favoriteGame;
    private FloatingActionButton tweetGame;

    // Adapters
    private CustomSwipeAdapter mCustomSwipeAdapter;
    private VideosAdapter mVideosAdapter;
    private TagsAdapter mTagsAdapter;

    // Arrays parsed
    private ArrayList<Video> videosList = new ArrayList<>();

    // Booleans
    private boolean isPressed = false;

    //delete
    private static VolleySingleton mVolleySingleton;
    private RequestQueue mRequestQueue;

    // Databases
    private MyDBHandler mMyDBHandler;
    private MyDBVideos mMyDBVideos;

    // Class to setReminders
    private ReminderDialog mReminderDialog;

    // For DatePicker
    int year = 0, month = 0, day = 0;

    // ActionBar Color
    ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FF0099CC"));

    public PageActivity(){}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Action bar's color
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        // Change Status Bar's Color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#FF0099CC"));
        }

        // Initialize the Databases
        mMyDBHandler = new MyDBHandler(this, "", null, 0);
        mMyDBVideos = new MyDBVideos(this, "", null, 0);

        // The Floating Action Buttons
        favoriteGame = (FloatingActionButton) findViewById( R.id.follow_item);
        tweetGame = (FloatingActionButton) findViewById(R.id.tweet_item);

        Intent intent = getIntent();
        final int id = intent.getIntExtra("id", 0);

        // We Talking about the game with this following ID
        // Initialize Reminder Class
        mReminderDialog = new ReminderDialog(this); // If ever we want to set the game a reminder

        final String title = intent.getStringExtra("title");
        String summary = intent.getStringExtra("summary");
        String cover = intent.getStringExtra("cover");
        String mainGenre = intent.getStringExtra("mainGenre");
        String releaseDate = intent.getStringExtra("releaseDate");

        //Store these;
        ArrayList<String> images = intent.getStringArrayListExtra("images");
        DataWrapper dw = (DataWrapper) getIntent().getSerializableExtra("videos");
        ArrayList<Video> videos = new ArrayList<>();

        try {
            // Store in Database
            videos = dw.getVideosList();
        } catch (Exception e){
            // Pass
        }

        // For the tags / Store in Database
        ArrayList<String> genres = intent.getStringArrayListExtra("genres");
        ArrayList<String> themes = intent.getStringArrayListExtra("themes");

        // If Game exists in Database then isPressed = true;
        if (mMyDBHandler.exists(id)){
            //Toast.makeText(this, title + " Exists", Toast.LENGTH_LONG).show();
            isPressed = true;
            favoriteGame.setImageResource(R.mipmap.ic_star_selected);
        }


        // I do this cause if user decides to add the game in database
        JSONObject jsonImages = new JSONObject();
        JSONObject jsonGenres = new JSONObject();
        JSONObject jsonThemes = new JSONObject();

        try {
            jsonImages.put("imagesArray", new JSONArray(images));
            jsonGenres.put("genresArray", new JSONArray(genres));
            jsonThemes.put("themesArray", new JSONArray(themes));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String imagesArray = jsonImages.toString();
        String genresArray = jsonGenres.toString();
        String themesArray = jsonThemes.toString();


        // Check what platform the user picked and store it with the game Ex; Uncharted 4 for PS4
        SharedPreferences prefs = getSharedPreferences("favoritePlatform", Context.MODE_PRIVATE);
        String platform = prefs.getString("name", "PC"); // Default is PC

        final Game game = new Game(id, title, releaseDate, summary, cover, mainGenre, imagesArray, genresArray, themesArray, platform);

        // Remove duplicates
        genres.removeAll(themes);
        // Merge the ArrayList
        genres.addAll(themes);


        final ArrayList<Video> finalVideos = videos;


        // FavoriteGame && TweetGame
        favoriteGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If button already pressed
                if (isPressed) {
                    //Toast.makeText(v.getContext(), "Button Unpressed", Toast.LENGTH_LONG).show();
                    isPressed = !isPressed; // isPressed = false;
                    mMyDBHandler.deleteGame(id);
                    mMyDBVideos.deleteVideos(id);
                    favoriteGame.setImageResource(R.mipmap.ic_star);
                } else {
                    //Toast.makeText(v.getContext(), "Button pressed", Toast.LENGTH_LONG).show();
                    isPressed = !isPressed; // isPressed = true;
                    mMyDBHandler.addGame(game);
                    setVideosInDB(finalVideos, id);
                    favoriteGame.setImageResource(R.mipmap.ic_star_selected);

                    // Open ReminderDialog for this game with its ID
                    mReminderDialog.openDialog(id, false);
                }
            }
        });

        // Tweet
        tweetGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Tweet Tweet", Toast.LENGTH_LONG).show();
                TweetComposer.Builder builder = new TweetComposer.Builder(v.getContext())
                        .text("I just Hyped " + title + " via " + "@Gaming_Reminder");
                builder.show();

            }
        });


        mVideoLayout = (LinearLayout) findViewById(R.id.videosLayout);
        mTagLayout = (RecyclerView) findViewById(R.id.tagLayoutRecyclerView);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mCreditsView = (TextView) findViewById(R.id.creditsTextView);
        mReleaseDateTxtView = (TextView) findViewById(R.id.releaseDateTxtView);
        mScrollView = (ScrollView) findViewById(R.id.scroll_view);

        // Reset ScrollView back on top
        mScrollView.smoothScrollTo(0, 0);

        // To IGDB.com
        mCreditsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.igdb.com/";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });


        mDescriptionView = (TextView) findViewById(R.id.descriptionView);
        mDescriptionView.setMovementMethod(new ScrollingMovementMethod());
        mVolleySingleton = VolleySingleton.getInstance();
        mRequestQueue = mVolleySingleton.getRequestQueue();


        // Set the Game's data in the UI
        this.setTitle(title);
        mDescriptionView.setText(summary);
        mReleaseDateTxtView.setText(releaseDate);

        // ViewPager Swipe Images
        mCustomSwipeAdapter = new CustomSwipeAdapter(this, images);
        mViewPager.setAdapter(mCustomSwipeAdapter);

        mCustomSwipeAdapter.notifyDataSetChanged();

        // Check Orientation
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){

        }
        else{
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, 630);

            layoutParams.setMargins(300, 0, 300, 0); // left, top, right, bottom
            mViewPager.setLayoutParams(layoutParams);
        }


        // Videos
        mVideosAdapter = new VideosAdapter(this, videos);

        int adapterCount = mVideosAdapter.getCount();

        // Add videos in RelativeLayout
        for (int i = 0; i < adapterCount; i++) {
            View item = mVideosAdapter.getView(i, null, null);
            mVideoLayout.addView(item);
        }

        mVideosAdapter.notifyDataSetChanged();

        // TAGS of THIS game (gameID)
        mTagsAdapter = new TagsAdapter(this, genres, id);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mTagLayout.setLayoutManager(layoutManager);
        mTagLayout.setAdapter(mTagsAdapter);

        mTagsAdapter.notifyDataSetChanged();
    }

    // Set Videos in Array
    public void setVideosInDB(ArrayList<Video> videos, int gameID){
        if (videos != null) {
            for (int i = 0; i < videos.size(); i++) {
                mMyDBVideos.addVideo(new VideoDB(gameID, videos.get(i).getTitle(), videos.get(i).getVideoID(), videos.get(i).getThumbnailURL()));
            }
        }

    }

    public void deleteGameFromDatabase(int id){
        mMyDBHandler.deleteGame(id);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivityApp.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


}
