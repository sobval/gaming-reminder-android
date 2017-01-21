package sobmad.com.gamingreminder.Main;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.RippleDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import sobmad.com.gamingreminder.Database.MyDBHandler;
import sobmad.com.gamingreminder.GamePage.PageActivity;
import sobmad.com.gamingreminder.Notification.AlarmManagerBroadcastReceiver;
import sobmad.com.gamingreminder.R;

/**
 * Created by user on 18/02/2016.
 */
public class ReminderDialog {

    private Context context;

    // Adapter to update
    private MyRecyclerViewAdapter mMyRecyclerViewAdapter;

    // Both get initialized in constructor, before the opening of the dialog
    // AlarmManager / setAlarm()
    private AlarmManagerBroadcastReceiver mNotification ;

    // DB
    private MyDBHandler mMyDBHandler;

    // Game Id
    private int gameID;

    // For Reminders; Each game will have different values dependant on their release date
    private int year = 0;
    private int month = 0;
    private int day = 0;

    // Reminder Dialog
    private Dialog dialog = null;

    private String title = "SET Reminder?";


    public ReminderDialog(Context context, MyRecyclerViewAdapter mMyRecyclerViewAdapter){
        this.context = context;
        mNotification = new AlarmManagerBroadcastReceiver(context);
        mMyDBHandler = new MyDBHandler(context, "", null, 0);
        this.mMyRecyclerViewAdapter = mMyRecyclerViewAdapter;
    }

    public ReminderDialog(Context context){
        this.context = context;
        mNotification = new AlarmManagerBroadcastReceiver(context);
        mMyDBHandler = new MyDBHandler(context, "", null, 0);
    }

    // canDelete is the equivalent of hasReminder
    public void openDialog(final int gameID, boolean canDelete) {
        // Global gameID for DatePicker
        this.gameID = gameID;

        // Convert String to Calender Object
        // First get the game's release date and then convert it to string
        stringToDate(mMyDBHandler.getReleaseDate(gameID));
        dialog = new Dialog(context);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.requestWindowFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        dialog.setContentView(R.layout.dialog_reminder);
        //dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        //Window window = dialog.getWindow();
        //window.setLayout(1400, 500);

        TextView titleView = (TextView) dialog.findViewById(R.id.dialog_title);
        Button cancelButton = (Button) dialog.findViewById(R.id.dialog_cancel);
        Button setButton = (Button) dialog.findViewById(R.id.dialog_set);

        titleView.setText(title);

        // Radio Buttons
        final RadioButton dayOfRelease = (RadioButton) dialog.findViewById(R.id.radio_release);
        final RadioButton weekBefore = (RadioButton) dialog.findViewById(R.id.radio_week);
        final RadioButton specificDate = (RadioButton) dialog.findViewById(R.id.radio_date);
        final RadioButton deleteReminder = (RadioButton) dialog.findViewById(R.id.delete_reminder);

        // if dialog gets open in a game's page
        if (canDelete){
            deleteReminder.setVisibility(View.VISIBLE);
        } else {
            deleteReminder.setVisibility(View.GONE);
        }

        // Cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        // set Alarms
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check radio button and does the math
                if (dayOfRelease.isChecked()){
                    long alertTime = getAlertTime(year, month, day, 1);
                    mNotification.setAlarm(gameID, alertTime);
                    // Store the long value in DB
                    mMyDBHandler.updateAlertTime(alertTime, gameID);
                    dialog.cancel();
                } else if (weekBefore.isChecked()){
                    // Id is 0 because week before
                    long alertTime =  getAlertTime(year, month, day, 0);
                    mNotification.setAlarm(gameID, alertTime);
                    // Store the long value in DB
                    mMyDBHandler.updateAlertTime(alertTime, gameID);
                    dialog.cancel();
                } else if (specificDate.isChecked()){
                    // DATE PICKER / Get Date / AlertTime gets (SET) inside the dialog's listener method
                    new DatePickerDialog(context, datePickerListener, year, month, day).show();
                } else if (deleteReminder.isChecked()){
                    // CHECK IF GAME IN HYPED LIST SO USER COULD DELETE REMINDER
                    // If CLICKED GAME REMINDER GOT DELETED
                    mMyDBHandler.updateAlertTime(0, gameID);
                    mNotification.deleteReminder(gameID);
                    dialog.cancel();
                }

                // Update If adapter is initialized / It's only initialized in MyRecyclerViewAdapter
                if (mMyRecyclerViewAdapter != null) {
                    mMyRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });

        dialog.show();
    }

    // Return alertTime
    public long getAlertTime(int year, int month, int day, int id){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);

        // Week Before Release
        if (id == 0){
            cal.add(Calendar.DAY_OF_YEAR, -7);
        }

        long alertTime = cal.getTimeInMillis();
        // Toast.makeText(context, "Reminder set for " + year + "-" + month + "-" + day, Toast.LENGTH_SHORT).show();
        return alertTime;
    }


    // Called as soon we get the release date of the game / Store Calender date in the (int) member variables; year, day and month (Up top)
    public void stringToDate(String releaseDate){

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;

        try {
            date = formatter.parse(releaseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

    }

    // Change title
    public void setTitle(String title){
        this.title = title;
    }


    // Dialog / Where the parameters get their data (value)
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int yearOf, int monthOfYear, int dayOfMonth) {

            year = yearOf;
            month = monthOfYear;
            day = dayOfMonth;

            // Toast.makeText(context, "Reminder set for " + year + "-" + month + " " + day, Toast.LENGTH_SHORT).show();

            long alertTime =  getAlertTime(year, month, day, 1);
            mNotification.setAlarm(gameID, alertTime);
            // Store the long value in DB
            mMyDBHandler.updateAlertTime(alertTime, gameID);

            if (mMyRecyclerViewAdapter != null) {
                mMyRecyclerViewAdapter.notifyDataSetChanged();
            }

            dialog.cancel();

        }

    };


}
