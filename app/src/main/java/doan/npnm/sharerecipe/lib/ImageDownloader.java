package doan.npnm.sharerecipe.lib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

    private OnImageDownloadedListener listener;

    public ImageDownloader(OnImageDownloadedListener listener) {
        this.listener = listener;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        String imageUrl = urls[0];
        Bitmap bitmap = null;
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (listener != null) {
            listener.onImageDownloaded(bitmap);
        }
    }

    public interface OnImageDownloadedListener {
        void onImageDownloaded(Bitmap bitmap);
    }
}
