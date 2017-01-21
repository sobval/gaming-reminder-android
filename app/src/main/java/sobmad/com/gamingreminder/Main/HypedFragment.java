package sobmad.com.gamingreminder.Main;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import sobmad.com.gamingreminder.Database.MyDBHandler;
import sobmad.com.gamingreminder.R;


public class HypedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // UI
    private RecyclerView mMyRecyclerView;
    private MyRecyclerViewAdapter mMyRecyclerViewAdapter;
    private TextView mTextView;

    // DB
    private MyDBHandler mMyDBHandler;
    private ArrayList<VideoGame> mUpcomingGames;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HypedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HypedFragment newInstance(String param1, String param2) {
        HypedFragment fragment = new HypedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HypedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // In this Activity check if game got delayed with the new json data retrived
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_hyped, container, false);

        mMyDBHandler = new MyDBHandler(getActivity(), "", null, 0);

        // From Database, sor Collections.Swap in the array of the database
        mUpcomingGames = mMyDBHandler.getAllData();

        mMyRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_hypedGames);

        // Theme
        // Retrieve theme
        SharedPreferences prefs = getContext().getSharedPreferences("appTheme", Context.MODE_PRIVATE);
        String theme = prefs.getString("theme", "dark"); // Default is dark

        if (theme.equals("light")){
            mMyRecyclerView.setBackgroundColor(Color.GRAY);
        } else {
            // DO Nothing theme is already dark
        }

        mMyRecyclerViewAdapter = new MyRecyclerViewAdapter(getActivity(), mUpcomingGames);


        mMyRecyclerView.setHasFixedSize(true);
        mMyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Apply this adapter to the RecyclerView
        mMyRecyclerView.setAdapter(mMyRecyclerViewAdapter);

        // Add ItemTouchHelper to RecyclerView (Drag / Drop)
        ItemTouchHelper.Callback callback = new RecyclerViewDragHelper(mMyRecyclerViewAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mMyRecyclerView);


        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        mUpcomingGames.clear();
        mUpcomingGames.addAll(mMyDBHandler.getAllData());
        mMyRecyclerViewAdapter.notifyDataSetChanged();
    }


}
