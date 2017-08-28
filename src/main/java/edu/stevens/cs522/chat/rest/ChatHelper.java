package edu.stevens.cs522.chat.rest;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

import java.util.Date;
import java.util.UUID;

import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.util.ResultReceiverWrapper;


/**
 * Created by dduggan.
 */

public class ChatHelper {

    public static final String DEFAULT_CHAT_ROOM = "_default";

    private Context context;

    private String chatName;

    private UUID clientID;

    public ChatHelper(Context context) {
        this.context = context;
        this.chatName = Settings.getChatName(context);
        this.clientID = Settings.getClientId(context);
    }

    // TODO provide a result receiver that will display a toast message upon completion
    public void register (String chatName, ResultReceiverWrapper receiver) {
        if (chatName != null && !chatName.isEmpty()) {
            RegisterRequest request = new RegisterRequest(chatName);
            this.chatName = chatName;
            Settings.saveChatName(context, chatName);
            addRequest(request, receiver);
        }
    }

    // TODO provide a result receiver that will display a toast message upon completion
    public void postMessage (String chatRoom, String text, ResultReceiverWrapper receiver) {
        if (text != null && !text.isEmpty()) {
            if (chatRoom == null || chatRoom.isEmpty()) {
                chatRoom = DEFAULT_CHAT_ROOM;
            }
            ChatMessage message = new ChatMessage();
            message.chatRoom = chatRoom;
            message.timestamp = new Date();
            message.messageText = text;
            message.sender = Settings.getChatName(context);
            PostMessageRequest request = new PostMessageRequest(message);
            addRequest(request, receiver);
        }
    }

    private void addRequest(Request request, ResultReceiver receiver) {
        context.startService(createIntent(context, request, receiver));
    }

   // private void addRequest(Request request) {
   //     addRequest(request, null);
   // }

    /**
     * Use an intent to send the request to a background service. The request is included as a Parcelable extra in
     * the intent. The key for the intent extra is in the RequestService class.
     */
    public static Intent createIntent(Context context, Request request, ResultReceiver receiver) {
        if(request == null){
            System.out.println("null request");
        }
        Intent requestIntent = new Intent(context, RequestService.class);
        requestIntent.putExtra(RequestService.SERVICE_REQUEST_KEY, request);
        if (receiver != null) {
            System.out.println("Not null");
            requestIntent.putExtra(RequestService.RESULT_RECEIVER_KEY, receiver);
        }
        return requestIntent;
    }

//    public static Intent createIntent(Context context, Request request) {
//        return createIntent(context, request, null);
//    }

}
