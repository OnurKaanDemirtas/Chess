package Online;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;

/**
 * Holds the single Firestore connection used by the whole app.
 *
 * The service-account key is read from the FIREBASE_KEY_PATH environment variable,
 * or from src/main/resources/fireStoreConnectionKey.json if the variable is not set.
 * The key file is gitignored and must never be committed.
 *
 * Firestore layout used by the game:
 * <pre>
 * DatabaseSize/size                         -> { size }  counter used to give new accounts an id
 * accounts/account{id}                      -> Account
 * accounts/account{id}/gameArchive/{n }     -> Game  (n = the player's n-th game, note the trailing space)
 * accounts/account{id}/gameArchive/{n }/moves/{001,002,...} -> MoveNotation (every move of the game)
 * {tempo} Matchmaking/{rating-name}         -> MatchMaking  players waiting for a game
 * Matchmaker/{tempo} Matchmaking - matchmaker -> MatchMaking  lock held by the client that pairs players
 * </pre>
 */
public class FireBaseConnection {
    public static final String DEFAULT_KEY_PATH = "src/main/resources/fireStoreConnectionKey.json";
    private static Firestore database;

    /** @return the Firestore connection, or null if it could not be created */
    public static Firestore getDatabase() {
        if(database==null){
            String keyPath = System.getenv().getOrDefault("FIREBASE_KEY_PATH", DEFAULT_KEY_PATH);
            try (FileInputStream serviceAccount = new FileInputStream(keyPath)) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                database = FirestoreClient.getFirestore();
                System.out.println("Connected to Firebase.");
            } catch (Exception e) {
                System.err.println("Could not connect to Firebase using key '" + keyPath + "': " + e.getMessage());
            }
        }
        return database;
    }

    /**
     * Firestore writes are asynchronous. Instead of blocking the Swing thread with get(),
     * fire the write and just print an error if it fails.
     */
    public static void logIfFails(ApiFuture<?> future, String whatWasBeingDone) {
        future.addListener(() -> {
            try {
                future.get();
            } catch (Exception e) {
                System.err.println("Firestore error while " + whatWasBeingDone + ": " + e.getMessage());
            }
        }, Runnable::run);
    }

    private FireBaseConnection() {
    }
}
