package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.InetAddress;
import java.util.Date;

import edu.stevens.cs522.chat.contracts.PeerContract;
import edu.stevens.cs522.chat.util.DateUtils;

/**
 * Created by dduggan.
 */

public class Peer implements Parcelable {

    public long id;

    // Use as PK
    public String name;

    // Last time we heard from this peer.
    public Date timestamp;

    public Double longitude;

    public Double latitude;

    public Peer() {
    }

    // TODO add operations for parcels (Parcelable), cursors and contentvalues

    public Peer(Cursor cursor) {
        // TODO
        this.id = PeerContract.getID(cursor);
        this.name = PeerContract.getName(cursor);
        this.timestamp = PeerContract.getTimestamp(cursor);
        this.longitude = PeerContract.getLongitude(cursor);
        this.latitude = PeerContract.getLatitude(cursor);
    }

    protected Peer(Parcel in) {
        id = in.readLong();
        name = in.readString();
        timestamp = DateUtils.readDate(in);
        longitude = in.readDouble();
        latitude = in.readDouble();
    }

    public static final Creator<Peer> CREATOR = new Creator<Peer>() {
        @Override
        public Peer createFromParcel(Parcel in) {
            return new Peer(in);
        }

        @Override
        public Peer[] newArray(int size) {
            return new Peer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
        dest.writeLong(id);
        dest.writeString(name);
        DateUtils.writeDate(dest,timestamp);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
    }

    public void writeToProvider(ContentValues values){
        PeerContract.putID(values,this.id);
        PeerContract.putname(values,this.name);
        PeerContract.puttimestamp(values,this.timestamp);
        PeerContract.putLongitude(values,this.longitude);
        PeerContract.putLatitude(values,this.latitude);
    }
}
