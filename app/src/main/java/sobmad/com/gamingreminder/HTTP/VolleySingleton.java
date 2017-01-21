package sobmad.com.gamingreminder.HTTP;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import sobmad.com.gamingreminder.Main.GamingReminder;

/**
 * Created by user on 01/01/2016.
 */
public class VolleySingleton {

    private static VolleySingleton sInstance = null;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public VolleySingleton(){
        mRequestQueue = Volley.newRequestQueue(GamingReminder.getAppContext());
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            /*
            Each time a value is accessed, it is moved to the head of a queue. When a value is added to a full cache,
            the value at the end of that queue is evicted and may become eligible for garbage collection (Objects that are not being used). (Source: http://developer.android.com/)
             */
            private LruCache<String, Bitmap> cache = new LruCache<>((int) (Runtime.getRuntime().maxMemory()/1024/8));

            @Override
            // if entry in cache present return it
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }
            // add to cache
            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    public static VolleySingleton getInstance(){
        if (sInstance == null){
            sInstance = new VolleySingleton();
        }
        return sInstance;
    }

    public RequestQueue getRequestQueue(){
        return mRequestQueue;
    }

    public ImageLoader getImageLoader(){
        return mImageLoader;
    }
}
