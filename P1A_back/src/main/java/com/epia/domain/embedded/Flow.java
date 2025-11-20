
package com.epia.domain.embedded;

import org.bson.types.ObjectId;

public class Flow {
    public ObjectId _id;
    public Sheets sheets;

    public Flow() {
        this._id = new ObjectId();
        this.sheets = new Sheets();
    }
}