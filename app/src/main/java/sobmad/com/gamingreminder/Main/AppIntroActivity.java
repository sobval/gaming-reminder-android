package sobmad.com.gamingreminder.Main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

import sobmad.com.gamingreminder.R;

public class AppIntroActivity extends AppIntro2 {

    @Override
    public void init(Bundle bundle) {
        getSupportActionBar().hide();
        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance("Welcome to Gaming Reminder!", "Swiping down on the home page requests new data back from our database", R.drawable.intro_1, Color.parseColor("#FF0099CC")));
        addSlide(AppIntroFragment.newInstance("Games games everywhere!!", "This is where the simple idea of a favorites list comes in. A quick and useful tool that separates the games your most excited about from the rest. Swipe right to access your list from the home page", R.drawable.intro_2, Color.parseColor("#FF0099CC")));
        addSlide(AppIntroFragment.newInstance("Set a Reminder!", "Set a reminder to any game and a push notification will notify you about the game's imminent release on the date you set", R.drawable.intro_3, Color.parseColor("#FF0099CC")));
        // mes games everywhere!
        // OPTIONAL METHODS
        // Override bar/separator color.
    }



    @Override
    public void onNextPressed() {

    }

    private void loadMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDonePressed() {
        SharedPreferences.Editor editor = getSharedPreferences("FirstBoot", MODE_PRIVATE).edit();
        editor.putBoolean("first", false);
        editor.commit();
        loadMainActivity();
    }

    @Override
    public void onSlideChanged() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_app_intro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
