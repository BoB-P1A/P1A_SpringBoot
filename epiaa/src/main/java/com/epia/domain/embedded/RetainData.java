package com.epia.domain.embedded;

import org.bson.types.ObjectId;

public class RetainData {
    public ObjectId _id;
    public String retain_task;
    public String retain_input_system;
    public String retain_space;
    public String retain_form;
    public String retain_purpose;
    public String retain_bundle;
    public String retain_items;
    public Boolean retain_online;
    public Boolean retain_encrypt;
    public String retain_enc_items;

    public RetainData() {
        this._id = new ObjectId();
        this.retain_online = false;
        this.retain_encrypt = false;
    }
}