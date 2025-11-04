package com.epia.seq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class SequenceService {

    @Autowired
    MongoOperations mongo;

    public int next(String name) {
        var query = new Query(Criteria.where("_id").is(name));
        var update = new Update().inc("seq", 1);
        var options = FindAndModifyOptions.options().upsert(true).returnNew(true);

        Counter c = mongo.findAndModify(query, update, options, Counter.class);
        return c.seq;
    }

    static class Counter {
        public String _id;
        public int seq;
    }
}