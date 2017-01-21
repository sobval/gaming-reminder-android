package sobmad.com.gamingreminder.Settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import sobmad.com.gamingreminder.HTTP.VolleySingleton;
import sobmad.com.gamingreminder.Main.MainActivity;
import sobmad.com.gamingreminder.R;

public class SettingsActivityApp extends ActionBarActivity {

    // UI
    private RelativeLayout mImageClearCache;
    // private Spinner mImageSpinner;
    private Spinner mCoverSpinner;
    private Switch mSwitch;
    private SeekBar mSeekBar;
    private RelativeLayout mIGDBContact;
    private RelativeLayout mFeedback;

    ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FF0099CC"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_activity_app);

        // Action Bar's Color
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        this.setTitle("Settings");

        // Change Status Bar's Color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#FF0099CC"));
        }

        // UI Initialize
        mImageClearCache = (RelativeLayout) findViewById(R.id.image_clear_cache);
        mIGDBContact = (RelativeLayout) findViewById(R.id.igdb_contact);
        mFeedback = (RelativeLayout) findViewById(R.id.contact_us);
        mSwitch = (Switch) findViewById(R.id.switch_theme);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        // mImageSpinner = (Spinner) findViewById(R.id.image_spinner);
        mCoverSpinner = (Spinner) findViewById(R.id.cover_spinner);
        loadSpinner();
        loadCoverSpinner();

        // To IGDB.com
        mIGDBContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.igdb.com/";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        // Contact US
        mFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"sobmad@outlook.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "");
                i.putExtra(Intent.EXTRA_TEXT   , "");
                try {
                    startActivity(Intent.createChooser(i, "Send mail"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSeekBar.setMax(4);


        // Retrieve FONT SIZE
        // First Check if fontSize exists
        SharedPreferences prefsFontSize = getSharedPreferences("fontSizePref", Context.MODE_PRIVATE);
        String size = prefsFontSize.getString("fontSize", "");
        //Toast.makeText(getApplicationContext(), "String: " + size, Toast.LENGTH_SHORT).show();
        if (!size.isEmpty()) {
            double fontSize = Double.parseDouble(size);
            mSeekBar.setProgress(getProgressValue(fontSize));
        } else {
            mSeekBar.setProgress(0);
        }


        // Change font size
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int mProgress = 0;
            SharedPreferences.Editor prefsFontSizeEditor = getSharedPreferences("fontSizePref", MODE_PRIVATE).edit();


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mProgress = progress;
                // value now holds the decimal value between 0.0 and 10.0 of the progress
                // Double parsed to long
                prefsFontSizeEditor.putString("fontSize", String.valueOf(getFontSize(mProgress)));
                prefsFontSizeEditor.commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                prefsFontSizeEditor.putString("fontSize", String.valueOf(getFontSize(mProgress)));
                prefsFontSizeEditor.commit();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                prefsFontSizeEditor.putString("fontSize", String.valueOf(getFontSize(mProgress)));
                prefsFontSizeEditor.commit();
            }
        });

        // Clear Image Cache
        mImageClearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VolleySingleton.getInstance().getRequestQueue().getCache().clear();
            }
        });

        // Retrieve Image Quality
        // First Check if screenshotQuality exists
        SharedPreferences prefsQuality = getSharedPreferences("sreenshotQuality", Context.MODE_PRIVATE);
        String quality = prefsQuality.getString("quality", "Medium");

        /*
        if (quality.equals("Big")){
            mImageSpinner.setSelection(1);
        } else if (quality.equals("Huge")){
            mImageSpinner.setSelection(2);
        } else {
            // Else Quality set to medium
            mImageSpinner.setSelection(0);
        }
        */


        // Spinner Listener
        // Default is Medium
        // SAVE Image Quality

        /*
        final SharedPreferences.Editor editorSreenQuality = getSharedPreferences("sreenshotQuality", MODE_PRIVATE).edit(); // First argument MY_PREFS_NAME (The name)
        mImageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = "Medium"; // Default is Medium
                text = mImageSpinner.getSelectedItem().toString();
                editorSreenQuality.putString("quality", text);
                editorSreenQuality.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        */

        // Cover Spinner

        // First Check if screenshotQuality exists
        SharedPreferences prefsCover = getSharedPreferences("coverQuality", Context.MODE_PRIVATE);
        String coverQuality = prefsCover.getString("quality", "Big");


        if (coverQuality.equals("Big")){
            mCoverSpinner.setSelection(1);
        } else if (coverQuality.equals("Big2X")){
            mCoverSpinner.setSelection(2);
        } else {
            // Else Quality set to Small
            mCoverSpinner.setSelection(0);
        }


        // Spinner Listener
        // Default is Small
        // SAVE Cover Quality
        final SharedPreferences.Editor editorCoverQuality = getSharedPreferences("coverQuality", MODE_PRIVATE).edit(); // First argument MY_PREFS_NAME (The name)
        mCoverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = "Big"; // Default is Small for Cover Images
                text = mCoverSpinner.getSelectedItem().toString();
                // Takes the String of the combo box and directly save it
                editorCoverQuality.putString("quality", text);
                editorCoverQuality.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // THEME
        // Retrieve theme
        SharedPreferences prefs = getSharedPreferences("appTheme", Context.MODE_PRIVATE);
        final String theme = prefs.getString("theme", "dark"); // Default is dark / Always Dark

        if (!theme.equals("dark")){
            mSwitch.setChecked(true);
        }

        // Save new theme
        final SharedPreferences.Editor editor = getSharedPreferences("appTheme", MODE_PRIVATE).edit(); // First argument MY_PREFS_NAME (The name)

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    editor.putString("theme", "light");
                    editor.commit();
                    //Toast.makeText(getApplicationContext(), theme, Toast.LENGTH_SHORT).show();
                } else {
                    editor.putString("theme", "dark");
                    editor.commit();
                    //Toast.makeText(getApplicationContext(), theme, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings_activity_app, menu);
        return true;
    }

    public double getFontSize(int progress){
        double fontSize = 10;
        switch (progress){
            case 0:
                fontSize = 10;
                break;
            case 1:
                fontSize = 10.5;
                break;
            case 2:
                fontSize = 11;
                break;
            case 3:
                fontSize = 11.5;
                break;
            case 4:
                fontSize = 12;
                break;
        }
        return fontSize;
    }


    public int getProgressValue(double fontSize){
        int progress = 0; // Default is set to 0
        if (fontSize == 10){
            progress = 0;
        } else if (fontSize == 10.5){
            progress = 1;
        } else if (fontSize == 11){
            progress = 2;
        } else if (fontSize == 11.5){
            progress = 3;
        } else if (fontSize == 12){
            progress = 4;
        }
        return  progress;
    }

    public void loadSpinner(){
        ArrayAdapter<String> adapter;
        ArrayList<String> list = new ArrayList<String>();

        list.add("Medium"); // screenshot_med
        list.add("Big"); // screenshot_big
        list.add("Huge"); // screenshot_huge

        // Spinner is populated from a pre-existing string-array and ArrayAdapter Accepts a list in its argument
        // simple_spinner_item and simple_spinner_dropdown_item are default values (android.R.layout.simple_spinner_dropdown_item)
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_item, list);

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_menu);

        // Add the Adapter in the Spinner
        // mImageSpinner.setAdapter(adapter);
    }

    public void loadCoverSpinner(){
        ArrayAdapter<String> adapter;
        ArrayList<String> list = new ArrayList<String>();

        list.add("Small"); // screenshot_med
        list.add("Big"); // screenshot_big
        list.add("Big2X"); // screenshot_huge

        // Spinner is populated from a pre-existing string-array and ArrayAdapter Accepts a list in its argument
        // simple_spinner_item and simple_spinner_dropdown_item are default values (android.R.layout.simple_spinner_dropdown_item)
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_item, list);

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_menu);

        // Add the Adapter in the Spinner
        mCoverSpinner.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        // check if settings have been changed
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}
