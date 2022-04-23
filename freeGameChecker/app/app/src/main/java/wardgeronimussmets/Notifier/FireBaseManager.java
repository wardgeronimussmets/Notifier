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

    public void removeGameDeal(String gameKey,String user){
        Log.v("Markel",gameKey);
        database.child("unseenDeals").orderByChild("dealId").equalTo(gameKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    if(snapshot1.child("userId").getValue().toString().equals(user)){
                        removeUnseenDeal(snapshot1.getKey());
                    }
                    counter ++;
                }
                if(snapshot.getChildrenCount() ==1){
                    //last one -> game can be removed from deals as well
                    removeDeal(gameKey);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Markel","Failed to retrieve in removeGameDeal with key: " + gameKey + " and user: " + user);
            }
        });

    }

    private void removeUnseenDeal(String unseenKey){
        database.child("unseenDeals").child(unseenKey).removeValue();
        Log.v("Markel","We will try to remove the following key " + unseenKey);
    }
    private void removeDeal(String gameKey){
        database.child("deals").child(gameKey).removeValue();
        Log.v("Markel","Trying to remove the following key " + gameKey);
    }
}
