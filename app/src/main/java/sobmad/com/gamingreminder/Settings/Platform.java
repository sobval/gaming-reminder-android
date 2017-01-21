package sobmad.com.gamingreminder.Settings;

/**
 * Created by user on 03/03/2016.
 */
public class Platform {
    private String platform;
    private String tag;

    public Platform(String platform, String tag){
        this.platform = platform;
        this.tag = tag;
    }

    public String getPlatform(){
        return platform;
    }

    public String getPlatformTag(){
        return tag;
    }

}
