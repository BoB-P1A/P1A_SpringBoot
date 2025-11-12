package com.epia.domain.embedded;

import org.bson.types.ObjectId;

public class UseData {
    public ObjectId _id;
    public String use_task;
    public String use_space;
    public String use_system;
    public String use_dept;
    public String use_purpose;
    public String use_method;
    public String use_bundle;
    public String use_items;
    public Boolean use_online;
    public Boolean use_encrypt;

    public UseData() {
        this._id = new ObjectId();
        this.use_online = false;
        this.use_encrypt = false;
    }
}