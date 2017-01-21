package sobmad.com.gamingreminder.Main;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import sobmad.com.gamingreminder.Database.MyDBHandler;
import sobmad.com.gamingreminder.Database.MyDBVideos;
import sobmad.com.gamingreminder.GamePage.DataWrapper;
import sobmad.com.gamingreminder.GamePage.PageActivity;
import sobmad.com.gamingreminder.GamePage.Video;
import sobmad.com.gamingreminder.HTTP.VolleySingleton;
import sobmad.com.gamingreminder.Notification.AlarmManagerBroadcastReceiver;
import sobmad.com.gamingreminder.R;

/**
 * Created by user on 27/01/2016.
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder>{

    private LayoutInflater inflater;
    private ArrayList<VideoGame> data;
    private ImageLoader mImageLoader;
    private Context mContext;

     // DB
    private MyDBVideos mMyDBVideos;
    private MyDBHandler mMyDBHandler;

    // If ever user wants to change reminder date (The date he set to get reminded for a game he hyped)
    private ReminderDialog mReminderDialog;

    // Layout
    private String theme;
    private double fontSize = 10;

    // Confirm Dialog
    private Dialog dialog;

    // Alarms : Usages here: To cancel alarm
    private AlarmManagerBroadcastReceiver mNotification;

    // CONSTRUCTOR
    public MyRecyclerViewAdapter(Context context, ArrayList<VideoGame> data){
        inflater = LayoutInflater.from(context);
        mMyDBVideos = new MyDBVideos(context, "", null, 0);
        mMyDBHandler = new MyDBHandler(context, "", null, 0);
        mImageLoader = VolleySingleton.getInstance().getImageLoader();
        this.data = data;
        this.mContext = context;
        mNotification = new AlarmManagerBroadcastReceiver(context);

        // Initialize ReminderDialog
        mReminderDialog = new ReminderDialog(mContext, this);

        // Theme
        // Retrieve theme
        SharedPreferences prefs = mContext.getSharedPreferences("appTheme", Context.MODE_PRIVATE);
        theme = prefs.getString("theme", "dark"); // Default is dark


        // Retrieve font Size
        SharedPreferences prefsFontSize = mContext.getSharedPreferences("fontSizePref", Context.MODE_PRIVATE);
        String size = prefsFontSize.getString("fontSize", "");
        if (!size.isEmpty()) {
            fontSize = Double.parseDouble(size); // Default is set to 10
        }

    }


    // Confirm Dialog
    public void openConfirmDialog(String title, final int position, final boolean hasReminder, final int gameId){
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.requestWindowFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        dialog.setContentView(R.layout.dialog_confirm);

        Button cancelButton = (Button) dialog.findViewById(R.id.dialog_cancel);
        Button deleteButton = (Button) dialog.findViewById(R.id.dialog_set);
        TextView deleteText = (TextView) dialog.findViewById(R.id.delete_text);

        if (hasReminder) {
            deleteText.setText("Delete " + title + " for good? This will also delete its reminder");
        } else {
            deleteText.setText("Delete " + title + " for good? ");
        }

        // Cancel
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        // Delete
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cancels the alarm
                if (hasReminder){
                    mNotification.deleteReminder(gameId);
                }
                data.remove(position);
                mMyDBHandler.deleteGame(gameId);
                mMyDBVideos.deleteVideos(gameId);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
                dialog.cancel();
            }
        });

        dialog.show();
    }

    public Date stringToDate(String dateString){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public Date getTodaysDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        String dateString = formatter.format(date);
        return stringToDate(dateString);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.hype_game_card, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final VideoGame videoGame = data.get(position);
        final int gameId = videoGame.getId();
        final long alertTime = mMyDBHandler.getReminderTime(gameId);

        // Change font size
        holder.mTitle.setTextSize((float) fontSize);
        holder.mGenre.setTextSize((float) fontSize);
        holder.mReleaseDate.setTextSize((float) fontSize);
        holder.mToGamePage.setTextSize((float) fontSize);
        holder.mReminderText.setTextSize((float) fontSize);
        holder.mDeleteGame.setTextSize((float) fontSize);

        // SO if fontSize equals 12 // Stay in one line
        if (fontSize > 11.5){
            holder.mTitle.setMaxLines(1);
        }

        // Setting theme
        if (theme.equals("light")){
            holder.mLayout.setBackgroundColor(Color.DKGRAY);
            holder.mCardLayout.setBackgroundColor(Color.DKGRAY);
        } else {
            // DO Nothing theme is already dark
        }

        // Acts liek a tag for OnClickListener
        final int reminderStatus;

        String dateString = videoGame.getReleaseDate();
        Date gameDate = stringToDate(dateString);
        Date todayDate = getTodaysDate();

        // if Game has reminder / All games start by having reminders except for games that have alertTime equal to 0 and games newly released (Game Now Out)
        boolean hasReminder = true;

        // Check if game got delayed
        if (mMyDBHandler.gotDelayed(gameId)) {
            reminderStatus = 0; // Game got delayed
            holder.mReminderText.setText("Game got delayed, change reminder?");
            holder.mReminderText.setTextColor(Color.RED);
            // if alertTime doesn't equal 0 that means reminder has been set
        } else if (alertTime != 0) {
            reminderStatus = 1; // Game has reminder
            holder.mReminderText.setText("Reminder set for " + alertTimeToDate(alertTime));

            // If game has reminder but came out, then;
            if (gameDate.before(todayDate) || gameDate.equals(todayDate)) {
                holder.mReminderText.setText("Game Now Out");
                // Messes everything up holder.mReminderText.setTextColor(Color.GREEN);
            }
        } else if (gameDate.before(todayDate) || gameDate.equals(todayDate)){
            holder.mReminderText.setText("Game Now Out");
            // Messes everything up:: holder.mReminderText.setTextColor(Color.GREEN);
            reminderStatus = 3;
            // Games that got released also doesn't have a reminder, because in OnReceive the game's alertTime gets reset to 0
            hasReminder = false;
        }else {
            // Game has no reminder
            reminderStatus = 2;
            holder.mReminderText.setText("No Reminder set. Click to set Reminder");
            hasReminder = false;
        }


        // Set On Click listener with different tags
        // if (tag == 1) {then change reminder? or if no reminder set, set reminder?}

        // Set Reminder / New Reminder
        holder.mReminderText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reminderStatus == 0){
                    // Game got delayed
                    mReminderDialog.setTitle("SET New Reminder");
                    // Open Dialog
                    mReminderDialog.openDialog(gameId, true);

                } else if (reminderStatus == 1){
                    // Game has already a reminder set
                    mReminderDialog.setTitle("Change Reminder");
                    mReminderDialog.openDialog(gameId, true);

                } else if (reminderStatus == 2){
                    // Game has no reminder
                    mReminderDialog.setTitle("SET Reminder");
                    mReminderDialog.openDialog(gameId, false);
                } else if (reminderStatus == 3){
                    // Do nothing
                }

                // If new Reminder set / Update (This is only an illusion up top code won't work
                // Ex: deleting reminder and clicking on title; the dialog will have "Change Reminder" and not "Set Reminder" as title


                // Open ReminderDialog for this game with its ID

            }
        });


        // Delete Game
        // Has reminder Temp variable
        final boolean finalHasReminder = hasReminder;

        holder.mDeleteGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove from ArrayList data first
                openConfirmDialog(videoGame.getTitle(), position, finalHasReminder, gameId);
            }
        });

        // To game page
        holder.mToGamePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Methods get JSON Themes (ArrayList String) and Genres (ArrayList String) from MyDBHandler and set them in the incomplete VideoGame instance
                // Method getAllVideos() get all Videos (ArrayList Video) from Video Database with game's id
                videoGame.setScreenshots(getScreenshots(videoGame));
                videoGame.setGenres(getGenres(videoGame));
                videoGame.setThemes(getThemes(videoGame));
                videoGame.setVideos(mMyDBVideos.getAllVideos(gameId));

                Intent intent = new Intent(v.getContext(), PageActivity.class);
                intent.putExtra("title", videoGame.getTitle());
                intent.putExtra("cover", videoGame.getCover());
                intent.putExtra("summary", videoGame.getSummary());
                intent.putExtra("releaseDate", videoGame.getReleaseDate());
                intent.putExtra("id", gameId);
                intent.putExtra("platforms", videoGame.getPlatforms());
                intent.putExtra("images", videoGame.getScreenshots()); // get screenshots url
                intent.putExtra("genres", videoGame.getGenres());
                intent.putExtra("themes", videoGame.getThemes());
                intent.putExtra("mainGenre", videoGame.getGameGenre());
                intent.putExtra("videos", new DataWrapper(videoGame.getVideos()));
                v.getContext().startActivity(intent);
            }
        });

        // Set Game's Info

        String title = videoGame.getTitle();
        // For database
        String genre = videoGame.getMainGenre();
        String releaseDate = videoGame.getReleaseDate();

        holder.mTitle.setText(title);
        holder.mGenre.setText(genre);
        holder.mReleaseDate.setText(releaseDate);

        mImageLoader.get(videoGame.getCover(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                holder.mCoverView.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                // Error
            }
        });

    }

    // Convert milliseconds to time format
    private String alertTimeToDate(long alertTime) {

        String releaseDate = "2016-12-31";

        Date date = new Date(alertTime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        releaseDate = formatter.format(date);

        return releaseDate;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    // Drag And Drop - http://androidessence.com/drag-and-drop-recyclerview-items/
    public void swap(int firstPosition, int secondPosition){
        Collections.swap(data, firstPosition, secondPosition);

        // SWAP IN SQL LITE DATABASE
        // Finds the position of each entries to switch them together
        int gameID1 = data.get(firstPosition).getId();
        int gameID2 = data.get(secondPosition).getId();

        // Re Arranging rows with COLUMN ID
        int firstGameOrderID = mMyDBHandler.getOrderID(gameID1);
        int secondGameOrderID = mMyDBHandler.getOrderID(gameID2);

        // Now we have our two games, let's swap positions

        // Toast.makeText(mContext, firstGameOrderID + "", Toast.LENGTH_LONG).show();
        // Toast.makeText(mContext, secondGameOrderID + "", Toast.LENGTH_LONG).show();

        // With the first updateOrder(); you're inserting same value for a column which is declared unique (typical primary key column)
        try {
            // So to not have two UNIQUE values that are the same: first set one as -1
            mMyDBHandler.updateOrder(gameID1, -1);

            // So This game gets to have the gameID1's position (The first game's COLUMN ID):
            mMyDBHandler.updateOrder(gameID2, firstGameOrderID);

            // But then for the first game; set it, don't keep it at -1, set it as the second game's GameID
            mMyDBHandler.updateOrder(gameID1, secondGameOrderID);

        } catch (Exception e){
            Toast.makeText(mContext, "Error Moving", Toast.LENGTH_LONG).show();
        }

        // At the end order rows by ID (DESCENDANT) 0 - 1 - 2 - 3 [...]
        mMyDBHandler.reArragneRows();
        notifyItemMoved(firstPosition, secondPosition);

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle;
        TextView mGenre;
        TextView mReleaseDate;
        ImageView mCoverView;
        RelativeLayout mLayout;
        RelativeLayout mCardLayout;
        TextView mReminderText;
        TextView mToGamePage;
        TextView mDeleteGame;
        // Delete?
        RelativeLayout llExpandArea;
        public MyViewHolder(View itemView) {
            super(itemView);
            mLayout = (RelativeLayout) itemView.findViewById(R.id.main_layout);
            mCardLayout = (RelativeLayout) itemView.findViewById(R.id.card_layout);
            mTitle = (TextView) itemView.findViewById(R.id.gameTitle);
            mGenre = (TextView) itemView.findViewById(R.id.genreView);
            mReleaseDate = (TextView) itemView.findViewById(R.id.releaseDate);
            mCoverView = (ImageView) itemView.findViewById(R.id.coverview);
            mReminderText = (TextView) itemView.findViewById(R.id.textViewReminder);
            mToGamePage = (TextView) itemView.findViewById(R.id.to_game_page);
            mDeleteGame = (TextView) itemView.findViewById(R.id.delete_game);
        }
    }

    public ArrayList<String> getGenres(VideoGame videoGame){

        ArrayList<String> genres = new ArrayList<>();

        if (videoGame.getGenresJSON() != null || !videoGame.getGenresJSON().isEmpty()) {
            try {
                JSONObject genresJSON = new JSONObject(videoGame.getGenresJSON());
                JSONArray genresArray = genresJSON.optJSONArray("genresArray");
                for (int j = 0; j < genresArray.length(); j++) {
                    String genre = genresArray.getString(j);
                    genres.add(genre);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return genres;
    }

    public ArrayList<String> getThemes(VideoGame videoGame){

        ArrayList<String> themes = new ArrayList<>();

        if (videoGame.getThemesJSON() != null || !videoGame.getThemesJSON() .isEmpty()) {
            try {
                JSONObject themesJSON = new JSONObject(videoGame.getThemesJSON() );
                JSONArray themesArray = themesJSON.optJSONArray("themesArray");
                for (int j = 0; j < themesArray.length(); j++) {
                    String genre = themesArray.getString(j);
                    themes.add(genre);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return themes;
    }

    public ArrayList<String> getScreenshots(VideoGame videoGame){

        ArrayList<String> images = new ArrayList<>();

        if (videoGame.getScreenshotsJSON() != null || !videoGame.getScreenshotsJSON().isEmpty()) {
            try {
                JSONObject imagesJSON = new JSONObject(videoGame.getScreenshotsJSON());
                JSONArray imagesArray = imagesJSON.optJSONArray("imagesArray");
                for (int j = 0; j < imagesArray.length(); j++) {
                    String image = imagesArray.getString(j);
                    images.add(image);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return images;
    }



}
