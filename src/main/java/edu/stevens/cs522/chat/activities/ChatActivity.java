/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender chatName and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;
import java.util.UUID;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.async.IContinue;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.rest.ChatHelper;
import edu.stevens.cs522.chat.rest.ServiceManager;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.util.InetAddressUtils;
import edu.stevens.cs522.chat.util.ResultReceiverWrapper;

public class ChatActivity extends Activity implements OnClickListener, QueryBuilder.IQueryListener<ChatMessage>, ResultReceiverWrapper.IReceive {

	final static public String TAG = ChatActivity.class.getCanonicalName();
		
    /*
     * UI for displaying received messages
     */
	private SimpleCursorAdapter messages;
	
	private ListView messageList;

    private SimpleCursorAdapter messagesAdapter;

    private MessageManager messageManager;

    private PeerManager peerManager;

    private ServiceManager serviceManager;

    /*
     * Widgets for dest address, message text, send button.
     */
    private EditText chatRoomName;

    private EditText messageText;

    private Button sendButton;


    /*
     * Helper for Web service
     */
    private ChatHelper helper;

    /*
     * For receiving ack when message is sent.
     */
    private ResultReceiverWrapper sendResultReceiver;
	
	/*
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.messages);

        messageList = (ListView) findViewById(R.id.message_list);
        messageText = (EditText) findViewById(R.id.message_text);
        sendButton = (Button) findViewById(R.id.send_button);
        chatRoomName = (EditText) findViewById(R.id.chat_room);

        // TODO use SimpleCursorAdapter to display the messages received.
        String[] from = new String[]{MessageContract.SENDER,MessageContract.MESSAGE_TEXT};
        int[] to = new int[]{android.R.id.text1,android.R.id.text2};

        messagesAdapter = new SimpleCursorAdapter(this,android.R.layout.simple_list_item_2,null,from,to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        messageList.setAdapter(messagesAdapter);
        sendButton.setOnClickListener(this);
        // TODO create the message and peer managers, and initiate a query for all messages
        messageManager = new MessageManager(this);
        messageManager.getAllMessagesAsync(this);
        peerManager = new PeerManager(this);
        // TODO instantiate helper for service
        helper = new ChatHelper(this);
        // TODO initialize sendResultReceiver and serviceManager
        sendResultReceiver = new ResultReceiverWrapper(new Handler());
        sendResultReceiver.setReceiver(this);
        serviceManager = new ServiceManager(this);

        /**
         * Initialize settings to default values.
         */
        if (!Settings.isRegistered(this)) {
            // Launch registration activity
            Settings.getClientId(this);
            startActivity(new Intent(this, RegisterActivity.class));
        }

    }

	public void onResume() {
        super.onResume();
        sendResultReceiver.setReceiver(this);
        serviceManager.scheduleBackgroundOperations();
    }

    public void onPause() {
        super.onPause();
        sendResultReceiver.setReceiver(null);
        serviceManager.cancelBackgroundOperations();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // TODO inflate a menu with PEERS and SETTINGS options
        getMenuInflater().inflate(R.menu.chatserver_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            // TODO PEERS provide the UI for viewing list of peers
            case R.id.peers:
                Intent intent2 = new Intent(this,ViewPeersActivity.class);
                startActivity(intent2);
                break;

            // TODO SETTINGS provide the UI for settings
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            default:
        }
        return false;
    }



    /*
     * Callback for the SEND button.
     */
    public void onClick(View v) {
        if (helper != null) {

            String chatRoom;

            String message;

            String username;

            // TODO get chatRoom and message from UI, and use helper to post a message
            chatRoom = chatRoomName.getText().toString();
            message = messageText.getText().toString();
            username = Settings.getChatName(this);

            // TODO add the message to the database
            helper.postMessage(chatRoom,message, sendResultReceiver);

            final Peer sender = new Peer();
            sender.name = username;
            sender.timestamp = new Date(System.currentTimeMillis());
            sender.longitude = 0.0;
            sender.latitude = 0.0;


            final ChatMessage message1 = new ChatMessage();
            message1.messageText = message;
            message1.sender = username;
            message1.timestamp = new Date(System.currentTimeMillis());
            message1.longitude = 0.0;
            message1.latitude = 0.0;


//            peerManager.persistAsync(sender, new IContinue<Long>() {
//                @Override
//                public void kontinue(Long id) {
//                   // message1.senderId = id;
//                    messageManager.persistAsync(message1);
//                }
//            });


            // End todo

            Log.i(TAG, "Sent message: " + message);

            messageText.setText("");
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        switch (resultCode) {
            case RESULT_OK:
                // TODO show a success toast message
                Toast.makeText(this,"post message success!",Toast.LENGTH_LONG).show();
                break;
            default:
                // TODO show a failure toast message
                Toast.makeText(this,"failure!",Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void handleResults(TypedCursor<ChatMessage> results) {
        // TODO
        this.messagesAdapter.swapCursor(results.getCursor());
    }

    @Override
    public void closeResults() {
        // TODO
        this.messagesAdapter.swapCursor(null);
    }

}