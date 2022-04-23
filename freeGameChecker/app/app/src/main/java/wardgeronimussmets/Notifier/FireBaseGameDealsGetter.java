package wardgeronimussmets.Notifier;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class FireBaseGameDealsGetter {
    private volatile ArrayList<GameDeal> gameDeals;
    private GameDealsReturner gameDealsReturner;
    private DatabaseReference database;
    private volatile int numberOfTasks = 0;

    public FireBaseGameDealsGetter(String user,GameDealsReturner gameDealsReturner,DatabaseReference database){

        gameDeals = new ArrayList<>();
        this.gameDealsReturner = gameDealsReturner;
        this.database = database;

        database.child("unseenDeals").orderByChild("userId").equalTo(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<GameDeal> deals = new ArrayList<>();

                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while(iterator.hasNext()){
                    numberOfTasks ++;
                    DataSnapshot snapshot = iterator.next();
                    Object gameId = snapshot.child("dealId").getValue();
                    getGameFromId(gameId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Markel","Failed to retrieve " + user + "'s games");
            }
        });
    }

    public void getGameFromId(Object gameId){
        database.child("deals").child(gameId.toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.v("Markel",gameId.toString() + "number of tasks = "+ Integer.toString(numberOfTasks));
                GameDeal deal = dataSnapshot.getValue(GameDeal.class);
                gameDeals.add(deal);
                numberOfTasks --;
                if(numberOfTasks <= 0){
                    pushGames();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Markel","Couldn't find the following game " + gameId.toString());
            }
        });
    }

    public void pushGames(){
        gameDealsReturner.returnDeals(gameDeals);
    }


}
