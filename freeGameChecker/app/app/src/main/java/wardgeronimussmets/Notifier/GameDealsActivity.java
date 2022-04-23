package wardgeronimussmets.Notifier;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class GameDealsActivity extends AppCompatActivity implements GameDealsReturner {
    private FireBaseManager fireBaseManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_deals);
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
            for(GameDeal deal: deals){
                if(deal == null || deal.getBody() == null){
                    Log.v("Markel","Deals is null");
                }
                else
                    Log.v("Markel",deal.getBody());
            }
        }
    }
}
