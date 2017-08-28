package edu.stevens.cs522.chat.rest;

import android.net.Uri;
import android.os.Parcel;
import android.util.JsonReader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.stevens.cs522.chat.util.EnumUtils;

/**
 * Created by dduggan.
 */

public class RegisterRequest extends Request {

    public String chatName;

    public RegisterRequest(String chatName) {
        super();
        this.chatName = chatName;
    }

    @Override
    public String getRequestEntity() throws IOException {
        return null;
    }

    @Override
    public Response getResponse(HttpURLConnection connection, JsonReader rd) throws IOException{
        return new RegisterResponse(connection);
    }

    @Override
    public Response process(RequestProcessor processor) {
        return processor.perform(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
        EnumUtils.writeEnum(dest,RequestType.REGISTER);
        super.writeToParcel(dest, flags);
        dest.writeString(chatName);
    }

    public RegisterRequest(Parcel in) {
        super(in);
        // TODO
        chatName = in.readString();
    }

}
