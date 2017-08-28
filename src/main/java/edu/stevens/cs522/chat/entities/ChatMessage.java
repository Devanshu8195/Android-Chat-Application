package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.util.DateUtils;

/**
 * Created by dduggan.
 */

public class ChatMessage implements Parcelable {

    // Primary key in the database
    public long id;

    // Global id provided by the server
    public long seqNum;

    public String messageText;

    public String chatRoom;

    // When and where the message was sent
    public Date timestamp;

    public Double longitude;

    public Double latitude;

    // Sender username and FK (in local database)
    public String sender;

    public ChatMessage() {
        id = 0;
        seqNum = 0;
        messageText= null;
        chatRoom= null;
        timestamp = null;
        sender = null;
        longitude= 0.0;
        latitude = 0.0;
    }

    // TODO add operations for parcels (Parcelable), cursors and contentvalues

    public ChatMessage(Cursor cursor) {
        // TODO
        this.id = MessageContract.getID(cursor);
        this.seqNum = MessageContract.getSequenceNumber(cursor);
        this.messageText = MessageContract.getMessageText(cursor);
        this.chatRoom = MessageContract.getChatRoom(cursor);
        this.sender = MessageContract.getSender(cursor);
        this.timestamp = MessageContract.getTimestamp(cursor);
        this.longitude = MessageContract.getLongitude(cursor);
        this.latitude = MessageContract.getLatitude(cursor);
    }

    protected ChatMessage(Parcel in) {
        id = in.readLong();
        seqNum = in.readLong();
        messageText = in.readString();
        chatRoom = in.readString();
        sender = in.readString();
        timestamp = DateUtils.readDate(in);
        longitude = in.readDouble();
        latitude = in.readDouble();
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

    public void writeToProvider(ContentValues values) {
        // TODO
        MessageContract.putID(values, this.id);
        MessageContract.putSequenceNumber(values, this.seqNum);
        MessageContract.putMessageText(values,this.messageText);
        MessageContract.putchatroom(values, this.chatRoom);
        MessageContract.putsender(values,this.sender);
        MessageContract.puttimestamp(values,this.timestamp);
        MessageContract.putLongitude(values,this.longitude);
        MessageContract.putLatitude(values,this.latitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(seqNum);
        dest.writeString(messageText);
        dest.writeString(chatRoom);
        dest.writeString(sender);
        DateUtils.writeDate(dest,timestamp);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
    }
}
