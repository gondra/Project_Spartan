package Utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.InputStream;
import java.net.URL;


public class LoadImageFromURI extends AsyncTask<String, String, Drawable > {
    ImageView imageView;
    Context mContext;
    ProgressBar bar;

    public LoadImageFromURI(Context mContext, ImageView imageView, ProgressBar bar){
        this.mContext = mContext;
        this.imageView = imageView;
        this.bar = bar;
    }
    @Override
    protected Drawable doInBackground(String... params) {

        //bar.setVisibility(View.VISIBLE);
        Drawable image = LoadImageFromWebOperations(params[0]);
        return image;
    }

    protected void onPostExecute(Drawable image) {
        bar.setVisibility(View.GONE);
        imageView.setImageDrawable(image);
    }

    public Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

}

