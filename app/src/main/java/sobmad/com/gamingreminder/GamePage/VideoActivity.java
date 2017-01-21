package sobmad.com.gamingreminder.GamePage;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;

import sobmad.com.gamingreminder.R;

public class VideoActivity extends YouTubeBaseActivity {

    // UI
    private YouTubePlayerView mYoutubePlayer;
    private ListView mListViewVideos;
    private VideosAdapter mVideosAdapter;
        // Listener that allows us to play videos from YouTube
    private YouTubePlayer.OnInitializedListener mOnInitializedListener;

        // ListView data
    private ArrayList<Video> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        mYoutubePlayer = (YouTubePlayerView) findViewById(R.id.youtubePlayer);
        mListViewVideos = (ListView) findViewById(R.id.listViewVideos);

        Intent intent = getIntent();
        // Position
        int position = intent.getIntExtra("position", 0);
        // UID of video
        final String uid = intent.getStringExtra("uid");


        // Retrieving ArrayList of videos
        DataWrapper dw = (DataWrapper) getIntent().getSerializableExtra("data");
        this.data = dw.getVideosList();

        mVideosAdapter = new VideosAdapter(this, data);
        mListViewVideos.setAdapter(mVideosAdapter);

        // Set Now playing video
        mVideosAdapter.setNowPlayingView(position);



        mOnInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

                youTubePlayer.loadVideo(uid);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(getApplicationContext(), "This video is no longer available", Toast.LENGTH_LONG).show();
            }
        };

        // Play video [Hard coded API Key / Please use yours]
        mYoutubePlayer.initialize("AIzaSyA4qFNPTSUWyRC78wXmg9dERPugCFgDo4E", mOnInitializedListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video, menu);
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
