package doan.npnm.sharerecipe.lib;

import android.content.ContentResolver;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.google.firebase.storage.StorageReference;

import doan.npnm.sharerecipe.lib.context.AppContext;

public class StorageUtils {

    public interface OnPutImageListener {
        void onComplete(String url);
        void onFailure(String mess);
    }

    public static void putImgToStorage(StorageReference storageReference, Uri uri, OnPutImageListener onPutImage) {
        String extension = fileExtension(uri);
        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + extension);

        fileRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                        .addOnSuccessListener(uri1 -> onPutImage.onComplete(uri1.toString()))
                        .addOnFailureListener(ex -> onPutImage.onFailure(ex.getMessage())))
                .addOnFailureListener(ex -> onPutImage.onFailure(ex.getMessage()));
    }

    private static String fileExtension(Uri uri) {
        ContentResolver cr = AppContext.getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}