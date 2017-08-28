package edu.stevens.cs522.chat.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.contracts.PeerContract;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.managers.TypedCursor;

import static edu.stevens.cs522.chat.R.id.peerList;


public class ViewPeersActivity extends Activity implements AdapterView.OnItemClickListener, QueryBuilder.IQueryListener<Peer> {

    /*
     * TODO See ChatActivity for example of what to do, query peers database instead of messages database.
     */

    private PeerManager peerManager;

    private SimpleCursorAdapter peerAdapter;

    private ListView peer_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);

        // TODO initialize peerAdapter with empty cursor (null)
        String[] from = new String[]{PeerContract.NAME};
        int[] to = new int[]{android.R.id.text1};
        peerAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, from, to);

        peer_list = (ListView) findViewById(R.id.peerList);
        peer_list.setAdapter(peerAdapter);

        peer_list.setOnItemClickListener(this);

        peerManager = new PeerManager(this);
        peerManager.getAllPeersAsync(this);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
         * Clicking on a peer brings up details
         */
        Cursor cursor = peerAdapter.getCursor();
        if (cursor.moveToPosition(position)) {
            Intent intent = new Intent(this, ViewPeerActivity.class);
            Peer peer = new Peer(cursor);
            intent.putExtra(ViewPeerActivity.PEER_KEY, peer);
            startActivity(intent);
        } else {
            throw new IllegalStateException("Unable to move to position in cursor: "+position);
        }
    }

    @Override
    public void handleResults(TypedCursor<Peer> results) {
        // TODO
        peerAdapter.swapCursor(results.getCursor());
    }

    @Override
    public void closeResults() {
        // TODO
        peerAdapter.swapCursor(null);
    }
}
