package wardgeronimussmets.Notifier;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedLoader {
    private SharedPreferences userData;
    public SharedLoader(Context context){
        userData = context.getSharedPreferences("userData",MODE_PRIVATE);
    }

    public String getUserName(){
        return userData.getString("userKey",null);
    }

    public void putNewUser(String userKey){
        SharedPreferences.Editor editor = userData.edit();
        editor.putString("userKey",userKey);
        editor.apply();
    }


}
