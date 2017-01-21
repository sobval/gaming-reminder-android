package sobmad.com.gamingreminder.GamePage;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;

import sobmad.com.gamingreminder.HTTP.VolleySingleton;
import sobmad.com.gamingreminder.R;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by user on 01/01/2016.
 */
public class CustomSwipeAdapter extends PagerAdapter {

    private ArrayList<String> image_resources = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ImageLoader mImageLoader;

    // https://github.com/chrisbanes/PhotoView PhotoViewer
    private PhotoViewAttacher mPhotoAttacher;

    public CustomSwipeAdapter(Context context, ArrayList<String> images){
        this.mContext = context;
        this.image_resources = images;
        mImageLoader = VolleySingleton.getInstance().getImageLoader();
    }

    @Override
    public int getCount() {
        return image_resources.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (FrameLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View item_view = mLayoutInflater.inflate(R.layout.swipe_layout, container, false);

        final ImageView imageView = (ImageView) item_view.findViewById(R.id.imageView);
        final ImageView right_arrow = (ImageView) item_view.findViewById(R.id.right_arrow);
        final ImageView left_arrow = (ImageView) item_view.findViewById(R.id.left_arrow);

        if (image_resources.get(position) != null && !image_resources.get(position).isEmpty()) {

            mImageLoader.get(image_resources.get(position), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                    if (image_resources.size() == 1) {
                        right_arrow.setVisibility(View.INVISIBLE);
                        left_arrow.setVisibility(View.INVISIBLE);
                    } else {
                        if (position - 1 < 0) {
                            left_arrow.setVisibility(View.INVISIBLE);
                        } else if (position + 1 == image_resources.size()) {
                            right_arrow.setVisibility(View.INVISIBLE);
                        } else {
                            right_arrow.setVisibility(View.VISIBLE);
                            left_arrow.setVisibility(View.VISIBLE);
                        }
                    }

                    imageView.setImageBitmap(response.getBitmap());

                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    imageView.setBackgroundColor(Color.LTGRAY);
                }
            });
        }

        container.addView(item_view);

        // Dialog Pop out
        item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage(position);
            }
        });


        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }


    public void showImage(int position) {
        // Dialog is called builder
        Dialog builder = new Dialog(mContext);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.requestWindowFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });

        final ImageView imageView = new ImageView(mContext);

        if (image_resources.get(position) != null && !image_resources.get(position).isEmpty()) {

            mImageLoader.get(image_resources.get(position), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                    imageView.setImageBitmap(response.getBitmap());
                    mPhotoAttacher = new PhotoViewAttacher(imageView);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    imageView.setBackgroundColor(Color.LTGRAY);
                }
            });
        }

        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
    }

}
