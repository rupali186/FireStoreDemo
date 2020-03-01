package com.android.rupali.myapplication;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Note {
    private String title,msg,id;

    public Note() {
    }

    public Note(String title, String msg) {
        this.title = title;
        this.msg = msg;
    }

    public Note(String title, String msg, String id) {
        this.title = title;
        this.msg = msg;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
