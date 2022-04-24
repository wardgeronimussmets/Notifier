package wardgeronimussmets.Notifier;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GameDealsActivity extends AppCompatActivity implements GameDealsReturner,GameCategoryRemoverInterface {
    private FireBaseManager fireBaseManager;
    private RecyclerView recyclerView;
    private MyRecyclerAdapter adapter;
    private TextView no_games;
    private TextView no_games_rather;
    private TextView no_games_quote;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_deals);
        recyclerView = findViewById(R.id.gameRecycler);
        no_games = findViewById(R.id.no_games_tv);
        no_games_rather = findViewById(R.id.no_games_tv_rather);
        no_games_quote = findViewById(R.id.no_games_tv_quote);
        no_games.setVisibility(View.GONE);
        no_games_quote.setVisibility(View.GONE);
        no_games_rather.setVisibility(View.GONE);
        fireBaseManager = new FireBaseManager();


        String user = new SharedLoader(getApplicationContext()).getUserName();
        setupNotifications();
        loadGameDeals(user);
    }

    private void loadGameDeals(String user){
        fireBaseManager.getGameDeals(user,this);
    }


    @Override
    public void returnDeals(ArrayList<GameDeal> deals) {
        if(deals == null){
            no_games.setVisibility(View.VISIBLE);
            no_games_quote.setVisibility(View.VISIBLE);
            no_games_rather.setVisibility(View.VISIBLE);
        }
        else{
            setAdapter(deals);
        }
    }

    private void setAdapter(ArrayList<GameDeal> gameDeals){
        adapter = new MyRecyclerAdapter(gameDeals,this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void notifyAdapterRemoved(int position) {
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void startFireBaseRemoval(String key) {
        new FireBaseManager().removeGameDeal(key,new SharedLoader(getApplicationContext()).getUserName());
    }

    private void setupNotifications(){
        new SharedLoader(getApplicationContext()).isGameDealsNotificationsRunning(); //makes to true if it ain't yet
//        try{
//            stopService(new Intent(getApplicationContext(),MyService.class));
//
//        }
//        catch (Exception e){
//            Log.v("Markel","Failed to remove service");
//        }
        Thread thread = new Thread(){ //start service in new thread
            @Override
            public void run() {
                startService(new Intent(getApplicationContext(),MyService.class));
            }
        };
        thread.start();
    }

}
