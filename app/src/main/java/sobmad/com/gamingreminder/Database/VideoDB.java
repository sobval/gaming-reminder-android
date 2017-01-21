package sobmad.com.gamingreminder.Database;

/**
 * Created by user on 24/01/2016.
 */
public class VideoDB {

    private int _gameID;
    private String _videoID;
    private String _title;
    private String _thumbnailURL;

    public VideoDB(int gameID, String title, String videoID, String thumbnailURL) {
        this._gameID = gameID;
        this._title = title;
        this._videoID = videoID;
        this._thumbnailURL = thumbnailURL;
    }

    public int get_gameID() {
        return _gameID;
    }

    public void set_gameID(int _gameID) {
        this._gameID = _gameID;
    }

    public String get_title() {
        return _title;
    }

    public void set_title(String _title) {
        this._title = _title;
    }

    public String get_videoID() {
        return _videoID;
    }

    public void set_videoID(String _videoID) {
        this._videoID = _videoID;
    }

    public String get_thumbnailURL() {
        return _thumbnailURL;
    }

    public void set_thumbnailURL(String _thumbnailURL) {
        this._thumbnailURL = _thumbnailURL;
    }
}
