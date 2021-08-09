package com.tavaresrit.petshop.models;


import java.util.HashMap;
import java.util.Map;

public class Post {

    public String uid;
    public String author;
    public String title;
    public String body;

    public Post() {
        // Construtor padrão necessário para chamadas para DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String author, String title, String body) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();

            result.put("uid", uid);
            result.put("author", author);
            result.put("title", title);
            result.put("body", body);

        return  result;


     }

}
