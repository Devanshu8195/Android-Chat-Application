package edu.stevens.cs522.chat.async;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import edu.stevens.cs522.chat.managers.TypedCursor;

/**
 * Created by dduggan.
 */

public class QueryBuilder<T> implements LoaderManager.LoaderCallbacks<Cursor> {

    String tag;
    Context context;
    Uri uri;
    int loaderID;
    IEntityCreator<T>	creator;
    IQueryListener<T>	listener;

    public QueryBuilder(String tag,
                        Activity context,
                        Uri uri,
                        int loaderID,
                        IEntityCreator creator,
                        IQueryListener listener) {
        this.tag = tag;
        this.context = context;
        this.uri = uri;
        this.loaderID = loaderID;
        this.creator = creator;
        this.listener = listener;
    }


    public static interface IQueryListener<T> {

        public void handleResults(TypedCursor<T> results);

        public void closeResults();

    }

    public	static	<T>	void executeQuery(String tag,
                                                 Activity context, Uri uri,
                                                 int loaderID,
                                                 IEntityCreator	creator,
                                                 IQueryListener	listener) {
        QueryBuilder<T> qb = new QueryBuilder<T>(tag, context, uri, loaderID, creator, listener);

        LoaderManager lm = context.getLoaderManager();
        lm.initLoader(loaderID,	null, qb);
    }

    // TODO complete the implementation of this

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if	(id	==	loaderID) {
            return new CursorLoader(context, uri, null, null, null, null);
        }else {
            throw new IllegalStateException("Unexpected loader callback");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if	(loader.getId()	==	loaderID)	{
            listener.handleResults(new	TypedCursor<T>((Cursor) data, creator));
        }else{
            throw	new	IllegalStateException("Unexpected	loader	callback");
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        if	(loader.getId()	==	loaderID)	{
            listener.closeResults();
        }else{
            throw	new	IllegalStateException("Unexpected	loader	callback");
        }
    }
}
