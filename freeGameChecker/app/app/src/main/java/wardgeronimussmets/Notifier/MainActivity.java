package wardgeronimussmets.Notifier;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ConditionVariable;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkAccount();

    }

    private String checkAccount(){
        String user = new SharedLoader(getApplicationContext()).getUserName();
        if(user == null){
            Intent intent = new Intent(getApplicationContext(),NewUserActivity.class);
            startActivity(intent);
        }
        else{
            System.out.println(user);
        }
        return user;
    }


}