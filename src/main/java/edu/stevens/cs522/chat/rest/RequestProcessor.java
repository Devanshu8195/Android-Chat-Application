package edu.stevens.cs522.chat.rest;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;

import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.RequestManager;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.util.StringUtils;

/**
 * Created by dduggan.
 */

public class RequestProcessor {

    private Context context;

    private RestMethod restMethod;

    private RequestManager requestManager;

    ChatMessage message =new ChatMessage();

    Peer peer = new Peer();

    public RequestProcessor(Context context) {
        this.context = context;
        this.restMethod =  new RestMethod(context);
        this.requestManager = new RequestManager(context);
    }

    public Response process(Request request) {
        return request.process(this);
    }

    public Response perform(RegisterRequest request) {
        return restMethod.perform(request);
    }

    public Response perform(PostMessageRequest request) {
        // We will just insert the message into the database, and rely on background sync to upload
        // return restMethod.perform(request)
        requestManager.persist(request.message);
        return request.getDummyResponse();
    }

    public Response perform(SynchronizeRequest request) {
        RestMethod.StreamingResponse response = null;
        final TypedCursor<ChatMessage> messages = requestManager.getUnsentMessages();
        final int numMessagesReplaced = messages.getCount();
        try {
            RestMethod.StreamingOutput out = new RestMethod.StreamingOutput() {
                @Override
                public void write(final OutputStream os) throws IOException {
                    try {
                        JsonWriter wr = new JsonWriter(new OutputStreamWriter(new BufferedOutputStream(os)));
                        wr.beginArray();
                        /*
                         * TODO stream unread messages to the server:
                         * {
                         *   chatroom : ...,
                         *   timestamp : ...,
                         *   latitude : ...,
                         *   longitude : ....,
                         *   text : ...
                         * }
                         */
                        if (messages.getCount() != 0) {
                            messages.moveToFirst();
                            for (int i = 0; i < numMessagesReplaced; i++) {
                                wr.beginObject();
                                wr.name("chatroom");
                                wr.value(messages.getEntity().chatRoom);
                                wr.name("timestamp");
                                wr.value(messages.getEntity().timestamp.getTime());
                                wr.name("latitude");
                                wr.value(messages.getEntity().latitude);
                                wr.name("longitude");
                                wr.value(messages.getEntity().longitude);
                                wr.name("text");
                                wr.value(messages.getEntity().messageText);
                                wr.endObject();
                                messages.moveToNext();
                            }
                        }
                        wr.endArray();
                        wr.flush();
                    } finally {
                        messages.close();
                    }
                }
            };
            response = restMethod.perform(request, out);

            JsonReader rd = new JsonReader(new InputStreamReader(new BufferedInputStream(response.getInputStream()), StringUtils.CHARSET));
            // TODO parse data from server (messages and peers) and update database
            // See RequestManager for operations to help with this.

            requestManager.deletePeers();
            rd.beginObject();
            String client = rd.nextName();
            if ("clients".equals(client)) {
                rd.beginArray();

                while (rd.peek() != JsonToken.END_ARRAY) {
                    rd.beginObject();
                    String st = rd.nextName();
                    if ("username".equals(st)) {
                        peer.name = rd.nextString();
                    }
                    String d = rd.nextName();
                    if ("timestamp".equals(d)) {
                        peer.timestamp = new Date(rd.nextLong());
                    }
                    String lat = rd.nextName();
                    if ("latitude".equals(lat)) {
                        peer.latitude = rd.nextDouble();
                    }
                    String db = rd.nextName();
                    if ("longitude".equals(db)) {
                        peer.longitude = rd.nextDouble();
                    }
                    rd.endObject();
                    if (peer.name != null) {
                        requestManager.persist(peer);
                    }
                }
                rd.endArray();
            }
            requestManager.persist(peer);
            String message1 = rd.nextName();
            ArrayList<ChatMessage> listmsg = new ArrayList<>();;
            if ("messages".equals(message1)) {
                rd.beginArray();
                while (rd.peek() != JsonToken.END_ARRAY) {
                    rd.beginObject();
                    String st1 = rd.nextName();
                    if ("chatroom".equals(st1)) {
                        message.chatRoom = rd.nextString();
                    }
                    String d = rd.nextName();
                    if ("timestamp".equals(d)) {
                        message.timestamp = new Date(rd.nextLong());
                    }
                    String lat = rd.nextName();
                    if ("latitude".equals(lat)) {
                        message.latitude = rd.nextDouble();
                    }
                    String db = rd.nextName();
                    if ("longitude".equals(db)) {
                        message.longitude = rd.nextDouble();
                    }
                    String l = rd.nextName();
                    if ("seqnum".equals(l)) {
                        message.seqNum = rd.nextLong();
                    }
                    String st3 = rd.nextName();
                    if ("sender".equals(st3)) {
                        message.sender = rd.nextString();
                    }
                    String st2 = rd.nextName();
                    if ("text".equals(st2)) {
                        message.messageText = rd.nextString();
                    }
                    if(message.seqNum > requestManager.getLastSequenceNumber())
                    {
                        listmsg.add(message);
                    }
                    rd.endObject();
                }
                rd.endArray();
                requestManager.syncMessages(numMessagesReplaced, listmsg);
            }
            rd.endObject();
            return response.getResponse();

        } catch (IOException e) {
            return new ErrorResponse(request.id, e);

        } finally {
            if (response != null) {
                response.disconnect();
            }
        }
    }

}
