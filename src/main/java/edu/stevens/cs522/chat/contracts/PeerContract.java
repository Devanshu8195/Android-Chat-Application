package edu.stevens.cs522.chat.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.util.Date;

import edu.stevens.cs522.chat.util.DateUtils;

/**
 * Created by dduggan.
 */

public class PeerContract extends BaseContract {

    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY, "Peer");

    public static final Uri CONTENT_URI(long id) {
        return CONTENT_URI(Long.toString(id));
    }

    public static final Uri CONTENT_URI(String id) {
        return withExtendedPath(CONTENT_URI, id);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));


    // TODO define column names, getters for cursors, setters for contentvalues

    public static final String ID = _ID;

    public static final String NAME = "name";

    public static final String Timestamp = "timestamp";

    public static final String LONGITUDE = "longitude";

    public static final String LATITUDE = "latitude";

    private static int idColumn = -1;
    private static int nameColumn = -1;
    private static int timestampColumn = -1;
    private static int longitudeColumn = -1;
    private static int latitudeColumn = -1;

    public  static int getID(Cursor pcur) {
        if (idColumn < 0) {
            idColumn =  pcur.getColumnIndexOrThrow(ID);;
        }
        return pcur.getInt(idColumn);
    }

    public  static void putID(ContentValues out, long id) {
        out.put(ID, id);
    }

    public static String getName(Cursor pcur) {
        if (nameColumn < 0) {
            nameColumn = pcur.getColumnIndexOrThrow(NAME);
        }
        return pcur.getString(nameColumn);
    }

    public static void putname(ContentValues out, String name) {
        out.put(NAME, name);
    }

    public static Date getTimestamp(Cursor cursor) {
        if (timestampColumn < 0) {
            timestampColumn = cursor.getColumnIndexOrThrow(Timestamp);
        }
        return DateUtils.getDate(cursor,timestampColumn);
    }

    public static void puttimestamp(ContentValues out, Date timeStamp) {
        DateUtils.putDate(out,Timestamp,timeStamp);
    }

    public static double getLongitude(Cursor cursor){
        if(longitudeColumn < 0){
            longitudeColumn = cursor.getColumnIndexOrThrow(LONGITUDE);
        }
        return cursor.getDouble(longitudeColumn);
    }

    public static void putLongitude(ContentValues values,double longitude){values.put(LONGITUDE,longitude);}


    public static double getLatitude(Cursor cursor){
        if(latitudeColumn < 0){
            latitudeColumn = cursor.getColumnIndexOrThrow(LATITUDE);
        }

        return cursor.getDouble(latitudeColumn);
    }

    public static void putLatitude(ContentValues values,double latitude){values.put(LATITUDE,latitude);}


}
