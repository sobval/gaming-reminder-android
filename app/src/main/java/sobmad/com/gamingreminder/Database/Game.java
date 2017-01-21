package sobmad.com.gamingreminder.Database;

/**
 * Created by user on 17/01/2016.
 */
public class Game {
    private int _id;
    private int _gameID;
    private String _title;
    private String _releaseDate;
    private String _summary;
    private String _cover;
    private String _mainGenre;
    private String _images;
    private String _genres;
    private String _themes;
    private String _platform;
    private int _delayed = 0; // False
    private long _alertTime = 0; // Default se to 0

    public Game(int gameID, String title, String releaseDate, String summary, String cover, String mainGenre, String images, String genres, String themes, String platform) {
        this._gameID = gameID;
        this._title = title;
        this._releaseDate = releaseDate;
        this._summary = summary;
        this._cover = cover;
        this._mainGenre = mainGenre;
        this._images = images;
        this._genres = genres;
        this._themes = themes;
        this._platform = platform;

    }

    public String get_platform(){
        return _platform;
    }

    public long get_alertTime() {
        return _alertTime;
    }

    public void set_alertTime(int _alertTime) {
        this._alertTime = _alertTime;
    }

    public int isDelayed() {
        return _delayed;
    }

    public void set_isDelayed(int _delayed) {
        this._delayed = _delayed;
    }

    public String get_images() {
        return _images;
    }

    public void set_images(String _images) {
        this._images = _images;
    }

    public String get_genres() {
        return _genres;
    }

    public void set_genres(String _genres) {
        this._genres = _genres;
    }

    public String get_themes() {
        return _themes;
    }

    public void set_themes(String _themes) {
        this._themes = _themes;
    }

    public int get_gameID() {
        return _gameID;
    }

    public int get_id() {
        return _id;
    }

    public String get_title() {
        return _title;
    }

    public String get_releaseDate() {
        return _releaseDate;
    }

    public String get_summary() {
        return _summary;
    }

    public String get_cover() {
        return _cover;
    }

    public void set_gameID(int _gameID) {
        this._gameID = _gameID;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void set_title(String _title) {
        this._title = _title;
    }

    public void set_releaseDate(String _releaseDate) {
        this._releaseDate = _releaseDate;
    }

    public void set_summary(String _summary) {
        this._summary = _summary;
    }

    public void set_cover(String _cover) {
        this._cover = _cover;
    }

    public void set_mainGenre(String _mainGenre) {
        this._mainGenre = _mainGenre;
    }

    public String get_mainGenre() {
        return _mainGenre;
    }
}
