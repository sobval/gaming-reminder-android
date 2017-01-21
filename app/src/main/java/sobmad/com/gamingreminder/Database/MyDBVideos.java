package sobmad.com.gamingreminder.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import sobmad.com.gamingreminder.GamePage.Video;
import sobmad.com.gamingreminder.Main.VideoGame;

/**
 * Created by user on 24/01/2016.
 */
public class MyDBVideos extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = "videos.db";
    public static final String TABLE_VIDEOS = "videos";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_GAMEID = "gameID";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_VIDEOID = "videoID";
    public static final String COLUMN_THUMBURL = "thumbnailURL";

    public MyDBVideos(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_VIDEOS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_GAMEID + " INTEGER, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_THUMBURL + " TEXT, " +
                COLUMN_VIDEOID + " TEXT " +
                ")";

        try {
            db.execSQL(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEOS);
        onCreate(db);
    }

    // Add a new row to the database
    public void addVideo(VideoDB videoDB){
        ContentValues values = new ContentValues();
        values.put(COLUMN_GAMEID, videoDB.get_gameID());
        values.put(COLUMN_TITLE, videoDB.get_title());
        values.put(COLUMN_THUMBURL, videoDB.get_thumbnailURL());
        values.put(COLUMN_VIDEOID, videoDB.get_videoID());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_VIDEOS, null, values);
        db.close();
    }

    // Delete all videos entry from the database for a game, with its gameID
    public boolean deleteVideos(int gameID){
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_VIDEOS, COLUMN_GAMEID + "=" + gameID, null) > 0;
    }

    public ArrayList<Video> getAllVideos(int gameID){
        ArrayList<Video> videos = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String IDGame = String.valueOf(gameID);
        String selectQuery = "SELECT * FROM " + TABLE_VIDEOS + " WHERE "
                + COLUMN_GAMEID + " = ?";

        String[] args={IDGame};
        Cursor cursor = db.rawQuery(selectQuery, args);

        // Move to first row
        try {
            while (cursor.moveToNext()) {
                Video video = new Video();

                video.setVideoID(cursor.getString(cursor.getColumnIndex(COLUMN_VIDEOID)));

                video.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));

                video.setThumbnailURL(cursor.getString(cursor.getColumnIndex(COLUMN_THUMBURL)));

                videos.add(video);
            }
        } finally {
            cursor.close();;
        }

        return videos;
    }


}
