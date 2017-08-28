package edu.stevens.cs522.chat.rest;

import android.os.Parcel;
import android.util.JsonReader;
import android.util.JsonWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;

import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.util.EnumUtils;

/**
 * Created by dduggan.
 */

public class SynchronizeRequest extends Request {

    public ChatMessage message;

    // Added by request processor
    public long lastSequenceNumber;

    public SynchronizeRequest(ChatMessage message) {
        super();
        this.message = message;
    }

    @Override
    public String getRequestEntity() throws IOException {
        // We stream output for SYNC, so this always returns null
        return null;
    }

    @Override
    public Response getResponse(HttpURLConnection connection, JsonReader rd) throws IOException{
        assert rd == null;
        return new SynchronizeResponse(connection);
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
        EnumUtils.writeEnum(dest,RequestType.SYNCHRONIZE);
        super.writeToParcel(dest, flags);
    }

    public SynchronizeRequest() {
        super();
    }

    public SynchronizeRequest(Parcel in) {
        super(in);
        // TODO
    }

}
