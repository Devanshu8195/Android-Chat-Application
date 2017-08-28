package edu.stevens.cs522.chat.rest;

import android.os.Parcel;
import android.util.JsonReader;

import java.io.IOException;
import java.net.HttpURLConnection;

import edu.stevens.cs522.chat.util.EnumUtils;

/**
 * Created by dduggan.
 */

public class SynchronizeResponse extends Response {

    public static final String ID_LABEL = "id";

    public SynchronizeResponse(HttpURLConnection connection) throws IOException {
        super(connection);
    }

    @Override
    public boolean isValid() { return true; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
        EnumUtils.writeEnum(dest, Request.RequestType.SYNCHRONIZE);
        super.writeToParcel(dest,flags);
    }

    public SynchronizeResponse(Parcel in) {
        super(in);
        // TODO
    }
}
