import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FireBaseDemo {
    private static volatile boolean notCompletedYet = true;
    public static void main(String[] args) {

        try {
            // Fetch the service account key JSON file contents
            FileInputStream serviceAccount = new FileInputStream("privateKeyFirebase.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://notifier-bc85e-default-rtdb.europe-west1.firebasedatabase.app/")
                    .build();

            FirebaseApp.initializeApp(options);

            DatabaseReference ref =  FirebaseDatabase.getInstance().getReference().child("testing");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    System.out.println("Datasnapshot" + dataSnapshot.toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("Cancelled");
                }
            });

            //writing to the database
            FirebaseDatabase.getInstance().getReference().child("testing").push().child("Name").setValue("Hopelijk de naams", new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    System.out.println("completed" + databaseError + databaseReference);
                    notCompletedYet = false;
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
        while(notCompletedYet){

        }
    }
}


//get notified on change -> usefull in the app //https://www.youtube.com/watch?v=XactTKR0Wfc