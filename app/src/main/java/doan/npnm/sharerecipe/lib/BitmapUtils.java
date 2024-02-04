package doan.npnm.sharerecipe.lib;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import doan.npnm.sharerecipe.app.context.AppContext;

public class BitmapUtils {

    public static File saveImage(Bitmap finalBitmap) {
        File pictureFileDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Photo Edit"
        );
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if (!isDirectoryCreated) {
                Log.i("TAG", "Can't create directory to save the image");
                return null;
            }
        }
        String filename = pictureFileDir.getPath() + File.separator + System.currentTimeMillis() + ".jpg";
        File pictureFile = new File(filename);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
            showToast("Save Image Successfully..");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        scanGallery(AppContext.getContext(), pictureFile.getAbsolutePath());
        return pictureFile;
    }

    public static String urlToString(String imageUrl) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                return Base64.encodeToString(byteArray, Base64.DEFAULT);
            } else {
                // Handle HTTP error response codes here
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void downloadImage(String imageUrl, String destinationPath) throws Exception {
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.connect();

        try (InputStream inputStream = connection.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(destinationPath)) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }
    public static void saveImagePhoto(Bitmap bitmap) {
        // Generating a file name
        String filename = System.currentTimeMillis() + ".jpg";

        OutputStream fos = null;

        // For devices running Android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = AppContext.getContext().getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            try {
                if (imageUri != null) {
                    fos = resolver.openOutputStream(imageUri);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            File imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File image = new File(imagesDir, filename);
            try {
                fos = new FileOutputStream(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (fos != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                showToast("Saved to Photos");
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void scanGallery(Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(
                    cntx, new String[]{path}, null,
                    (path1, uri) -> Toast.makeText(
                            cntx,
                            "Save Image Successfully..",
                            Toast.LENGTH_SHORT
                    ).show()
            );
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue scanning gallery.");
        }
    }


    public static Uri uriFromDrawable(Context context, int resource) {
        BitmapDrawable drawable = (BitmapDrawable) ContextCompat.getDrawable(context, resource);
        if (drawable == null) return null;

        Bitmap bitmap = drawable.getBitmap();
        File cachePath = new File(context.getCacheDir(), File.separator + System.currentTimeMillis() + ".jpg");
        try {
            FileOutputStream outputStream = new FileOutputStream(cachePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
            return FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", cachePath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encodeImage(Bitmap bitmap) {
        int preWidth = bitmap.getWidth() > bitmap.getHeight() ? bitmap.getHeight() / 2 : bitmap.getWidth() / 2;
        int preHeight = bitmap.getHeight() * preWidth / bitmap.getWidth();
        Bitmap prevBitmap = Bitmap.createScaledBitmap(bitmap, preWidth, preHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        prevBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static Bitmap stringToBitmap(String encodedImage) {
        byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String uriToString(Uri imageUri) {
        try {
            InputStream inputStream = AppContext.getContext().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return encodeImage(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Uri uriFromId(long id) {
        Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        return ContentUris.withAppendedId(contentUri, id);
    }

    public static Bitmap bitmapFromId(long id) throws FileNotFoundException {
        ContentResolver contentResolver = AppContext.getContext().getContentResolver();
        Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Long.toString(id));
        InputStream inputStream = contentResolver.openInputStream(uri);
        return BitmapFactory.decodeStream(inputStream);
    }

    public static Bitmap bitmapFromId(int id, Context context) {
        return BitmapFactory.decodeResource(context.getResources(), id);
    }

    private static void showToast(String message) {
        Toast.makeText(AppContext.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}

