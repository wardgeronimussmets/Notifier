import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class FireBaseManager {
    private volatile int writesLeft = 0;
    private volatile ArrayList<String> users = new ArrayList<>(5); //capacity is just a random guess
    private volatile  boolean finishedLoadingUsers = false;
    private final String privateKey = "./privateKeyFirebase.json";
    private  final String dumpFile = "./dump.txt";
    private EmailManager emailManager ;
    public FireBaseManager(){
        try{
            //initialize emailManager
            emailManager = new EmailManager();
            // Fetch the service account key JSON file contents
            FileInputStream serviceAccount = new FileInputStream(privateKey);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://notifier-bc85e-default-rtdb.europe-west1.firebasedatabase.app/")
                    .build();

            FirebaseApp.initializeApp(options);

            loadUserIds();
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
        //writing to email
        emailManager.appendMessage(map);
        //writing to the database
        writesLeft ++;
        String key = FirebaseDatabase.getInstance().getReference().child("deals").push().getKey();
        FirebaseDatabase.getInstance().getReference().child("deals").child(key).updateChildren(map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                addGamesToUsers(databaseReference.getKey());

            }
        });
    }

    private void loadUserIds(){
        DatabaseReference collection = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = collection.orderByKey();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> snaps = dataSnapshot.getChildren();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    users.add(snapshot.getKey());
                }
                finishedLoadingUsers = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Failed to get from database");
                finishedLoadingUsers = true;
            }
        });
    }

    private void addGamesToUsers(String gameKey){
        while(!finishedLoadingUsers){
            try {
                Thread.sleep(10);
            }
            catch (Exception e){
                System.out.println("Failed to sleep");
            }
        }
        for(String user: users) {
            writesLeft ++;
            HashMap<String, Object> map = new HashMap<>(2);
            map.put("dealId", gameKey);
            map.put("userId", user);
            String key = FirebaseDatabase.getInstance().getReference().child("unseenDeals").push().getKey();
            FirebaseDatabase.getInstance().getReference().child("unseenDeals").child(key).updateChildren(map, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    writesLeft --;
                }
            });
        }
        writesLeft --;
    }

    public void pushFromDump(){
        try {
            BufferedReader file = new BufferedReader(new FileReader(dumpFile));
            Scanner scanner = new Scanner(file);
            HashMap<String,Object> mapTemplate = getNewMap();
            int currType = 0; //0 = category, 1 = body, 2 = link
            boolean newInfo = scanner.hasNextLine();
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
                    System.out.println("Writing "+ mapTemplate.values() + " to database");
                    mapTemplate = getNewMap();

                }
            }
            //send all received games via email
            if(newInfo)
                emailManager.sendMessage();
            //finished reading
            clearDump();


        }
        catch(Exception e){
            e.printStackTrace();
        }
        Instant start = Instant.now();
        while(writesLeft > 0 ){
            //wait for the writes to be completed
            try{
                Thread.sleep(1000);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            if(Duration.between(start,Instant.now()).compareTo(Duration.ofSeconds(60))>0){ //the comparator value, negative if less, positive if greater
                System.out.println("Waited for a minute but still no write -> let's just assume everything was written");
                System.exit(0);
            }
        }
    }
    private void clearDump(){
        try {

            File file = new File(dumpFile);
            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
