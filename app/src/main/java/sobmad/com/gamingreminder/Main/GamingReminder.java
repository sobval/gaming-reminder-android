package sobmad.com.gamingreminder.Main;

import android.app.Application;
import android.content.Context;

/**
 * Created by user on 01/01/2016.
 */
public class GamingReminder extends Application{

    private static GamingReminder sInstance;

    public GamingReminder(){
        sInstance = this;
    }

    public static GamingReminder getInstance(){
        return sInstance;
    }

    public static Context getAppContext(){
        return sInstance.getApplicationContext();
    }

}
