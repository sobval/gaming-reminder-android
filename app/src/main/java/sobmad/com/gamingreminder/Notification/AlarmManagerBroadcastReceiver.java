package sobmad.com.gamingreminder.Notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import sobmad.com.gamingreminder.Database.MyDBHandler;
import sobmad.com.gamingreminder.GamePage.PageActivity;
import sobmad.com.gamingreminder.Main.MainActivity;
import sobmad.com.gamingreminder.Main.VideoGame;
import sobmad.com.gamingreminder.R;

/**
 * Created by user on 09/02/2016.
 */
public class AlarmManagerBroadcastReceiver extends BroadcastReceiver{

    private static final String TAG = AlarmManagerBroadcastReceiver.class.getSimpleName();

    private static Context mContext;
    // Database
    private static MyDBHandler mMyDBHandler;


    public AlarmManagerBroadcastReceiver(){}

    public AlarmManagerBroadcastReceiver(Context context){
        this.mContext = context;
        // Database only gets used when user sets reminder (gets initialized)
        mMyDBHandler = new MyDBHandler(context, "", null, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        // App closed (?) createNotifications uses the DB
        if (mMyDBHandler == null){
            mMyDBHandler = new MyDBHandler(context, "", null, 0);
        }

        // Persistent alarms ? Initialize alarms when user closes device

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            //Toast.makeText(context, "Rebooting Alarms", Toast.LENGTH_LONG).show();
            // Starting the Service / Handles asynchronous requests on demand
            //Intent serviceIntent = new Intent(context, AlarmService.class);
            //context.startService(serviceIntent);

            try {
                MyDBHandler mMyDBHandler = new MyDBHandler(context, "", null, 0);
                ArrayList<VideoGame> hypedVideoGames = mMyDBHandler.getAllHypedGames();


                // Restart alarms
                for (VideoGame videoGame: hypedVideoGames){

                    // AlarmManagerBroadcastReceiver.setAlarm(gameID, mMyDBHandler.getReminderTime(gameID));

                    if (videoGame != null) {

                        int gameID = videoGame.getId();

                        // Make sure to set long in PageActivity for the game or If in Boot Activated get The long time (get the alert time)
                        //Toast.makeText(context, "Alarm set for " + videoGame.getTitle(), Toast.LENGTH_SHORT).show();

                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                        // onReceive()
                        Intent intentAlarm = new Intent(context, AlarmManagerBroadcastReceiver.class);
                        intentAlarm.putExtra("id", gameID);
                        intentAlarm.putExtra("title", videoGame.getTitle());
                        intentAlarm.putExtra("releaseDate", videoGame.getReleaseDate());

                        // Flag indicating that if the described PendingIntent already exists, then keep it but replace its extra data with what is in this new Intent.
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, gameID, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

                        alarmManager.set(AlarmManager.RTC_WAKEUP, mMyDBHandler.getReminderTime(gameID), pendingIntent);

                    } else {
                        Toast.makeText(context, "Error setting reminder", Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (Exception e){

                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            //Toast.makeText(context, "Fart3", Toast.LENGTH_LONG).show();

        } else {
            Log.d(TAG, "Reboot Game");
            int id = intent.getIntExtra("id", 0);
            String title = intent.getStringExtra("title");
            String releaseDate = intent.getStringExtra("releaseDate");

            // Alarm got called
            mMyDBHandler.updateAlertTime(0, id);

            String message = "Will release on the " + releaseDate;
            // CreateNotification() / needs onReceive()'s context
            createNotification(context, message, title, id);
        }
    }

    // Takes Game's ID (GamePage)/ The long value is passed from Dialog (ReminderDialog)
    public static void setAlarm(int id, long alertTime){

        //Toast.makeText(mContext,alertTime + "", Toast.LENGTH_SHORT).show();

        VideoGame videoGame = mMyDBHandler.getVideoGameByID(id);

        // if DB finds the video game we're talking about
        if (videoGame != null) {
            // Make sure to set long in PageActivity for the game
            Toast.makeText(mContext,"Alarm set for " + videoGame.getTitle(), Toast.LENGTH_SHORT).show();
            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

            // onReceive()
            Intent intent = new Intent(mContext, AlarmManagerBroadcastReceiver.class);
            intent.putExtra("id", id);
            intent.putExtra("title", videoGame.getTitle());
            intent.putExtra("releaseDate", videoGame.getReleaseDate());

            // Flag indicating that if the described PendingIntent already exists, then keep it but replace its extra data with what is in this new Intent.
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime, pendingIntent);

        } else {
            Toast.makeText(mContext, "Error setting reminder", Toast.LENGTH_SHORT).show();
        }

    }


    private void createNotification(Context context, String msgAlert, String title, int id){

        // Define an Intent and an action to perform with it by another application
        Intent intent = new Intent (context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent notificIntent = PendingIntent.getActivity(context, 0, intent, 0);
        int color = 0xff0099cc;
        // Builds a notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.selection)
                        .setColor(color)
                        .setContentTitle(title)
                        .setTicker(msgAlert)
                        .setContentText(msgAlert);

        // Defines the Intent to fire when the notification is clicked
        mBuilder.setContentIntent(notificIntent);

        // Set the default notification option (Add option in settings in shared preference)
        // DEFAULT_SOUND : Make sound
        // DEFAULT_VIBRATE : Vibrate
        // DEFAULT_LIGHTS : Use the default light notification
        mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);

        // Auto cancels the notification when clicked on in the task bar
        mBuilder.setAutoCancel(true);

        // Gets a NotificationManager which is used to notify the user of the background event
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Post the notification
        mNotificationManager.notify(id, mBuilder.build());
    }

    public void deleteReminder(int id){
        Intent intent = new Intent(mContext, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(mContext, id, intent, 0);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(mContext.ALARM_SERVICE);
        // Alarm Cancelled:
        alarmManager.cancel(sender);
    }


}
