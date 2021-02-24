package hn.techcom.com.hnapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

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

    private MaterialTextView createAccountButton;
    private MaterialTextView termsButton;

    public LoginWithFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(getContext()), gso);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_with, container, false);

        //Hooks
        MaterialCardView loginWithGmailButton = view.findViewById(R.id.button_login_with_gmail);
        MaterialCardView loginWithPhone = view.findViewById(R.id.button_login_with_phone);
        termsButton = view.findViewById(R.id.button_terms_click);
        createAccountButton = view.findViewById(R.id.create_account_button);


        //Click listeners
        loginWithGmailButton.setOnClickListener(this);
        loginWithPhone.setOnClickListener(this);
        termsButton.setOnClickListener(this);
        createAccountButton.setOnClickListener(this);

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
        else if (view.getId() == R.id.create_account_button) {
            createAccountButton.setTextColor(getResources().getColor(R.color.colorCenterLinearGradient));
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.framelayout_login, new UserOnboardingFragment(), "UserOnboardingFragment")
                    .addToBackStack(null)
                    .commit();
        } else if (view.getId() == R.id.button_terms_click) {
            termsButton.setTextColor(getResources().getColor(R.color.colorCenterLinearGradient));
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.framelayout_login, new TermsAndConditionsFragment(), "TermsAndConditionsFragment")
                    .addToBackStack(null)
                    .commit();
//            termsButton.setTextColor(getResources().getColor(R.color.colorSecondary));
        }
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            Log.d(TAG, "signInResult:success user =" + "name: " + account.getDisplayName() + " email: " + account.getEmail() + " photo url: " + account.getPhotoUrl());

            emailValidation(account.getEmail());

            // Signed in successfully, show authenticated UI.
//            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d(TAG, "signInResult:failed code=" + e.getStatusCode());
//            updateUI(null);
        }
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
        String baseUrl = "http://hn.techcomengine.com/api/users/email/validate/" + email + "/";
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