package doan.npnm.sharerecipe.utility;

import android.annotation.SuppressLint;

import java.util.HashMap;

public class Constant {
    public static final String KEY_USER="Users";
    public static final String RECIPE="Recipe";
    public static final String CATEGORY="Category";
    public static final String KEY_DICUSSION = "Discussion";
    public static final String FOLLOW_USER = "Single_Follow";
    public static final String LOVE = "Love_Recipe";
    public static String REMOTE_NOTIFICATION_AUTHORIZATION = "Authorization";
    public static String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static String REMOTE_MSG_DATA = "data";
    public static String REMOTE_MSG_IDS = "registration_ids";


    public static String NOTIFICATION_TYPE="notification";
    public static String NOTI_CONTENT="Content";


    @SuppressLint("SuspiciousIndentation")
    public static HashMap<String, String> GetRemoteMSGHeader() {
        HashMap<String, String> header = new HashMap<>();
        header.put(
                REMOTE_NOTIFICATION_AUTHORIZATION,
                "key=AAAAfjgSYI4:APA91bFrjdstI6OjEr1MiV7jY2CPOZZQ2yK5RcZNMSGnsiNakakv4tz2G7zNjhTZoPGlwRyVWHcgVLC2tTULncbivmSnuPHOvMcu7uyPu6A1uqH_68guC5SZa3S_gZZ3q6Pe2bb6rwnw"
        );
        header.put(
                REMOTE_MSG_CONTENT_TYPE,
                "application/json"
        );
        return header;
    }


    public static final String NOTIFICATION="Notification";
}
