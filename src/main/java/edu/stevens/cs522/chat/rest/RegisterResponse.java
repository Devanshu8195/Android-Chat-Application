package edu.stevens.cs522.chat.rest;

import android.os.Parcel;

import java.io.IOException;
import java.net.HttpURLConnection;

import edu.stevens.cs522.chat.util.EnumUtils;

/**
 * Created by dduggan.
 */

public class RegisterResponse extends Response {

    public RegisterResponse(HttpURLConnection connection) throws IOException {
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
        EnumUtils.writeEnum(dest, Request.RequestType.REGISTER);
        super.writeToParcel(dest,flags);
    }

    public RegisterResponse(Parcel in) {
        super(in);
        // TODO
    }
}
