package sobmad.com.gamingreminder.Main;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import sobmad.com.gamingreminder.Database.MyDBHandler;
import sobmad.com.gamingreminder.GamePage.Video;
import sobmad.com.gamingreminder.HTTP.VolleySingleton;
import sobmad.com.gamingreminder.R;
import sobmad.com.gamingreminder.Settings.Platform;
import sobmad.com.gamingreminder.Settings.PlatformsAdapter;
import sobmad.com.gamingreminder.Settings.SettingsActivityApp;


public class UpcomingFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // UI
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingActionButton;

    // Adapters
    private RecyclerViewAdapter mRecyclerViewAdapter;

    // Store games data
    private ArrayList<VideoGame> mUpcomingGamesList;

    // Sections
    private int januaryGames = 0, februaryGames = 0, marsGames = 0, aprilGames = 0, mayGames = 0, juneGames = 0, julyGames = 0,
            augustGames = 0, septemberGames = 0, octoberGames = 0, novemberGames = 0, decemberGames = 0, comingSoon = 0;

    private ArrayList<Integer> nbGamesByMonth = new ArrayList<>();

    // DB
    private MyDBHandler mMyDBHandler;

    // Refresh
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // Request
    private RequestQueue mRequestQueue;

    // Filter Dialog
    private static Dialog mFilterDialog;
    private ArrayList<Platform> platforms;

    // Progress Dialog
    private ProgressDialog mProgressDialog;

    private boolean requestError = false;
    private boolean firstTime;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpcomingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpcomingFragment newInstance(String param1, String param2) {
        UpcomingFragment fragment = new UpcomingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public UpcomingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
        mMyDBHandler = new MyDBHandler(getActivity(), "", null, 0);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);
        // UI
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.floatingButton);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.update_data);
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage("Loading...");

        SharedPreferences prefs = getContext().getSharedPreferences("appTheme", Context.MODE_PRIVATE);
        String theme = prefs.getString("theme", "dark"); // Default is dark

        if (theme.equals("light")){
            mRecyclerView.setBackgroundColor(Color.GRAY);
        } // ELSE DO Nothing; theme is already dark

        SharedPreferences firstTimePref = getActivity().getSharedPreferences("FirstTime", Context.MODE_PRIVATE);
        firstTime = firstTimePref.getBoolean("firstTime", true); // Default true / It gets set to false first time it calls sendJSONRequest()
        if (firstTime) {
            // if true / requestError only true if sendJSONRequest has an error
            if (requestError){
                // Toast.makeText(getContext(), "First time failed, I'll honor you", Toast.LENGTH_SHORT).show();
                initializeUI();
            } else {
                // Toast.makeText(getContext(), "First", Toast.LENGTH_SHORT).show();
                sendJSONRequest(); // True means first time running sendJSONRequest
            }
        } else {
            initializeUI();
        }
        // ELSE
        //Toast.makeText(getContext(), "Not first", Toast.LENGTH_SHORT).show();


        return view;
    }

    public void initializeUI(){
        // Our fragment will take care of loading the data
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // if null at default json stored in asset // only the first time in order to give a first time user a taste of the app if JSON is null
        if (requestError && firstTime){
            requestError = false;
            // firstTime not set to false, cuz i want it to update when the json is good
            mUpcomingGamesList = parseJSONResponse(loadJSONFromAsset());
        } else {
            mUpcomingGamesList = parseJSONResponse(loadJSON());
        }

        mRecyclerViewAdapter = new RecyclerViewAdapter(getContext(), mUpcomingGamesList);
        //Apply this adapter to the RecyclerView
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setHasFixedSize(true);

        // Check Orientation for GridLayoutManager
        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        }
        else{
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        }

        // mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        //This is the code to provide a sectioned grid
        List<SectionedGridRecyclerViewAdapter.Section> sections =
                new ArrayList<SectionedGridRecyclerViewAdapter.Section>();

        // Getting the year
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);

        // Remove the current game if it already released from the iterator and the list
        for (Iterator<VideoGame> iterator = mUpcomingGamesList.iterator(); iterator.hasNext();) {
            VideoGame game = iterator.next();
            if (game.getYear() < thisYear) {
                iterator.remove();
            }
        }

        for (int i = 0; i < mUpcomingGamesList.size(); i++) {
            // if Releases next year
            if (mUpcomingGamesList.get(i).getYear() > thisYear) {
                comingSoon++;
                // if Releases this year
            } else if (mUpcomingGamesList.get(i).getMonth() != -1 && mUpcomingGamesList.get(i).getYear() == thisYear) {
                checkMonth(mUpcomingGamesList.get(i).getMonth());
            }
        }

        // Add in list
        nbGamesByMonth.add(januaryGames);
        nbGamesByMonth.add(februaryGames);
        nbGamesByMonth.add(marsGames);
        nbGamesByMonth.add(aprilGames);
        nbGamesByMonth.add(mayGames);
        nbGamesByMonth.add(juneGames);
        nbGamesByMonth.add(julyGames);
        nbGamesByMonth.add(augustGames);
        nbGamesByMonth.add(septemberGames);
        nbGamesByMonth.add(octoberGames);
        nbGamesByMonth.add(novemberGames);
        nbGamesByMonth.add(decemberGames);
        nbGamesByMonth.add(comingSoon);


        int position = 0;
        // Iterates 13 times
        for (int i = 0; i < nbGamesByMonth.size(); i++){
            // Has games coming out this month and creates the sections
            if (nbGamesByMonth.get(i) != 0 ){
                sections.add(new SectionedGridRecyclerViewAdapter.Section(position , getMonthString(i)));
                position += nbGamesByMonth.get(i);
            }
        }

        // Add your adapter to the sectionAdapter
        SectionedGridRecyclerViewAdapter.Section[] dummy = new SectionedGridRecyclerViewAdapter.Section[sections.size()];
        SectionedGridRecyclerViewAdapter mSectionedAdapter = new
                SectionedGridRecyclerViewAdapter(getActivity(),R.layout.section,R.id.section_text, mRecyclerView, mRecyclerViewAdapter);
        mSectionedAdapter.setSections(sections.toArray(dummy));

        //Apply this adapter to the RecyclerView
        mRecyclerView.setAdapter(mSectionedAdapter);

        // Platforms
        initializePlatforms();

        // Filter Button
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilterDialog();
            }
        });

    }

    // adds +1 to how many games comes out the month pass in parameter
    public void checkMonth(int month){
        switch (month){
            case 0:
                januaryGames++;
                break;
            case 1:
                februaryGames++;
                break;
            case 2:
                marsGames++;
                break;
            case 3:
                aprilGames++;
                break;
            case 4:
                mayGames++;
                break;
            case 5:
                juneGames++;
                break;
            case 6:
                julyGames++;
                break;
            case 7:
                augustGames++;
                break;
            case 8:
                septemberGames++;
                break;
            case 9:
                octoberGames++;
                break;
            case 10:
                novemberGames++;
                break;
            case 11:
                decemberGames++;
                break;
        }
    }

    public String getMonthString(int month){
        String monthString = "";
        switch (month){
            case 0:
                monthString = "January";
                break;
            case 1:
                monthString = "February";
                break;
            case 2:
                monthString = "Mars";
                break;
            case 3:
                monthString = "April";
                break;
            case 4:
                monthString = "May";
                break;
            case 5:
                monthString = "June";
                break;
            case 6:
                monthString = "July";
                break;
            case 7:
                monthString = "August";
                break;
            case 8:
                monthString = "September";
                break;
            case 9:
                monthString = "October";
                break;
            case 10:
                monthString = "November";
                break;
            case 11:
                monthString = "December";
                break;
            case 12:
                monthString = "Coming the Next Years";
                break;
        }
        return monthString;
    }


    public String loadJSONFromAsset() {
        String json = null;
        try {
            // InputStream inputStream = getActivity().openFileInput("upcomingGamesJSON.json");
            InputStream is = getActivity().getAssets().open("upcomingGamesJSON.json");

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

    public void sendJSONRequest(){
        mProgressDialog.show();
        final SharedPreferences.Editor editor = getContext().getSharedPreferences("FirstTime", Context.MODE_PRIVATE).edit();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "http://vps70293.vps.ovh.ca/videogames/video_games.json",
                (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getContext(), "Requesting...", Toast.LENGTH_SHORT).show();
                        writeToFile(response.toString());
                        //Toast.makeText(getContext(), "Reading...", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                        editor.putBoolean("firstTime", false); // firstTime set to false
                        editor.commit();
                        refreshFragment();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error Fetching New Data. Please Try Again Later", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
                if (firstTime) {
                    requestError = true;
                    refreshFragment(); // Restart to get the default JSON for first time users / firstTime still true
                }
            }
        });

        mRequestQueue.add(jsonObjectRequest);


    }

    // Write JSON
    private void writeToFile(String jsonData) {
        try {
            //Toast.makeText(getContext(), "Writing...", Toast.LENGTH_SHORT).show();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getContext().openFileOutput("upcomingGamesJSON.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(jsonData);
            outputStreamWriter.close();
            //Toast.makeText(getContext(), "Done Writing", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    // Read JSON
    private String loadJSON() {

        String ret = "";

        try {
            InputStream inputStream = getContext().openFileInput("upcomingGamesJSON.json");

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
            Toast.makeText(getContext(), "File not found", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Can not read JSON", Toast.LENGTH_SHORT).show();
        }

        return ret;
    }

    public void openFilterDialog(){
        mFilterDialog = new Dialog(getContext());
        mFilterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mFilterDialog.requestWindowFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        // Animation
        // mFilterDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        mFilterDialog.setContentView(R.layout.dialog_filter);

        ListView platformsListView = (ListView) mFilterDialog.findViewById(R.id.platform_list_view);
        Button cancelButton = (Button) mFilterDialog.findViewById(R.id.dialog_cancel);
        // Adapter
        PlatformsAdapter platformsAdapter = new PlatformsAdapter(getContext(), platforms, this);

        platformsListView.setAdapter(platformsAdapter);
        mFilterDialog.show();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFilterDialog.cancel();
            }
        });
    }

    public static final Dialog getFilterDialog(){
        return mFilterDialog;
    }


    // Add platforms to ArrayList
    public void initializePlatforms(){
        platforms = new ArrayList<>();
        // Tag is the name of the JSON Array
        Platform Windows = new Platform("Windows", "Windows");
        Platform PS4 = new Platform("Playstation 4", "PS4");
        Platform PS3 = new Platform("Playstation 3", "PS3");
        Platform XboxOne = new Platform("Xbox One", "XboxOne");
        Platform Xbox360 = new Platform("Xbox 360", "Xbox 360"); // There's a space between Xbox and 360
        Platform WiiU = new Platform("Wii U", "WiiU");
        Platform D3S = new Platform("3DS", "3DS");

        platforms.add(Windows);
        platforms.add(PS3);
        platforms.add(PS4);
        platforms.add(Xbox360);
        platforms.add(XboxOne);
        platforms.add(WiiU);
        platforms.add(D3S);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // https://www.youtube.com/watch?v=qIhwPaa6rlU&index=43&list=PLonJJ3BVjZW6CtAMbJz1XD8ELUs1KXaTD
    }

    public ArrayList<VideoGame> parseJSONResponse(String json){

        ArrayList<VideoGame> gamesList = new ArrayList<>();

        if (!json.isEmpty() && json != null){
            try {

                // Retrieve Screenshot Quality
                // First Check if screenshotQuality exists

                // For deleted Screenshot Quality Settings
                // SharedPreferences prefsQuality = getActivity().getSharedPreferences("sreenshotQuality", Context.MODE_PRIVATE);
                // String quality = prefsQuality.getString("quality", "Medium");
                String imageTag = "t_screenshot_huge";

                /*
                if (quality.equals("Big")){
                    imageTag = "t_screenshot_big";
                } else if (quality.equals("Huge")){
                    imageTag = "t_screenshot_huge";
                } else {
                    // Else Quality set to medium
                    imageTag = "t_screenshot_med";
                }
                */

                // Retrieve Cover Quality
                // First Check if screenshotQuality exists
                SharedPreferences prefsCoverQuality = getActivity().getSharedPreferences("coverQuality", Context.MODE_PRIVATE);
                String coverQuality = prefsCoverQuality.getString("quality", "Big");
                String coverTag = "";

                if (coverQuality.equals("Small")){
                    coverTag = "t_cover_small_2x";
                } else if (coverQuality.equals("Big2X")){
                    coverTag = "t_cover_big_2x";
                } else {
                    // Else Quality set to Big
                    coverTag = "t_cover_big";
                }

                // Check what platform the user picked and return its upcoming games
                //String platform = SharedPrefHelper.getString("platform", act);
                SharedPreferences prefs = getContext().getSharedPreferences("favoritePlatform", Context.MODE_PRIVATE);
                String platform = prefs.getString("name", "Windows"); // Default is Windows

                // Maybe a method that changes the theme of the app changeToPlatformTheme(String platform) if (platform == "PC")

                JSONObject upcomingGames = new JSONObject(json);

                if (!upcomingGames.isNull(platform) && upcomingGames.has(platform)) {
                    JSONArray platformsArray = upcomingGames.getJSONArray(platform);

                    for(int i = 0; i < platformsArray.length(); i++){

                        VideoGame videoGame = new VideoGame();

                        JSONObject GameObject = platformsArray.getJSONObject(i);

                        String title = "";
                        if (GameObject.has("name") && !GameObject.isNull("name")){
                            title = GameObject.getString("name");
                        }

                        String summary = "This is a cool description";
                        if (GameObject.has("summary") && !GameObject.isNull("summary")){
                            summary = GameObject.getString("summary");
                        }

                        int id = 0;
                        if (GameObject.has("id") && !GameObject.isNull("id")){
                            id = GameObject.getInt("id");
                        }

                        String releaseDate = "TBA";
                        if (GameObject.has("release_date") && !GameObject.isNull("release_date")){
                            releaseDate = GameObject.getString("release_date");
                            if (mMyDBHandler.exists(id)){
                                mMyDBHandler.updateReleaseDate(releaseDate, id);
                            }
                        }

                        ArrayList<String> platforms = new ArrayList<>();
                        if (GameObject.has("release_dates") && !GameObject.isNull("release_dates")){
                            JSONArray releasesArray = GameObject.getJSONArray("release_dates");
                            for (int x = 0; x < releasesArray.length(); x++){
                                JSONObject releaseObject = releasesArray.getJSONObject(x);
                                String platformName = releaseObject.getString("platform_name");
                                platforms.add(platformName);
                            }
                        }

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

                        String coverID = "";
                        String coverURL = ""; // Cover Big (t_cover_big)
                        if (GameObject.has("cover") && !GameObject.isNull("cover")){
                            JSONObject coverObject = GameObject.getJSONObject("cover");
                            coverID = coverObject.getString("id");
                            coverURL = "http://res.cloudinary.com/igdb/image/upload/"+coverTag+"/" + coverID + ".png";
                        }

                        ArrayList<Video> videosList = new ArrayList<>();
                        if (GameObject.has("videos") && !GameObject.isNull("videos")) {
                            JSONArray videosArray = GameObject.getJSONArray("videos");
                            for (int y = 0; y < videosArray.length(); y++){
                                JSONObject videoObj = videosArray.getJSONObject(y);
                                String videoTitle = videoObj.getString("title");
                                String videoId = videoObj.getString("uid");
                                String thumbnailURL = "http://img.youtube.com/vi/"+videoId+"/0.jpg";
                                Video video = new Video(videoTitle, videoId, thumbnailURL);
                                videosList.add(video);
                            }
                        }

                        // Screenshots
                        ArrayList<String> images = new ArrayList<>();
                        ArrayList<String> imagesTablet = new ArrayList<>();
                        if (GameObject.has("screenshots") && !GameObject.isNull("screenshots")) {
                            JSONArray screenShotsArray = GameObject.getJSONArray("screenshots");
                            for (int y = 0; y < screenShotsArray.length(); y++){
                                JSONObject screenshot = screenShotsArray.getJSONObject(y);

                                // Game Screenshots have IDS
                                String screenshotID = screenshot.getString("id");
                                String screenshotURL = "http://res.cloudinary.com/igdb/image/upload/"+imageTag+"/"+screenshotID+".jpg";
                                images.add(screenshotURL);

                                // For tablets or landscape view
                                /*
                                String screenshotID = screenshot.getString("id");
                                screenshotURL = "http://res.cloudinary.com/igdb/image/upload/t_screenshot_big/"+screenshotID+".jpg";
                                imagesTablet.add(screenshotURL);
                                */
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

                    } // End of for loop

                } // End of if

            } catch (JSONException e){
                // Error
            }
        }

        return gamesList;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void refreshFragment(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
        Toast.makeText(getContext(), "Fragment Updated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        if (isNetworkAvailable()) {
            sendJSONRequest();
            mSwipeRefreshLayout.setRefreshing(false);
        } else {
            Toast.makeText(getContext(), "No network available", Toast.LENGTH_LONG).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
