package wardgeronimussmets.Notifier;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedLoader {
    private SharedPreferences userData;
    private Context context;

    private final String notificationKey = "notifications";
    public SharedLoader(Context context){
        this.context = context;
        userData = context.getSharedPreferences("userData",MODE_PRIVATE);
    }
//    public void forceUserKey(String forcedKey){
//        SharedPreferences.Editor editor = userData.edit();
//        editor.putString("userKey",forcedKey);
//        editor.apply();
//    }

    public String getUserName(){
        return userData.getString("userKey",null);
    }

    public void putNewUser(String userKey){
        SharedPreferences.Editor editor = userData.edit();
        editor.putString("userKey",userKey);
        editor.apply();
    }

    private SharedPreferences getGameDealsData(){
        return context.getSharedPreferences("gameDealsData",MODE_PRIVATE);
    }

    public boolean isGameDealsNotificationsRunning(){
        SharedPreferences pref = getGameDealsData();
        if(pref.getBoolean(notificationKey,false)){
            //have to still load them
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(notificationKey,true);
            editor.apply();
            return false;
        }
        else{
            return true;
        }

    }


}
