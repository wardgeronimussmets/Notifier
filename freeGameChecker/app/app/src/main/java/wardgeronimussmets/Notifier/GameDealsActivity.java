package wardgeronimussmets.Notifier;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class GameDealsActivity extends AppCompatActivity implements GameDealsReturner {
    private FireBaseManager fireBaseManager;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_deals);
        recyclerView = findViewById(R.id.gameRecycler);
        fireBaseManager = new FireBaseManager();
        loadGameNotifications();
    }

    private void loadGameNotifications(){
        String user = new SharedLoader(getApplicationContext()).getUserName();
        fireBaseManager.getGameDeals(user,this);
    }


    @Override
    public void returnDeals(ArrayList<GameDeal> deals) {
        if(deals == null){
            Toast.makeText(getApplicationContext(),"No new games",Toast.LENGTH_LONG).show();
        }
        else{
            setAdapter(deals);
        }
    }

    private void setAdapter(ArrayList<GameDeal> gameDeals){
        MyRecyclerAdapter adapter = new MyRecyclerAdapter(gameDeals,getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }
}
