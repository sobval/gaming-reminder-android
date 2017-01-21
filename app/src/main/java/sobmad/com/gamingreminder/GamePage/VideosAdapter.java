package sobmad.com.gamingreminder.GamePage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import sobmad.com.gamingreminder.HTTP.VolleySingleton;
import sobmad.com.gamingreminder.R;

/**
 * Created by user on 03/01/2016.
 */
public class VideosAdapter extends ArrayAdapter {

    private ArrayList<Video> data = new ArrayList<>();
    private ImageLoader mImageLoader;
    // ArrayList of all the rows (the views)
    private ArrayList<View> videoRowsList = new ArrayList<>();


    public VideosAdapter(Context context, ArrayList<Video> data) {
        super(context, R.layout.video_row, data);
        this.data = data;
        mImageLoader = VolleySingleton.getInstance().getImageLoader();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View videoRow = inflater.inflate(R.layout.video_row, parent, false);
        TextView videoTitleView = (TextView) videoRow.findViewById(R.id.videoTitleTxtView);
        TextView creditsTxtView = (TextView) videoRow.findViewById(R.id.titleCredit);
        final ImageView thumbnailView = (ImageView) videoRow.findViewById(R.id.thumbnail_view);

        // Retrieving the game's video data
        final Video video = data.get(position);


        // Setting the UI
        videoTitleView.setText(video.getTitle());

        if (video.getBeingWatched()){
            creditsTxtView.setText("Now Playing");
            creditsTxtView.setTextColor(Color.GREEN);
        } else {
            creditsTxtView.setText("Youtube Video");
            creditsTxtView.setTextColor(Color.BLACK);
        }


        mImageLoader.get(video.getThumbnailURL(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                thumbnailView.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                // Do nothing, Youtube provides no thumbnail
            }
        });

        // Play video in VideoActivity
        videoRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), VideoActivity.class);
                intent.putExtra("uid", video.getVideoID());
                intent.putExtra("data", new DataWrapper(data));
                intent.putExtra("position", position);
                if (getContext() instanceof  VideoActivity){
                    // All of the other activities on top of it will be closed
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }

                getContext().startActivity(intent);

            }
        });

        return videoRow;
    }


    public void setNowPlayingView(int position){

        for (Video video: data){
            video.setBeingWatched(false);
        }

        data.get(position).setBeingWatched(true);
    }

}
