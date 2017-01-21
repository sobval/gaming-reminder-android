package sobmad.com.gamingreminder.GamePage;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;

import sobmad.com.gamingreminder.Main.RecyclerViewAdapter;
import sobmad.com.gamingreminder.R;

/**
 * Created by user on 07/01/2016.
 */
public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<String> data;

    // The game we talking about
    private int gameID;

    public TagsAdapter(Context context, ArrayList<String> data, int gameID){
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.gameID = gameID;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Represents a card
        View view = inflater.inflate(R.layout.tag, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final String text = data.get(position);

        holder.mTag.setText(text);

        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), TagsFilterActivity.class);
                intent.putExtra("tagText", text);
                intent.putExtra("gameID", gameID);
                v.getContext().startActivity(intent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder  {
        TextView mTag;
        RelativeLayout mLayout;
        public MyViewHolder(View itemView) {
            super(itemView);
            mTag = (TextView) itemView.findViewById(R.id.tagText);
            mLayout = (RelativeLayout) itemView.findViewById(R.id.tagLayout);
        }

    }

}
