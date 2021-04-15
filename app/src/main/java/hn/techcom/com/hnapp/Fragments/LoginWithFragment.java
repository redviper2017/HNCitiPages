package hn.techcom.com.hnapp.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import hn.techcom.com.hnapp.Activities.MainActivity;
import hn.techcom.com.hnapp.Interfaces.GetDataService;
import hn.techcom.com.hnapp.Models.Validate;
import hn.techcom.com.hnapp.Network.RetrofitClientInstance;
import hn.techcom.com.hnapp.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginWithFragment extends Fragment implements View.OnClickListener {

    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient googleSignInClient;
    private static final String TAG = "LoginWithFragment";

//    private MaterialTextView createAccountButton;
    private MaterialTextView termsButton;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    public LoginWithFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        //if user is logged in already
        if(user != null){
            startActivity(new Intent(getContext(),MainActivity.class));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        createRequest();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_with, container, false);

        //Hooks
        MaterialCardView loginWithGmailButton = view.findViewById(R.id.button_login_with_gmail);
        MaterialCardView loginWithPhone = view.findViewById(R.id.button_login_with_phone);
        termsButton = view.findViewById(R.id.button_terms_click);
//        createAccountButton = view.findViewById(R.id.create_account_button);


        //Click listeners
        loginWithGmailButton.setOnClickListener(this);
        loginWithPhone.setOnClickListener(this);
        termsButton.setOnClickListener(this);
//        createAccountButton.setOnClickListener(this);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "request code = " + resultCode);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_login_with_gmail)
            signIn();
        else if (view.getId() == R.id.button_login_with_phone)
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.framelayout_login, new LoginWithPhoneFragment(), "LoginWithPhoneFragment")
                    .addToBackStack(null)
                    .commit();
//        else if (view.getId() == R.id.create_account_button) {
//            createAccountButton.setTextColor(getResources().getColor(R.color.colorCenterLinearGradient));
//            getActivity().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.framelayout_login, new UserOnboardingFragment(), "UserOnboardingFragment")
//                    .addToBackStack(null)
//                    .commit();
//        }
        else if (view.getId() == R.id.button_terms_click) {
            termsButton.setTextColor(getResources().getColor(R.color.colorCenterLinearGradient));
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.framelayout_login, new TermsAndConditionsFragment(), "TermsAndConditionsFragment")
                    .addToBackStack(null)
                    .commit();
//            termsButton.setTextColor(getResources().getColor(R.color.colorSecondary));
        }
    }

    private void createRequest() {
        // Configure Google Sign In - to get a popup to show all the gmail accounts synced to the device
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(getContext()), gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            Log.d(TAG, "signInResult:success user =" + "name: " + account.getDisplayName() + " email: " + account.getEmail() + " photo url: " + account.getPhotoUrl());
            firebaseAuthWithGoogle(account.getIdToken());


            // Signed in successfully, show authenticated UI.
//            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d(TAG, "signInResult:failed code=" + e.getStatusCode());
//            updateUI(null);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            emailValidation(user.getEmail());

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            Toast.makeText(getContext(),"Sorry login failed! Please try again.", Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }

    private void updateUi(String userType) {
        switch (userType) {
            case "new":
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout_login, new UserOnboardingFragment(), "UserOnboardingFragment")
                        .addToBackStack(null)
                        .commit();
                break;
            case "old":
                startActivity(new Intent(getActivity(), MainActivity.class));
                break;
        }
    }

    private boolean checkIfNewUser(String username) {
        //get user data from server if exists
        return false;
    }

    public void emailValidation(String email) {
        String baseUrl = "http://167.99.13.238:8000/api/users/emailvalidate/" + email + "/";
        Log.d(TAG, "base url = " + baseUrl);
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Validate> call = service.validateEmail(baseUrl);
        call.enqueue(new Callback<Validate>() {
            @Override
            public void onResponse(Call<Validate> call, Response<Validate> response) {
                if (response.body() != null) {
                    boolean doesEmailExists = response.body().getExisting();
                    if (doesEmailExists) {
                        Log.d(TAG, "this user is a returning user");
                        updateUi("old");
                    } else {
                        Log.d(TAG, "this user is a new user");
                        updateUi("new");
                    }
                }
            }

            @Override
            public void onFailure(Call<Validate> call, Throwable t) {
                Log.d(TAG, "Email validation failed!");
            }
        });
    }
}