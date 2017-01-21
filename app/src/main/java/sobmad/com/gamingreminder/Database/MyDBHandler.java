package sobmad.com.gamingreminder.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

import sobmad.com.gamingreminder.GamePage.Video;
import sobmad.com.gamingreminder.Main.VideoGame;

/**
 * Created by user on 17/01/2016.
 */
public class MyDBHandler extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 21;
    public static final String DATABASE_NAME = "hypedgames.db";
    public static final String TABLE_GAMES = "games"; // Table videos 2?
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_GAMEID = "gameID";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_RELEASEDATE = "releaseDate";
    public static final String COLUMN_SUMMARY = "summary";
    public static final String COLUMN_COVER = "cover";
    public static final String COLUMN_MAINGENRE = "mainGenre";
    public static final String COLUMN_IMAGES = "images";
    public static final String COLUMN_GENRES = "genres";
    public static final String COLUMN_THEMES = "themes";
    public static final String COLUMN_DELAYED = "delayed";
    public static final String COLUMN_ALERTTIME = "alerttime";
    public static final String COLUMN_PLATFORM = "platform";

    // Context Delete
    private Context mContext;

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_GAMES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_GAMEID + " INTEGER, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_RELEASEDATE + " TEXT, " +
                COLUMN_SUMMARY + " TEXT, " +
                COLUMN_COVER + " TEXT, " +
                COLUMN_MAINGENRE + " TEXT, " +
                COLUMN_IMAGES + " TEXT, " +
                COLUMN_GENRES + " TEXT, " +
                COLUMN_THEMES + " TEXT," +
                COLUMN_DELAYED + " INTEGER," +
                COLUMN_ALERTTIME + " INTEGER," +
                COLUMN_PLATFORM + " TEXT " +
                ")";

        try {
            db.execSQL(query);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);
        onCreate(db);
    }

    // Add a new row to the database
    public void addGame(Game game){
        ContentValues values = new ContentValues();
        values.put(COLUMN_GAMEID, game.get_gameID());
        values.put(COLUMN_TITLE, game.get_title());
        values.put(COLUMN_RELEASEDATE, game.get_releaseDate());
        values.put(COLUMN_SUMMARY, game.get_summary());
        values.put(COLUMN_COVER, game.get_cover());
        values.put(COLUMN_MAINGENRE, game.get_mainGenre());
        values.put(COLUMN_IMAGES, game.get_images());
        values.put(COLUMN_GENRES, game.get_genres());
        values.put(COLUMN_THEMES, game.get_themes());
        values.put(COLUMN_DELAYED, game.isDelayed()); // Default set to false. Only way to update it is with method below updateReleaseDate()
        values.put(COLUMN_ALERTTIME, game.get_alertTime()); // Default set to 0 (Means no alarm set for this game). Only way to update it is with method updateAlertTime()
        values.put(COLUMN_PLATFORM, game.get_platform());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_GAMES, null, values);
        db.close();
    }

    // Get game by ID
    public VideoGame getVideoGameByID(int gameID){
        SQLiteDatabase db = getWritableDatabase();
        String Query = "Select * from " + TABLE_GAMES + " where " + COLUMN_GAMEID + " = " + gameID;
        Cursor cursor = db.rawQuery(Query, null);

        // Default / if returns null then the game wasn't found
        VideoGame game = null;

        try {
            while (cursor.moveToNext()) {

                game = new VideoGame();

                game.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_GAMEID)));

                game.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));

                game.setReleaseDate(cursor.getString(cursor.getColumnIndex(COLUMN_RELEASEDATE)));

                game.setSummary(cursor.getString(cursor.getColumnIndex(COLUMN_SUMMARY)));

                game.setCover(cursor.getString(cursor.getColumnIndex(COLUMN_COVER)));

                game.setScreenshotsJSON(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGES)));

                game.setMainGenre(cursor.getString(cursor.getColumnIndex(COLUMN_MAINGENRE)));

                game.setGenresJSON(cursor.getString(cursor.getColumnIndex(COLUMN_GENRES)));

                game.setThemesJSON(cursor.getString(cursor.getColumnIndex(COLUMN_THEMES)));

            }
        } finally {
            cursor.close();
        }

        return game;
    }

    // Delete a game from the database
    public boolean deleteGame(int gameID){
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_GAMES, COLUMN_GAMEID + "=" + gameID, null) > 0;
    }

    // Check if SQL entry exists
    public boolean exists(int gameID){
        SQLiteDatabase db = getWritableDatabase();
        String Query = "Select * from " + TABLE_GAMES + " where " + COLUMN_GAMEID + " = " + gameID;
        Cursor cursor = db.rawQuery(Query, null);

        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }

        cursor.close();
        return true;
    }

    // Return all data
    public ArrayList<VideoGame> getAllData() {

        ArrayList<VideoGame> games = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {COLUMN_ID, COLUMN_GAMEID, COLUMN_TITLE, COLUMN_RELEASEDATE, COLUMN_SUMMARY, COLUMN_COVER, COLUMN_MAINGENRE, COLUMN_IMAGES, COLUMN_GENRES, COLUMN_THEMES};
        Cursor cursor = db.query(TABLE_GAMES, columns, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                VideoGame game = new VideoGame();
                game.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_GAMEID)));

                game.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));

                game.setReleaseDate(cursor.getString(cursor.getColumnIndex(COLUMN_RELEASEDATE)));

                game.setSummary(cursor.getString(cursor.getColumnIndex(COLUMN_SUMMARY)));

                game.setCover(cursor.getString(cursor.getColumnIndex(COLUMN_COVER)));

                game.setScreenshotsJSON(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGES)));

                game.setMainGenre(cursor.getString(cursor.getColumnIndex(COLUMN_MAINGENRE)));

                game.setGenresJSON(cursor.getString(cursor.getColumnIndex(COLUMN_GENRES)));

                game.setThemesJSON(cursor.getString(cursor.getColumnIndex(COLUMN_THEMES)));

                games.add(game);

            } while (cursor.moveToNext());
        }

        return games;
    }

    public ArrayList<VideoGame> getAllHypedGames(){
        ArrayList<VideoGame> games = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        // Get games where alertTime doesn't equal 0 (They have reminder set)
        String Query = "SELECT * FROM " + TABLE_GAMES + " WHERE " + COLUMN_ALERTTIME + " != " + 0;
        Cursor cursor = db.rawQuery(Query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                VideoGame game = new VideoGame();
                game.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_GAMEID)));

                game.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));

                game.setReleaseDate(cursor.getString(cursor.getColumnIndex(COLUMN_RELEASEDATE)));

                game.setSummary(cursor.getString(cursor.getColumnIndex(COLUMN_SUMMARY)));

                game.setCover(cursor.getString(cursor.getColumnIndex(COLUMN_COVER)));

                game.setScreenshotsJSON(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGES)));

                game.setMainGenre(cursor.getString(cursor.getColumnIndex(COLUMN_MAINGENRE)));

                game.setGenresJSON(cursor.getString(cursor.getColumnIndex(COLUMN_GENRES)));

                game.setThemesJSON(cursor.getString(cursor.getColumnIndex(COLUMN_THEMES)));

                games.add(game);

            } while (cursor.moveToNext());
        }

        return games;
    }

    // if game has alertTime; that means it has alarm set; when alarm gets called, then for this game alertTime will equal 0
    public void updateAlertTime(long alertTime, int gameID){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ALERTTIME, alertTime);
        db.update(TABLE_GAMES, cv, "gameID=" + gameID, null); // Third parameter Where clause
    }

    // Each time when view is re-created parseJSONRespnse gets called and if it finds a game that exists in the Database in
    // the new JSON; it comes to this method and if the release date is a new one not the same as the one saved in the database for
    // the game in context. Then the game gets delayed and the old release date gets updated with the new
    public void updateReleaseDate(String releaseDate, int gameID){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        if (!releaseDate.equals(getReleaseDate(gameID))) {
            // Insert New Data
            cv.put(COLUMN_RELEASEDATE, releaseDate);
            cv.put(COLUMN_DELAYED, 1); // Game got delayed
            db.update(TABLE_GAMES, cv, "gameID=" + gameID, null); // Third parameter Where clause
        }
    }

    public String getReleaseDate(int gameID){
        SQLiteDatabase db = getWritableDatabase();
        String Query = "Select " + COLUMN_RELEASEDATE + " from " + TABLE_GAMES + " where " + COLUMN_GAMEID + " = " + gameID;
        Cursor cursor = db.rawQuery(Query, null);
        String releaseDate = "2016-11-31";
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    releaseDate = cursor.getString(cursor.getColumnIndex(COLUMN_RELEASEDATE));
                }
            } finally {
                cursor.close();
            }
        }

        return releaseDate;
    }

    // Check if game truly got delayed, if Column_Delayed equals 1, then game got delayed
    public boolean gotDelayed(int gameID){
        SQLiteDatabase db = getWritableDatabase();
        String Query = "Select " + COLUMN_DELAYED + " from " + TABLE_GAMES + " where " + COLUMN_GAMEID + " = " + gameID;
        Cursor cursor = db.rawQuery(Query, null);

        boolean delayed = false;

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    // if bigger than 0, it means game got delayed; 1 is true
                    delayed = cursor.getInt(cursor.getColumnIndex(COLUMN_DELAYED)) > 0;
                }
            } finally {
                cursor.close();
            }
        }

        return delayed;
    }

    // Get if game has reminder
    public long getReminderTime(int gameID){
        SQLiteDatabase db = getWritableDatabase();
        String Query = "Select " + COLUMN_ALERTTIME + " from " + TABLE_GAMES + " where " + COLUMN_GAMEID + " = " + gameID;
        Cursor cursor = db.rawQuery(Query, null);

        long alertTime = 0;

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    // if  0 than no reminder set
                    alertTime = cursor.getLong(cursor.getColumnIndex(COLUMN_ALERTTIME));
                }
            } finally {
                cursor.close();
            }
        }

        return alertTime;
    }

    // Drag and Drop / New positions saved
    public void reArragneRows(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(TABLE_GAMES, null, null, null, null, null, COLUMN_ID + " DESC"); // Order by COLUMN_ID
    }

    // Auto Column ID Updated
    // GameID is the game to be swapped by the other game
    public void updateOrder(int gameID, int position){
        SQLiteDatabase db = getWritableDatabase();
        // Updating Order ID ...
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, position);
        db.update(TABLE_GAMES, cv, "gameID=" + gameID, null); // Third parameter Where clause
    }

    public int getOrderID(int gameID){
        SQLiteDatabase db = getWritableDatabase();
        String Query = "Select " + COLUMN_ID + " from " + TABLE_GAMES + " where " + COLUMN_GAMEID + " = " + gameID;
        Cursor cursor = db.rawQuery(Query, null);
        int orderPosition = 0;
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    // if  0 than no reminder set
                    orderPosition = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                }
            } finally {
                cursor.close();
            }
        }

        return orderPosition;
    }


    public String getPlatfom(int gameID){
        SQLiteDatabase db = getWritableDatabase();
        String Query = "Select " + COLUMN_PLATFORM + " from " + TABLE_GAMES + " where " + COLUMN_GAMEID + " = " + gameID;
        Cursor cursor = db.rawQuery(Query, null);
        String platform = ""; // Default Empty See (TagsFilterActivity parseJSONResponse for why) / Because if game isn't in DB and this method returns empty then get platform from sharedPrefrences
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    // if  0 than no reminder set
                    platform = cursor.getString(cursor.getColumnIndex(COLUMN_PLATFORM));
                }
            } finally {
                cursor.close();
            }
        }

        return platform;
    }

}
