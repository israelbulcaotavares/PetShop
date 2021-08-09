package com.tavaresrit.petshop.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class Firebase {

    public Firebase() {
    }

    public static Query getQuery(DatabaseReference databaseReference){
        return databaseReference.child("user-posts").child(getUid());
    }

    public static String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}
