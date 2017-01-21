package sobmad.com.gamingreminder.GamePage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import sobmad.com.gamingreminder.Database.MyDBHandler;
import sobmad.com.gamingreminder.Main.RecyclerViewAdapter;
import sobmad.com.gamingreminder.Main.VideoGame;
import sobmad.com.gamingreminder.R;
import sobmad.com.gamingreminder.Settings.SettingsActivityApp;

public class TagsFilterActivity extends ActionBarActivity {

    // UI
    private RecyclerView mRecyclerView;
    private TextView mTextView;

    // Data
    private ArrayList<VideoGame> mUpcomingGamesList;

    // Adapter
    private RecyclerViewAdapter mRecyclerViewAdapter;

    // ActionBar Color
    ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FF0099CC"));

    // Game ID
    private int gameID;

    //DB
    private MyDBHandler mMyDBHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags_filter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Action bar's color
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        // Change Status Bar's Color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#FF0099CC"));
        }

        // DB
        mMyDBHandler = new MyDBHandler(this, "", null, 0);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerviewTag);
        mTextView = (TextView) findViewById(R.id.noGamesText);

        mTextView.setVisibility(View.INVISIBLE);


        // Theme
        // Retrieve theme
        SharedPreferences prefs = getSharedPreferences("appTheme", Context.MODE_PRIVATE);
        String theme = prefs.getString("theme", "dark"); // Default is dark

        if (theme.equals("light")){
            mRecyclerView.setBackgroundColor(Color.GRAY);
        } else {
            // DO Nothing theme is already dark
        }

        // Get the search tag to filter The data
        Intent intent = getIntent();
        gameID = intent.getIntExtra("gameID", 0);


        SharedPreferences firstTimePref = getSharedPreferences("FirstTime", Context.MODE_PRIVATE);
        boolean firstTime = firstTimePref.getBoolean("firstTime", true); // Default true

        // To check if user doesn't have JSON written in cache, he has only the Default asset JSON
        if (firstTime) {
            mUpcomingGamesList = parseJSONResponse(loadJSONFromAsset(), intent.getStringExtra("tagText"));
        } else {
            mUpcomingGamesList = parseJSONResponse(loadJSON(), intent.getStringExtra("tagText"));
        }
        mRecyclerViewAdapter = new RecyclerViewAdapter(this, mUpcomingGamesList);


        // Apply this adapter to the RecyclerView
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setHasFixedSize(true);

        // Check Orientation for GridLayoutManager
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        }
        else{
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        }

        // No Data found / Recyclerview is empty
        if (mRecyclerViewAdapter.getItemCount() == 0){
            mTextView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tags_filter, menu);
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

    // Be sure to load from Cache

    // Read JSON
    private String loadJSON() {

        String ret = "";

        try {
            InputStream inputStream = openFileInput("upcomingGamesJSON.json");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Can not read JSON", Toast.LENGTH_SHORT).show();
        }

        return ret;
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            // InputStream inputStream = getActivity().openFileInput("upcomingGamesJSON.json");
            InputStream is = getAssets().open("upcomingGamesJSON.json");

            if (is != null) {

                int size = is.available();

                byte[] buffer = new byte[size];

                is.read(buffer);

                is.close();

                json = new String(buffer, "UTF-8");
            }


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return json; // if it returns null / ArrayList of Video Games (mUpcomingList) empty / So no Errors
    }


    /*
     - This Class gets the JSON Data from the Cache, gets the shared preference platform
     - Action Games for the Wii U for Example and returns the games
     */

    public ArrayList<VideoGame> parseJSONResponse(String json, String tagFilter){

        ArrayList<VideoGame> gamesList = new ArrayList<>();

        if (!json.isEmpty() && json != null){
            try {


                // Get from database
                String platform = mMyDBHandler.getPlatfom(gameID);

                // Game not in DataBase
                if (platform.isEmpty()) {
                    SharedPreferences prefs = getSharedPreferences("favoritePlatform", Context.MODE_PRIVATE);
                    platform = prefs.getString("name", "PC"); // Default is PC
                }

                // Upcoming Action games for the Wii U
                this.setTitle("Upcoming " + tagFilter + " Games for the  " + platform);

                JSONObject upcomingGames = new JSONObject(json);

                if (!upcomingGames.isNull(platform) && upcomingGames.has(platform)) {
                    JSONArray platformsArray = upcomingGames.getJSONArray(platform);

                    for(int i = 0; i < platformsArray.length(); i++){

                        VideoGame videoGame = new VideoGame();

                        JSONObject GameObject = platformsArray.getJSONObject(i);

                        ArrayList<String> themes = new ArrayList<>();
                        if (GameObject.has("themes") && !GameObject.isNull("themes")){
                            JSONArray themesArray = GameObject.getJSONArray("themes");
                            for (int x = 0; x < themesArray.length(); x++){
                                JSONObject themeObject = themesArray.getJSONObject(x);
                                String theme = themeObject.getString("name");
                                themes.add(theme);
                            }
                        }

                        ArrayList<String> genres = new ArrayList<>();
                        if (GameObject.has("genres") && !GameObject.isNull("genres")){
                            JSONArray genresArray = GameObject.getJSONArray("genres");
                            for (int x = 0; x < genresArray.length(); x++){
                                JSONObject genreObject = genresArray.getJSONObject(x);
                                String genre = genreObject.getString("name");
                                genres.add(genre);
                            }
                        }

                        if (themes.contains(tagFilter) || genres.contains(tagFilter)) {

                            String title = "";
                            if (GameObject.has("name") && !GameObject.isNull("name")) {
                                title = GameObject.getString("name");
                            }

                            String summary = "This is a cool description";
                            if (GameObject.has("summary") && !GameObject.isNull("summary")) {
                                summary = GameObject.getString("summary");
                            }

                            int id = 0;
                            if (GameObject.has("id") && !GameObject.isNull("id")) {
                                id = GameObject.getInt("id");
                            }

                            String releaseDate = "TBA";
                            if (GameObject.has("release_date") && !GameObject.isNull("release_date")) {
                                releaseDate = GameObject.getString("release_date");
                            }

                            ArrayList<String> platforms = new ArrayList<>();
                            if (GameObject.has("release_dates") && !GameObject.isNull("release_dates")) {
                                JSONArray releasesArray = GameObject.getJSONArray("release_dates");
                                for (int x = 0; x < releasesArray.length(); x++) {
                                    JSONObject releaseObject = releasesArray.getJSONObject(x);
                                    String platformName = releaseObject.getString("platform_name");
                                    platforms.add(platformName);
                                }
                            }

                            String coverID = "";
                            String coverURL = ""; // Cover Big (t_cover_big)
                            if (GameObject.has("cover") && !GameObject.isNull("cover")) {
                                JSONObject coverObject = GameObject.getJSONObject("cover");
                                coverID = coverObject.getString("id");
                                coverURL = "http://res.cloudinary.com/igdb/image/upload/t_cover_big/" + coverID + ".png";
                            }

                            ArrayList<Video> videosList = new ArrayList<>();
                            if (GameObject.has("videos") && !GameObject.isNull("videos")) {
                                JSONArray videosArray = GameObject.getJSONArray("videos");
                                for (int y = 0; y < videosArray.length(); y++) {
                                    JSONObject videoObj = videosArray.getJSONObject(y);
                                    String videoTitle = videoObj.getString("title");
                                    String videoId = videoObj.getString("uid");
                                    String thumbnailURL = "http://img.youtube.com/vi/" + videoId + "/0.jpg";
                                    Video video = new Video(videoTitle, videoId, thumbnailURL);
                                    videosList.add(video);
                                }
                            }

                            ArrayList<String> images = new ArrayList<>();
                            ArrayList<String> imagesTablet = new ArrayList<>();
                            if (GameObject.has("screenshots") && !GameObject.isNull("screenshots")) {
                                JSONArray screenShotsArray = GameObject.getJSONArray("screenshots");
                                for (int y = 0; y < screenShotsArray.length(); y++) {
                                    JSONObject screenshot = screenShotsArray.getJSONObject(y);
                                    String screenshotURL = "http:" + screenshot.getString("url");
                                    images.add(screenshotURL);

                                    // For tablets or landscape view
                                    String screenshotID = screenshot.getString("id");
                                    screenshotURL = "http://res.cloudinary.com/igdb/image/upload/t_screenshot_big/" + screenshotID + ".jpg";
                                    imagesTablet.add(screenshotURL);
                                }
                            }

                            videoGame.setTitle(title);
                            videoGame.setSummary(summary);
                            videoGame.setId(id);
                            videoGame.setReleaseDate(releaseDate);
                            videoGame.setPlatforms(platforms);
                            videoGame.setThemes(themes);
                            videoGame.setGenres(genres);
                            videoGame.setCoverID(coverID);
                            videoGame.setCover(coverURL);
                            videoGame.setVideos(videosList);
                            videoGame.setScreenshots(images);
                            videoGame.setScreenshotsTablet(imagesTablet);

                            gamesList.add(videoGame);
                        }

                    // Else don't do nothing if Tag isn't found in the Game

                    } // End of for loop

                } // End of if

            } catch (JSONException e){
                // Error
            }
        }

        return gamesList;
    }

}
