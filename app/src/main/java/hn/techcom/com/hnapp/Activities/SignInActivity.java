package hn.techcom.com.hnapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;

import java.util.Objects;

import hn.techcom.com.hnapp.Fragments.HomeFragment;
import hn.techcom.com.hnapp.Interfaces.GetDataService;
import hn.techcom.com.hnapp.Models.Profile;
import hn.techcom.com.hnapp.Models.ValidationResponse;
import hn.techcom.com.hnapp.Network.RetrofitClientInstance;
import hn.techcom.com.hnapp.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String  TAG = "SignInActivity";
    private MaterialCardView googleSignInButton, phoneSignInButton, getCodeButton;
    private MaterialTextView termsButton;
    private LinearLayout loginWithLayout, loginWithPhoneLayout, enterPhoneLayout, enterCodelayout;
    private CountryCodePicker countryCodePicker;
    private TextInputEditText firstOtpBox, secondOtpBox, thirdOtpBox, fourthOtpBox, fifthOtpBox, phoneText;
    private ProgressBar progressBar;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    private Profile newUser;
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onStart() {
        super.onStart();
        if (!checkPermission()) {
            requestPermission();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Hooks
        googleSignInButton   = findViewById(R.id.button_login_with_gmail);
        phoneSignInButton    = findViewById(R.id.button_login_with_phone_final);
        termsButton          = findViewById(R.id.button_terms_click);
        getCodeButton        = findViewById(R.id.button_get_code);
        loginWithLayout      = findViewById(R.id.layout_login_with_signin);
        loginWithPhoneLayout = findViewById(R.id.layout_login_with_phone_signin);
        enterPhoneLayout     = findViewById(R.id.layout_phone_login);
        enterCodelayout      = findViewById(R.id.layout_code_login);
        countryCodePicker    = findViewById(R.id.country_code_picker);
        phoneText            = findViewById(R.id.textInputEditText_phone);
        firstOtpBox          = findViewById(R.id.first_otp_box);
        secondOtpBox         = findViewById(R.id.second_otp_box);
        thirdOtpBox          = findViewById(R.id.third_otp_box);
        fourthOtpBox         = findViewById(R.id.fourth_otp_box);
        fifthOtpBox          = findViewById(R.id.fifth_otp_box);
        progressBar          = findViewById(R.id.signin_progressbar);

        mAuth                = FirebaseAuth.getInstance();

        // Configure Google Sign In - to get a popup to show all the gmail accounts synced to the device
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient  = GoogleSignIn.getClient(this, gso);

        //Click listeners
        googleSignInButton.setOnClickListener(this);
        phoneSignInButton.setOnClickListener(this);
        getCodeButton.setOnClickListener(this);
        termsButton.setOnClickListener(this);


        firstOtpBox.addTextChangedListener(new GenericTextWatcher(firstOtpBox));
        secondOtpBox.addTextChangedListener(new GenericTextWatcher(secondOtpBox));
        thirdOtpBox.addTextChangedListener(new GenericTextWatcher(thirdOtpBox));
        fourthOtpBox.addTextChangedListener(new GenericTextWatcher(fourthOtpBox));
        fifthOtpBox.addTextChangedListener(new GenericTextWatcher(fifthOtpBox));
    }

    @Override
    public void onBackPressed() {
        if(enterPhoneLayout.getVisibility() == View.VISIBLE) {
            loginWithPhoneLayout.setVisibility(View.GONE);
            loginWithLayout.setVisibility(View.VISIBLE);
        }else if(enterCodelayout.getVisibility() == View.VISIBLE){
            enterCodelayout.setVisibility(View.GONE);
            enterPhoneLayout.setVisibility(View.VISIBLE);
        }
        else
            super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button_login_with_phone_final){
            Log.d(TAG,"login with = "+"PHONE");
            loginWithLayout.setVisibility(View.GONE);
            loginWithPhoneLayout.setVisibility(View.VISIBLE);
            enterPhoneLayout.setVisibility(View.VISIBLE);
        }
        if(view.getId() == R.id.button_get_code){
            String phoneWithCode = countryCodePicker.getSelectedCountryCodeWithPlus()+phoneText.getText().toString();
            enterCodelayout.setVisibility(View.VISIBLE);
            enterPhoneLayout.setVisibility(View.GONE);
        }
        if(view.getId() == R.id.button_terms_click) {
            startActivity(new Intent(this, TermsActivity.class));
//            throw new RuntimeException("Test Crash"); // Force a crash
        }
        if(view.getId() == R.id.button_login_with_gmail)
            signIn();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean recordAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted && recordAccepted){
//                        Fragment fragment = new HomeFragment();
//                        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragment)).commit();
                    }
                    else
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                            showMessageOKCancel(
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA, RECORD_AUDIO},
                                                    PERMISSION_REQUEST_CODE);
                                        }
                                    });
                            return;
                        }
                    }
                }else
                    finish();
        }
    }

    private void signIn() {
        progressBar.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            Log.d(TAG, "signInResult:success user =" + "name: " + account.getDisplayName() + " email: " + account.getEmail() + " photo url: " + account.getPhotoUrl());
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d(TAG, "signInResult:failed code=" + e.getStatusCode());

        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            progressBar.setVisibility(View.GONE);

                            emailValidation(Objects.requireNonNull(user).getEmail(), Objects.requireNonNull(user).getDisplayName());

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            Toast.makeText(SignInActivity.this,"Sorry login failed! Please try again.", Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }

    public void emailValidation(String email, String name) {
        String baseUrl = "http://167.99.13.238:8000/api/users/emailvalidate/" + email + "/";
        Log.d(TAG, "base url = " + baseUrl);
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<ValidationResponse> call = service.validateEmail(baseUrl);
        call.enqueue(new Callback<ValidationResponse>() {
            @Override
            public void onResponse(Call<ValidationResponse> call, Response<ValidationResponse> response) {
                if (response.body() != null) {
                    if (response.body().getProfile() != null) {
                        Profile profile = response.body().getProfile();
                        Log.d(TAG,"validated profile hnid = " + profile.getHnid());
                        newUser = profile;
                        updateUi("old");
                    }else {
                        newUser = new Profile();
                        newUser.setEmail(email);
                        newUser.setFullName(name);
                        Log.d(TAG, "validated profile hnid = " + null);
                        updateUi("new");
                    }
                }
            }

            @Override
            public void onFailure(Call<ValidationResponse> call, Throwable t) {
                Log.d(TAG, "Email validation failed!");
            }
        });
    }

    private void updateUi(String userType){
        if (userType.equals("old")) {
            storeOldUserToSharedPref();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else{
            storeNewUserToSharedPref();
            startActivity(new Intent(this,OnboardingActivity.class));
            finish();
        }
    }

    private void storeOldUserToSharedPref() {
        SharedPreferences.Editor editor = this.getSharedPreferences("MY_PREF", Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(newUser);
        editor.putString("NewUser",json);
        editor.apply();
    }

    private void storeNewUserToSharedPref() {
        SharedPreferences.Editor editor = this.getSharedPreferences("MY_PREF", Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(newUser);
        editor.putString("NewUser",json);
        editor.apply();
    }

    public class GenericTextWatcher implements TextWatcher {
        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // TODO Auto-generated method stub
            String text = editable.toString();

            if (view.getId() == R.id.first_otp_box) {
                if (text.length() == 1)
                    secondOtpBox.requestFocus();
            } else if (view.getId() == R.id.second_otp_box) {
                if (text.length() == 1)
                    thirdOtpBox.requestFocus();
                else
                    firstOtpBox.requestFocus();
            } else if (view.getId() == R.id.third_otp_box) {
                if (text.length() == 1)
                    fourthOtpBox.requestFocus();
                else
                    secondOtpBox.requestFocus();
            } else if (view.getId() == R.id.fourth_otp_box) {
                if (text.length() == 1)
                    fifthOtpBox.requestFocus();
                else
                    thirdOtpBox.requestFocus();
            } else if (text.length() == 0)
                fourthOtpBox.requestFocus();
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }
    }

    //Custom methods
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(this, CAMERA);
        int result2 = ContextCompat.checkSelfPermission(this, RECORD_AUDIO);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, CAMERA, RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
    }

    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage("You need to allow access to all of these permission in order to continue with this app.")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        onDestroy();
                    }
                })
                .create()
                .show();
    }
}