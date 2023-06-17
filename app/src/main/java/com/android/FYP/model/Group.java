package com.android.FYP.model;



public class Group extends Room {
    public String id;
    public ListFriend listFriend;

    public Group(){
        listFriend = new ListFriend();
    }
}
