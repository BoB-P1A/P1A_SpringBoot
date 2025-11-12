
package com.epia.domain.embedded;

import org.bson.types.ObjectId;

public class CollectData {
    public ObjectId _id;
    public String collect_task;
    public String collect_target;
    public String collect_route;
    public String collect_dept;
    public String collect_purpose;
    public String collect_system;
    public String collect_bundle;
    public String collect_items;
    public Boolean collect_online;
    public Boolean collect_encrypt;

    public CollectData() {
        this._id = new ObjectId();
        this.collect_online = false;
        this.collect_encrypt = false;
    }
}