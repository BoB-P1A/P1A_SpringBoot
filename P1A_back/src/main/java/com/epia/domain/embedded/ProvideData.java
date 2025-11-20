package com.epia.domain.embedded;

import org.bson.types.ObjectId;

public class ProvideData {
    public ObjectId _id;
    public String provide_task;
    public String provide_space;
    public String provide_dept;
    public String provide_system;
    public Boolean provide_sys_online;
    public Boolean provide_sys_encrypt;
    public String receiver;
    public String provide_bundle;
    public String provide_items;
    public String provide_purpose;
    public String provide_method;
    public Boolean receiver_online;
    public Boolean receiver_encrypt;

    public ProvideData() {
        this._id = new ObjectId();
        this.provide_sys_online = false;
        this.provide_sys_encrypt = false;
        this.receiver_online = false;
        this.receiver_encrypt = false;
    }
}