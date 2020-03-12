package Server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import com.google.gson.JsonObject;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import okhttp3.internal.http.HttpHeaders;


public class FCM_Push {

	private static final String Key_PATH = "D:\\AndroidStudio-Study-Github\\Together\\Together_Test\\FCM_Key\\together-c49b9-firebase-adminsdk-pomji-9936711e8c.json";
	
	private static final String PROJECT_ID = "together-bf66c";

	private static final String BASE_URL = "https://fcm.googleapis.com";
	private static final String FCM_SEND_ENDPOINT = "/v1/projects/" + PROJECT_ID + "/messages:send";
	private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
	private static final String FIREBASE_SCOPE = "https://www.googleapis.com/auth/firebase";
	private static final String CLOUD_SCOPE = "https://www.googleapis.com/auth/cloud-platform";
	private static final String READONLY_SCOPE = "https://www.googleapis.com/auth/firebase.readonly";
	private static final String[] SCOPES = { MESSAGING_SCOPE,FIREBASE_SCOPE,CLOUD_SCOPE,READONLY_SCOPE, };

	private static final String TITLE = "FCM Notification";
	private static final String BODY = "Notification from FCM";
	public static final String MESSAGE_KEY = "message";
	
	//===============================Ǫ�þ˶�������
	
	public void LikeSend(String UserToken) throws IOException, FirebaseMessagingException {
		FileInputStream serviceAccount =
				  new FileInputStream(Key_PATH);

				FirebaseOptions options = new FirebaseOptions.Builder()
				  .setCredentials(GoogleCredentials.fromStream(serviceAccount))
				  .setDatabaseUrl("https://together-c49b9.firebaseio.com")
				  .build();

				FirebaseApp.initializeApp(options);
				
				
				// This registration token comes from the client FCM SDKs.
				String registrationToken = UserToken;

				// See documentation on defining a message payload.
				Message message = Message.builder()
				    .putData("title", "���ɿ�û")
				    .putData("body", "���ο� ���� ��û�� �Ծ��")
				    .setToken(registrationToken)
				    .build();
				
				System.out.println(message);

				// Send a message to the device corresponding to the provided
				// registration token.
				String response = FirebaseMessaging.getInstance().send(message);
				// Response is a message ID string.
				System.out.println("Successfully sent message: " + response);
	}
	
	public void Send(String UserToken) throws IOException, FirebaseMessagingException {
		FileInputStream serviceAccount =
				  new FileInputStream(Key_PATH);

				FirebaseOptions options = new FirebaseOptions.Builder()
				  .setCredentials(GoogleCredentials.fromStream(serviceAccount))
				  .setDatabaseUrl("https://together-c49b9.firebaseio.com")
				  .build();

				FirebaseApp.initializeApp(options);
				
				
				// This registration token comes from the client FCM SDKs.
				String registrationToken = UserToken;

				// See documentation on defining a message payload.
				Message message = Message.builder()
				    .putData("title", "���οϷ�")
				    .putData("body", "�������� �Բ� ���������?")
				    .setToken(registrationToken)
				    .build();
				
				System.out.println(message);

				// Send a message to the device corresponding to the provided
				// registration token.
				String response = FirebaseMessaging.getInstance().send(message);
				// Response is a message ID string.
				System.out.println("Successfully sent message: " + response);
	}
}
