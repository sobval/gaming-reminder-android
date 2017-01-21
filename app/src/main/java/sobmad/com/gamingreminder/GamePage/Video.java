package sobmad.com.gamingreminder.GamePage;

import java.io.Serializable;

/**
 * Created by user on 03/01/2016.
 */
public class Video implements Serializable{

    private String title;
    private String videoID;
    private String thumbnailURL;
    private boolean beingWatched = false;

    public Video(){}

    public Video(String title, String videoID, String thumbnailURL){
        this.title = title;
        this.videoID = videoID;
        this.thumbnailURL = thumbnailURL;
    }

    public String getTitle(){
        return title;
    }

    public String getVideoID(){
        return videoID;
    }

    public String getThumbnailURL(){
        return thumbnailURL;
    }

    public void setBeingWatched(boolean beingWatched){
        this.beingWatched = beingWatched;
    }

    public boolean getBeingWatched(){
        return beingWatched;
    }

    // Setters

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

}
