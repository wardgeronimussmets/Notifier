import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Scanner;


public class FireBaseManager {
    private volatile int writesLeft = 0;
    public FireBaseManager(){
        try{
            // Fetch the service account key JSON file contents
            FileInputStream serviceAccount = new FileInputStream("privateKeyFirebase.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://notifier-bc85e-default-rtdb.europe-west1.firebasedatabase.app/")
                    .build();

            FirebaseApp.initializeApp(options);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private HashMap<String,Object> getNewMap() {
        HashMap<String, Object> mapTemplate = new HashMap<>();
        mapTemplate.put("Category", "");
        mapTemplate.put("Body", "");
        mapTemplate.put("Link", "");
        return mapTemplate;
    }

    private void writeGameToDB(HashMap<String,Object> map){
        //writing to the database
        writesLeft ++;
        String key = FirebaseDatabase.getInstance().getReference().child("deals").push().getKey();
        FirebaseDatabase.getInstance().getReference().child("deals").child(key).updateChildren(map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                System.out.println("completed" + databaseReference.getKey());
                writesLeft --;
            }
        });
    }

    public void pushFromDump(){
        try {
            BufferedReader file = new BufferedReader(new FileReader("dump.txt"));
            Scanner scanner = new Scanner(file);
            HashMap<String,Object> mapTemplate = getNewMap();
            int currType = 0; //0 = category, 1 = body, 2 = link
            while(scanner.hasNext()){
                String next = scanner.nextLine();
                if(currType == 0){
                    mapTemplate.replace("Category",next);
                }
                else if(currType == 1){
                    mapTemplate.replace("Body",next);
                }
                else if(currType == 2){
                    mapTemplate.replace("Link",next);
                }
                currType ++;
                if(currType > 2) {
                    currType = 0;
                    writeGameToDB(mapTemplate);
                    mapTemplate = getNewMap();
                }
            }


        }
        catch(Exception e){
            e.printStackTrace();
        }
        while(writesLeft > 0 ){
            //wait for the writes to be completed
        }
    }
}
