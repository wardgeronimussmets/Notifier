package wardgeronimussmets.Notifier;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class FireBaseManager {
    private DatabaseReference database;

    public FireBaseManager(){
        try{
            database = FirebaseDatabase.getInstance().getReference();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public String createNewUser(String name){
        String newKey = database.child("users").push().getKey();
        HashMap<String,Object> map = new HashMap<>(1);
        map.put("name",name);
        database.child("users").child(newKey).updateChildren(map);
        return newKey;
    }

    public void getGameDeals(String user,GameDealsReturner returner){
        new FireBaseGameDealsGetter(user,returner,database);
    }

}
