package com.hyunjunian.www.justamemo;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hyunjunian on 06/01/2017.
 */

public class Memo {

    public String uid;
    public String author;
    public String body;
    public String date;

    public Memo() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Memo(String uid, String author, String body, String date) {
        this.uid = uid;
        this.author = author;
        this.body = body;
        this.date = date;
    }

    // [START memo_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("body", body);
        result.put("date", date);

        return result;
    }
    // [END memo_to_map]

}
