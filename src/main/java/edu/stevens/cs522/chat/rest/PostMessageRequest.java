package edu.stevens.cs522.chat.rest;

import android.net.Uri;
import android.os.Parcel;
import android.util.JsonReader;
import android.util.JsonWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.util.DateUtils;
import edu.stevens.cs522.chat.util.EnumUtils;

/**
 * Created by dduggan.
 */

public class PostMessageRequest extends Request {

    public ChatMessage message;
    public String Chatroom;
    public String messagetext;

    public PostMessageRequest(ChatMessage message) {
        super();
        this.message = message;
        this.Chatroom = message.chatRoom;
        this.messagetext = message.messageText;
    }

    @Override
    public String getRequestEntity() throws IOException {
        StringWriter wr = new StringWriter();
        JsonWriter jw = new JsonWriter(wr);
        // TODO write a JSON message of the form:
        // { "room" : <chat-room-name>, "message" : <message-text> }
        jw.beginObject();
        jw.name("chatroom");
        jw.value(message.chatRoom);
        jw.name("text");
        jw.value(message.messageText);
        jw.endObject();
        return wr.toString();
    }

    @Override
    public Response getResponse(HttpURLConnection connection, JsonReader rd) throws IOException{
        throw new IllegalStateException("PostMessage request should only return dummy response");
    }

    public Response getDummyResponse() {
        return new DummyResponse(id);
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
        EnumUtils.writeEnum(dest, RequestType.POST_MESSAGE);
        super.writeToParcel(dest,flags);
        dest.writeParcelable(message, flags);
        dest.writeString(message.chatRoom);
        dest.writeString(message.messageText);
    }
    public PostMessageRequest(Parcel in) {
        super(in);
        // TODO
        message = in.readParcelable(ChatMessage.class.getClassLoader());
        Chatroom = in.readString();
        messagetext = in.readString();
    }
}
