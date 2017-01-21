package sobmad.com.gamingreminder.Main;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import sobmad.com.gamingreminder.GamePage.Video;

/**
 * Created by user on 10/01/2016.
 */
public class VideoGame {
    private int id;
    private String cover;
    private String coverID;
    private String title;
    private String releaseDate; // or Date (?)
    private String summary;
    private ArrayList<String> platforms;
    private ArrayList<String> genres; // The first obe .get(0) will appear in the upcoming game's card
    private ArrayList<String> themes; // Tags
    private ArrayList<String> screenshots;
    private ArrayList<String> screenshotsTablet;
    private ArrayList<Video> videos;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // Saved in DB
    private String mainGenre;
    private String screenshotsJSON = "";
    private String genresJSON = "";
    private String themesJSON = "";

    /*
    //For DB If game delayed
    private int isDelayed = 0; //False
    // For DB AlertTime
    private long alertime = 0; // Default equals 0, if equals 0 then that means no alarm set for this game

    public long getAlertime() {
        return alertime;
    }

    public void setAlertime(long alertime) {
        this.alertime = alertime;
    }

    public int getIsDelayed() {
        return isDelayed;
    }

    public void setIsDelayed(int isDelayed) {
        this.isDelayed = isDelayed;
    }
    */

    public String getScreenshotsJSON() {
        return screenshotsJSON;
    }

    public void setScreenshotsJSON(String screenshotsJSON) {
        this.screenshotsJSON = screenshotsJSON;
    }

    public String getGenresJSON() {
        return genresJSON;
    }

    public void setGenresJSON(String genresJSON) {
        this.genresJSON = genresJSON;
    }

    public String getThemesJSON() {
        return themesJSON;
    }

    public void setThemesJSON(String themesJSON) {
        this.themesJSON = themesJSON;
    }


    // Get year too
    public int getMonth(){
        int month = -1;
        if (!releaseDate.isEmpty() || releaseDate != "TBA"){
            try {
                Date date = dateFormat.parse(releaseDate);
                // getMonth() in java.util.Date is deprecated so;
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                month = cal.get(Calendar.MONTH); // January == 0

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return month;
    }

    public int getYear(){
        int year = -1;
        if (!releaseDate.isEmpty() || releaseDate != "TBA"){
            try {
                Date date = dateFormat.parse(releaseDate);
                // getMonth() in java.util.Date is deprecated so;
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                year = cal.get(Calendar.YEAR);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return year;
    }

    public String getGameGenre(){
        String genre = "Non-categorized";

        if (!genres.isEmpty()){
            genre = genres.get(0);
        }

        return genre;
    }

    public String getMainGenre() {
        return mainGenre;
    }

    public void setMainGenre(String mainGenre) {
        this.mainGenre = mainGenre;
    }

    public ArrayList<Video> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<Video> videos) {
        this.videos = videos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCoverID() {
        return coverID;
    }

    public void setCoverID(String coverID) {
        this.coverID = coverID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public ArrayList<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(ArrayList<String> platforms) {
        this.platforms = platforms;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public ArrayList<String> getThemes() {
        return themes;
    }

    public void setThemes(ArrayList<String> themes) {
        this.themes = themes;
    }

    public ArrayList<String> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(ArrayList<String> screenshots) {
        this.screenshots = screenshots;
    }

    public ArrayList<String> getScreenshotsTablet() {
        return screenshotsTablet;
    }

    public void setScreenshotsTablet(ArrayList<String> screenshotsTablet) {
        this.screenshotsTablet = screenshotsTablet;
    }


    // Delete
    public String getGenreFrom(int pos){
        return genres.get(pos);
    }

    // Delete
    public String getThemesFrom(int pos){
        return themes.get(pos);
    }

    // Delete
    public String getVideoTtitle(int pos){
        return videos.get(pos).getTitle();
    }


}
