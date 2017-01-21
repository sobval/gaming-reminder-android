package sobmad.com.gamingreminder.Settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import java.util.ArrayList;

import sobmad.com.gamingreminder.Main.MainActivity;
import sobmad.com.gamingreminder.Main.RecyclerViewAdapter;
import sobmad.com.gamingreminder.Main.UpcomingFragment;
import sobmad.com.gamingreminder.R;

/**
 * Created by user on 03/03/2016.
 */
public class PlatformsAdapter extends ArrayAdapter<Platform>{

    private ArrayList<Platform> data;
    private Context mContext;
    private UpcomingFragment fragment;


    public PlatformsAdapter(Context context, ArrayList<Platform> data, UpcomingFragment fragment) {
        super(context, R.layout.platform_item, data);
        this.data = data;
        this.fragment = fragment;
        mContext = context;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        final View view = layoutInflater.inflate(R.layout.platform_item, parent, false);

        final Platform platform = data.get(position);

        final TextView platfomName = (TextView) view.findViewById(R.id.platform_name);

        // Get name
        platfomName.setText(platform.getPlatform());

        // If doesn't exist positionHighlighted is 1 because PC (Windows) is default platform
        SharedPreferences prefsPosition = mContext.getSharedPreferences("highlightedPosition", Context.MODE_PRIVATE);
        int positionHighlighted = prefsPosition.getInt("position", 0); // 0 is Windows

        if (position == positionHighlighted){
            // view.setBackgroundColor(Color.LTGRAY);
            view.setBackgroundColor(Color.parseColor("#FF0099CC"));
            platfomName.setTextColor(Color.parseColor("#ffffffff"));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save Platform Tag
                SharedPreferences.Editor editor = mContext.getSharedPreferences("favoritePlatform", mContext.MODE_PRIVATE).edit();
                editor.putString("name", platform.getPlatformTag()); // Platform in context / position
                editor.commit();

                // Save Position to highlight it
                SharedPreferences.Editor editorPosition = mContext.getSharedPreferences("highlightedPosition", mContext.MODE_PRIVATE).edit();
                editorPosition.putInt("position", position);
                editorPosition.commit();

                //Toast.makeText(mContext, platform.getPlatformTag(), Toast.LENGTH_SHORT).show();

                // change the background color of the selected element
                // view.setBackgroundColor(Color.LTGRAY);
                // No Need mRecyclerViewAdapter.notifyDataSetChanged();
                UpcomingFragment.getFilterDialog().cancel();

                // To Refresh / Update

                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                v.getContext().startActivity(intent);

            }
        });

        return view;
    }

}
