package sobmad.com.gamingreminder.GamePage;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by user on 04/01/2016.
 */
public class DataWrapper implements Serializable {

    /*
    Wrap ArrayList of Objects here to pass it in intent to another Activity
    */

    private ArrayList<Video> data;

    public DataWrapper(ArrayList<Video> data) {
        this.data = data;
    }

    public ArrayList<Video> getVideosList() {
        return data;
    }

}
