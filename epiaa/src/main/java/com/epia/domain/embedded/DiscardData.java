package com.epia.domain.embedded;

import org.bson.types.ObjectId;

public class DiscardData {
    public ObjectId _id;
    public String discard_task;
    public String discard_space;
    public String discard_system;
    public String discard_period;
    public String discard_dept;
    public String discard_proc;
    public String discard_bundle;
    public String discard_items;
    public Boolean discard_online;

    public DiscardData() {
        this._id = new ObjectId();
        this.discard_online = false;
    }
}