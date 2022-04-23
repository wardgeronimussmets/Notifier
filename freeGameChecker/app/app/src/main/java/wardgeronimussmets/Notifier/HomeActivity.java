package wardgeronimussmets.Notifier;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
    }

    public void gameDeals(View Caller){
        Intent intent = new Intent(getApplicationContext(),GameDealsActivity.class);
        startActivity(intent);
    }
}
