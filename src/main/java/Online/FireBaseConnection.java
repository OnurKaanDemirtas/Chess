package Online;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;

public class FireBaseConnection {
    private static Firestore database;

    public static Firestore getDatabase() {
        if(database==null){
            try {
                FileInputStream serviceAccount =
                        new FileInputStream("src/main/resources/fireStoreConnectionKey.json");

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);

                System.out.println("Firebase bağlantısı başarılı!");

                database = FirestoreClient.getFirestore();

            } catch (Exception e) {
                System.out.println("Firebase bağlantısı başarısız: " + e.getMessage());
            }
        }
        return database;
    }

    public FireBaseConnection() {
    }
}
