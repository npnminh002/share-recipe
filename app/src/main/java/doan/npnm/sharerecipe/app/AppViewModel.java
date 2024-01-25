package doan.npnm.sharerecipe.app;

import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;

import doan.npnm.sharerecipe.app.context.AppContext;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.utility.Constant;

public class AppViewModel extends ViewModel {
    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    public FirebaseStorage storage = FirebaseStorage.getInstance();
    public StorageReference storageReference = storage.getReference();

    public MutableLiveData<Users> users = new MutableLiveData<>();

    public AppViewModel() {
        if (auth.getCurrentUser()!=null) {
            getDataFromUser(auth.getCurrentUser().getUid());
        }
    }

    public void getDataFromUser(String uid) {
        firestore.collection(Constant.KEY_USER)
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Users us = documentSnapshot.toObject(Users.class);
                    users.postValue(us);
                })
                .addOnFailureListener(e -> {
                    showToast("Error: " + e.getMessage());
                });
    }

    public void signUpApp(String email, String name, String pass, MutableLiveData<Boolean> isEmailExists) {
        firestore.collection(Constant.KEY_USER)
                .whereEqualTo("Email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        createUserInFirestore(email, name, pass);
                        isEmailExists.postValue(false);
                    } else {
                        isEmailExists.postValue(true);
                    }
                })
                .addOnFailureListener(e -> {
                    showToast("Error checking email existence: " + e.getMessage());
                    isEmailExists.postValue(false);
                });
    }

    public MutableLiveData<Users> getUsers() {
        return users;
    }

    private void createUserInFirestore(String email, String name, String pass) {
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {
                    authResult.getUser().getIdToken(true)
                            .addOnSuccessListener(getTokenResult -> {
                                String idToken = getTokenResult.getToken();
                                Users user = new Users(authResult.getUser().getUid(), name,
                                        email, pass, idToken, "", new Date().toString(),0);
                                firestore.collection(Constant.KEY_USER)
                                        .document(user.UserID)
                                        .set(user)
                                        .addOnSuccessListener(unused -> {
                                            showToast("Sign Up Success");
                                            users.setValue(user);
                                        })
                                        .addOnFailureListener(error -> {
                                            showToast(error.getMessage());
                                        });
                            })
                            .addOnFailureListener(error -> {
                                showToast("Failed to retrieve ID token: " + error.getMessage());
                            });
                })
                .addOnFailureListener(error -> {
                    showToast(error.getMessage());
                });
    }


    public void showToast(String mess) {
        Toast.makeText(AppContext.getContext(), mess, Toast.LENGTH_LONG).show();
    }

}
