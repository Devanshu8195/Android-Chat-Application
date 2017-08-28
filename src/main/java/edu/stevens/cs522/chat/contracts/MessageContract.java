package edu.stevens.cs522.chat.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.util.Date;

import edu.stevens.cs522.chat.util.DateUtils;

import static android.R.attr.id;

/**
 * Created by dduggan.
 */

public class MessageContract extends BaseContract {

    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY, "Message");

    public static final Uri CONTENT_URI(long id) {
        return CONTENT_URI(Long.toString(id));
    }

    public static final Uri CONTENT_URI(String id) {
        return withExtendedPath(CONTENT_URI, id);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));

    /*
     * A special URI for replacing messages after sequence numbers are assigned by server.
     * The number in the URI specifies how many messages to be replaced after server assigns seq numbers.
     */
    private static final Uri CONTENT_URI_SYNC = withExtendedPath(CONTENT_URI, "sync");

    public static final Uri CONTENT_URI_SYNC(int id) {
        return CONTENT_URI_SYNC(Integer.toString(id));
    }

    private static final Uri CONTENT_URI_SYNC(String id) {
        return withExtendedPath(CONTENT_URI_SYNC, id);
    }

    public static final String CONTENT_PATH_SYNC = CONTENT_PATH(CONTENT_URI_SYNC("#"));


    public static final String ID = _ID;

    public static final String SEQUENCE_NUMBER = "sequence_number";

    public static final String MESSAGE_TEXT = "message_text";

    public static final String CHAT_ROOM = "chat_room";

    public static final String TIMESTAMP = "timestamp";

    public static final String LATITUDE = "latitude";

    public static final String LONGITUDE = "longitude";

    public static final String SENDER = "sender";

    public static final String[] COLUMNS = {ID, SEQUENCE_NUMBER, MESSAGE_TEXT, CHAT_ROOM, TIMESTAMP, LATITUDE, LONGITUDE, SENDER};


    private static int sequenceNumberColumn = -1;

    public static Long getSequenceNumber(Cursor cursor) {
        if (sequenceNumberColumn < 0) {
            sequenceNumberColumn = cursor.getColumnIndexOrThrow(SEQUENCE_NUMBER);
        }
        return cursor.getLong(sequenceNumberColumn);
    }

    public static void putSequenceNumber(ContentValues out, Long seqNum) {
        out.put(SEQUENCE_NUMBER, seqNum);
    }

    private static int messageTextColumn = -1;

    public static String getMessageText(Cursor cursor) {
        if (messageTextColumn < 0) {
            messageTextColumn = cursor.getColumnIndexOrThrow(MESSAGE_TEXT);
        }
        return cursor.getString(messageTextColumn);
    }

    public static void putMessageText(ContentValues out, String messageText) {
        out.put(MESSAGE_TEXT, messageText);
    }

    // TODO remaining getter and putter operations for other columns

    private static int chatroomColumn = -1;

    public static String getChatRoom(Cursor cursor) {
        if (chatroomColumn < 0) {
            chatroomColumn = cursor.getColumnIndexOrThrow(CHAT_ROOM);
        }
        return cursor.getString(chatroomColumn);
    }

    public static void putchatroom(ContentValues out, String chatroom) {
        out.put(CHAT_ROOM, chatroom);
    }

    private static int idColumn = -1;

    public  static long getID(Cursor cursor) {
        if (idColumn < 0) {
            idColumn =  cursor.getColumnIndexOrThrow(ID);;
        }
        return cursor.getLong(idColumn);
    }

    public  static void putID(ContentValues values, long id) {
        values.put(ID, id);
    }

    private static int timestampColumn = -1;

    public static Date getTimestamp(Cursor cursor) {
        if (timestampColumn < 0) {
            timestampColumn = cursor.getColumnIndexOrThrow(TIMESTAMP);
        }
        return DateUtils.getDate(cursor, timestampColumn);
    }

    public  static void puttimestamp(ContentValues out, Date timeStamp) {
        DateUtils.putDate(out,TIMESTAMP,timeStamp);
    }

    private static int senderColumn = -1;

    public static String getSender(Cursor cursor) {
        if (senderColumn < 0) {
            senderColumn = cursor.getColumnIndexOrThrow(SENDER);
        }
        return cursor.getString(senderColumn);
    }

    public static void putsender(ContentValues out, String sender) {
        out.put(SENDER, sender);
    }

    private static int longitudecolumn = -1;

    public static double getLongitude(Cursor cursor){
        if(longitudecolumn < 0){
            longitudecolumn = cursor.getColumnIndexOrThrow(LONGITUDE);
        }
        return cursor.getDouble(longitudecolumn);
    }

    public static void putLongitude(ContentValues values,double longitude){values.put(LONGITUDE,longitude);}

    private static int latitudecolumn = -1;

    public static double getLatitude(Cursor cursor){
        if(latitudecolumn < 0){
            latitudecolumn = cursor.getColumnIndexOrThrow(LATITUDE);
        }

        return cursor.getDouble(latitudecolumn);
    }

    public static void putLatitude(ContentValues values,double latitude){values.put(LATITUDE,latitude);}
}
