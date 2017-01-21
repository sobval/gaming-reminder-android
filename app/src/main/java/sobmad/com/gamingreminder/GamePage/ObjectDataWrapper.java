package sobmad.com.gamingreminder.GamePage;

import java.io.Serializable;

import sobmad.com.gamingreminder.Main.VideoGame;

/**
 * Created by user on 19/01/2016.
 */
public class ObjectDataWrapper implements Serializable {

    private VideoGame videoGame;

    public ObjectDataWrapper(VideoGame videoGame){ this.videoGame = videoGame; }

    public VideoGame getVideoGame(){ return videoGame; }
}
