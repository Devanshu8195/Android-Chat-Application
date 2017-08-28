package edu.stevens.cs522.chat.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import edu.stevens.cs522.chat.contracts.BaseContract;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.contracts.PeerContract;

import static edu.stevens.cs522.chat.contracts.PeerContract.NAME;

public class ChatProvider extends ContentProvider {

    public ChatProvider() {
    }

    private static final String AUTHORITY = BaseContract.AUTHORITY;

    private static final String MESSAGE_CONTENT_PATH = MessageContract.CONTENT_PATH;

    private static final String MESSAGE_CONTENT_PATH_ITEM = MessageContract.CONTENT_PATH_ITEM;

    private static final String MESSAGE_CONTENT_PATH_SYNC = MessageContract.CONTENT_PATH_SYNC;

    private static final String PEER_CONTENT_PATH = PeerContract.CONTENT_PATH;

    private static final String PEER_CONTENT_PATH_ITEM = PeerContract.CONTENT_PATH_ITEM;


    private static final String DATABASE_NAME = "chat.db";

    private static final int DATABASE_VERSION = 3;

    private static final String MESSAGES_TABLE = "messages";

    private static final String PEERS_TABLE = "peers";



    // Create the constants used to differentiate between the different URI  requests.
    private static final int MESSAGES_ALL_ROWS = 1;
    private static final int MESSAGES_SINGLE_ROW = 2;
    private static final int MESSAGES_SYNC = 3;
    private static final int PEERS_ALL_ROWS = 4;
    private static final int PEERS_SINGLE_ROW = 5;

    public static class DbHelper extends SQLiteOpenHelper {

        public static final String MESSAGE_CREATE =
                "CREATE TABLE "+MESSAGES_TABLE+" ( "
                        + "_id INTEGER PRIMARY KEY autoincrement, "
                        + "sequence_number integer not null,"
                        + "message_text text not null,"
                        + "chat_room text not null,"
                        + "timestamp long not null,"
                        + "sender text not null,"
                        + "longitude double not null,"
                        + "latitude double not null,"
                        + "FOREIGN KEY (sender) REFERENCES Peers(name) ON DELETE CASCADE " +
                ")";

        public static final String PEER_CREATE =
                "CREATE TABLE "+PEERS_TABLE+" ( "
                        +  "_id integer not null, "
                        + "name TEXT PRIMARY KEY, "
                        + "timestamp long not null,"
                        + "longitude double not null,"
                        + "latitude double not null "
                        + ")";

        public static final String MESSAGE_PEER_INDEX = "CREATE	INDEX MessagesPeerIndex ON Messages(sender);";

        public static final String PEER_NAME_INDEX = "CREATE INDEX PeerNameIndex ON Peers(name);";


        public DbHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO initialize database tables
            db.execSQL(MESSAGE_CREATE);
            db.execSQL(PEER_CREATE);
            db.execSQL(MESSAGE_PEER_INDEX);
            db.execSQL(PEER_NAME_INDEX);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO upgrade database if necessary
            Log.w("TaskDBAdapter",
                    "Upgrading from version " + oldVersion + " to " + newVersion);

            db.execSQL("DROP TABLE IF EXISTS " + PEERS_TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + MESSAGES_TABLE);

            onCreate(db);
        }
    }

    private DbHelper dbHelper;

    @Override
    public boolean onCreate() {
        // Initialize your content provider on startup.
        dbHelper = new DbHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        return true;
    }

    // Used to dispatch operation based on URI
    private static final UriMatcher uriMatcher;

    // uriMatcher.addURI(AUTHORITY, CONTENT_PATH, OPCODE)
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH, MESSAGES_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH_ITEM, MESSAGES_SINGLE_ROW);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH_SYNC, MESSAGES_SYNC);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH, PEERS_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH_ITEM, PEERS_SINGLE_ROW);
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                // TODO: Implement this to handle requests to insert a new message.
                // Make sure to notify any observers
                long row = db.insert(MESSAGES_TABLE, null, values);
                Uri	instanceUri	=	MessageContract.CONTENT_URI(row);
                ContentResolver cr = getContext().getContentResolver();
                cr.notifyChange(instanceUri, null);
                return	instanceUri;

            case PEERS_ALL_ROWS:
                // TODO: Implement this to handle requests to insert a new peer.
                // Make sure to notify any observers
                String peerName = values.get("name").toString();
                String query = "SELECT * FROM " + PEERS_TABLE + " WHERE " + NAME + " = '" + peerName + "'";
                Cursor peerCursor = db.rawQuery(query, null);
                long row1 = 0;
                if(peerCursor.getCount() == 0){
                    row1 = db.insert(PEERS_TABLE, null, values);
                }else if (peerCursor.getCount() == 1){
                    row1 = db.update(PEERS_TABLE, values, NAME + "=?" , new String[]{peerName});
                }
                Uri instanceUri1 = PeerContract.CONTENT_URI(row1);
                ContentResolver cr1 = getContext().getContentResolver();
                cr1.notifyChange(instanceUri1, null);
                return instanceUri1;

            case MESSAGES_SINGLE_ROW:
                throw new IllegalArgumentException("insert expects a whole-table URI");
            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;
        Cursor cur;

        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                // TODO: Implement this to handle query of all messages.
                String s = " SELECT * FROM " + MESSAGES_TABLE ;
                cursor =db.rawQuery(s,null);
                cursor.setNotificationUri(getContext().getContentResolver(),MessageContract.CONTENT_URI);
                return cursor;

            case PEERS_ALL_ROWS:
                // TODO: Implement this to handle query of all peers.
                String p = " SELECT * FROM " + PEERS_TABLE;
                Cursor pcursor;
                pcursor = db.rawQuery(p,null);
                pcursor.setNotificationUri(getContext().getContentResolver(),PeerContract.CONTENT_URI);
                return pcursor;

            case MESSAGES_SINGLE_ROW:
                // TODO: Implement this to handle query of a specific message.
                String s1 = " SELECT * FROM " + MESSAGES_TABLE ;
                selectionArgs = new String[]{String.valueOf(MessageContract.getId(uri))};
                cur = db.rawQuery(s1,selectionArgs);
                cur.setNotificationUri(getContext().getContentResolver(),MessageContract.CONTENT_URI);
                return cur;

            case PEERS_SINGLE_ROW:
                // TODO: Implement this to handle query of a specific peer.
                String p1 = " SELECT * FROM " + PEERS_TABLE;
                selectionArgs = new String[]{String.valueOf(PeerContract.getId(uri))};
                Cursor pcur;
                pcur = db.rawQuery(p1,selectionArgs);
                pcur.setNotificationUri(getContext().getContentResolver(),PeerContract.CONTENT_URI);
                return pcur;

            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO Implement this to handle requests to update one or more rows.
        throw new IllegalStateException("update method in provider");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Implement this to handle requests to delete one or more rows.
        throw new IllegalStateException("Delete method in provider");
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] records) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case MESSAGES_SYNC:
                /*
                 * Do all of this in a single transaction.
                 */
                db.beginTransaction();
                try {

                    /*
                     * Delete the first N messages with sequence number = 0, where N = records.length.
                     */
                    int numReplacedMessages = Integer.parseInt(uri.getLastPathSegment());

                    String[] columns = {MessageContract.ID};
                    String selection = MessageContract.SEQUENCE_NUMBER + "=0";
                    Cursor cursor = db.query(MESSAGES_TABLE, columns, selection, null, null, null, MessageContract.TIMESTAMP);
                    try {
                        if (numReplacedMessages > 0 && cursor.moveToFirst()) {
                            do {
                                String deleteSelection = MessageContract.ID + "=" + Long.toString(cursor.getLong(0));
                                db.delete(MESSAGES_TABLE, deleteSelection, null);
                                numReplacedMessages--;
                            } while (numReplacedMessages > 0 && cursor.moveToNext());
                        }
                    } finally {
                        cursor.close();
                    }

                    /*
                     * Insert the messages downloaded from server, which will include replacements for deleted records.
                     */
//                    for (ContentValues record : records) {
//                        if (db.insert(MESSAGES_TABLE, null, record) != 1) {
//                            throw new IllegalStateException("Failure to insert updated chat message record!");
//                        };
//                    }
                    for(ContentValues record : records){
                        try{
                            db.insert(MESSAGES_TABLE, null, record);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                // TODO Make sure to notify any observers


                break;

            default:
                throw new IllegalStateException("insert: bad case");

        }
        return 1;
    }


}
