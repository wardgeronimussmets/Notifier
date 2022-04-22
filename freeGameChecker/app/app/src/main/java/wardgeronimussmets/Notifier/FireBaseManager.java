package wardgeronimussmets.Notifier;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
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
}
