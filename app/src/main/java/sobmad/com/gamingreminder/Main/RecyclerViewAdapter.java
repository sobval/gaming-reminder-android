package sobmad.com.gamingreminder.Main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.w3c.dom.Text;

import java.util.ArrayList;

import sobmad.com.gamingreminder.GamePage.DataWrapper;
import sobmad.com.gamingreminder.GamePage.ObjectDataWrapper;
import sobmad.com.gamingreminder.GamePage.PageActivity;
import sobmad.com.gamingreminder.GamePage.Video;
import sobmad.com.gamingreminder.HTTP.VolleySingleton;
import sobmad.com.gamingreminder.R;

/**
 * Created by user on 07/01/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<VideoGame> data;
    private ImageLoader mImageLoader;
    private Context mContext;
    private String theme = "dark";
    private double fontSize = 10;

    public RecyclerViewAdapter(Context context, ArrayList<VideoGame> data){
        inflater = LayoutInflater.from(context);
        mImageLoader = VolleySingleton.getInstance().getImageLoader();
        this.data = data;
        mContext = context;

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

        // Toast.makeText(mContext, ((float)fontSize) + "", Toast.LENGTH_SHORT).show();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Represents a card
        View view = inflater.inflate(R.layout.upcoming_game_card, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final VideoGame videoGame = data.get(position);
        String title = videoGame.getTitle();
        String genre = videoGame.getGameGenre();
        String releaseDate = videoGame.getReleaseDate();

        holder.mTitle.setText(title);
        holder.mGenre.setText(genre);
        holder.mReleaseDate.setText(releaseDate);

        // Change font size
        holder.mTitle.setTextSize((float) fontSize);
        holder.mGenre.setTextSize((float) fontSize);
        holder.mReleaseDate.setTextSize((float) fontSize);

        // SO if fontSize equals 12 // Stay in one line
        if (fontSize > 11.5){
            holder.mTitle.setMaxLines(1);
        }

        // Setting theme
        if (theme.equals("light")){
            holder.mLayout.setBackgroundColor(Color.DKGRAY);
        } else {
            // DO Nothing theme is already dark
        }

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

        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PageActivity.class);
                // intent.putExtra("myVideoGame", new ObjectDataWrapper(videoGame));
                intent.putExtra("title", videoGame.getTitle());
                intent.putExtra("cover", videoGame.getCover());
                intent.putExtra("summary", videoGame.getSummary());
                intent.putExtra("releaseDate", videoGame.getReleaseDate());
                intent.putExtra("id", videoGame.getId());
                intent.putExtra("platforms", videoGame.getPlatforms());
                intent.putExtra("images", videoGame.getScreenshots());
                intent.putExtra("genres", videoGame.getGenres());
                intent.putExtra("themes", videoGame.getThemes());
                intent.putExtra("mainGenre", videoGame.getGameGenre());
                intent.putExtra("videos", new DataWrapper(videoGame.getVideos()));
                v.getContext().startActivity(intent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder  {
        TextView mTitle;
        TextView mGenre;
        TextView mReleaseDate;
        ImageView mCoverView;
        RelativeLayout mLayout;
        public MyViewHolder(View itemView) {
            super(itemView);
            mLayout = (RelativeLayout) itemView.findViewById(R.id.card_layout);
            mTitle = (TextView) itemView.findViewById(R.id.gameTitle);
            mGenre = (TextView) itemView.findViewById(R.id.genreView);
            mReleaseDate = (TextView) itemView.findViewById(R.id.releaseDate);
            mCoverView = (ImageView) itemView.findViewById(R.id.coverview);
            mLayout = (RelativeLayout) itemView.findViewById(R.id.card_layout);
        }

    }

}
