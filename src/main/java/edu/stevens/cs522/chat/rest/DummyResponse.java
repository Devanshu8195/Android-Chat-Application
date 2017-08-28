package edu.stevens.cs522.chat.rest;

import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.chat.util.EnumUtils;

/**
 * Created by dduggan.
 */

public class DummyResponse extends Response implements Parcelable {

    public boolean isValid() {
        return true;
    }

    public DummyResponse(long id) {
        super(id, "", 200, "OK");
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        EnumUtils.writeEnum(out, ResponseType.DUMMY);
        super.writeToParcel(out, flags);
    }

    public DummyResponse(Parcel in) {
        super(in);
    }

}

