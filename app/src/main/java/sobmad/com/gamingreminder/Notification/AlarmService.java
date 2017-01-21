package sobmad.com.gamingreminder.Notification;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import sobmad.com.gamingreminder.Database.MyDBHandler;
import sobmad.com.gamingreminder.Main.VideoGame;

/**
 * Created by user on 26/03/2016.
 */
public class AlarmService extends IntentService {

    private MyDBHandler mMyDBHandler;
    private static final String TAG = AlarmManagerBroadcastReceiver.class.getSimpleName();

    public AlarmService() {
        super("AlarmService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    public void initializeAlarms(){
        ArrayList<VideoGame> hypedVideoGames = mMyDBHandler.getAllHypedGames();
        for (VideoGame videoGame: hypedVideoGames){
            int gameID = videoGame.getId();
            AlarmManagerBroadcastReceiver.setAlarm(gameID, mMyDBHandler.getReminderTime(gameID));
        }
    }
}
