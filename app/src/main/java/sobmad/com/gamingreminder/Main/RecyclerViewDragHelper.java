package sobmad.com.gamingreminder.Main;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by user on 28/02/2016.
 */
public class RecyclerViewDragHelper extends ItemTouchHelper.SimpleCallback{

    private MyRecyclerViewAdapter mAdapter;

    public RecyclerViewDragHelper(MyRecyclerViewAdapter mAdapter){
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.UP| ItemTouchHelper.DOWN);
        this.mAdapter = mAdapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mAdapter.swap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // Do Nothing This is when you swipe (Look at constrcutor, that's why Swipe left and right dpn't work)
    }
}
