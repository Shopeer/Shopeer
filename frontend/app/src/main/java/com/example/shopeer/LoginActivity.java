package com.example.shopeer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private Button registerButton;
    private int RC_SIGN_IN=1;
    final static String TAG = "SignIn Activity";
    private GoogleSignInClient mGoogleSignInClient;

    final private String regisUrl = "http://20.230.148.126:8080/user/registration";
    final private String loginUrl = "http://20.230.148.126:8080/user/profile";

    private boolean register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        setButtons();
    }

    public void setButtons() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
                register = false;
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
                register = true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        mGoogleSignInClient.signOut();
    }

    @Override
    protected void onStart() {
        super.onStart();


        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            updateUI(account);
        }
    }


    private void updateUI(GoogleSignInAccount account) {
        if (account == null) {
            Log.d(TAG, "There is no user logged in!");
        }
        else {
            // TODO:get user info and call backend to register or login
            if (register) {
                registerUser(account);
            }
            else {
                loginUser(account);
            }
        }
    }

    private void registerUser(GoogleSignInAccount account) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String url = regisUrl + "?email=" + account.getEmail() +"&" + "name=" + account.getDisplayName();
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", account.getDisplayName());
            jsonBody.put("email", account.getEmail());
            try{
                Log.d(TAG, account.getPhotoUrl().toString());
                URL imgUrl = new URL(account.getPhotoUrl().toString());
                Bitmap image = BitmapFactory.decodeStream(imgUrl.openConnection().getInputStream());
                String encodedString = ProfileFragment.newInstance().encodeImage(image);
                jsonBody.put("photo", encodedString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "onResponse register: " + response);
                    // redirect to MainActivity
                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    mainIntent.putExtra("name", account.getDisplayName());
                    mainIntent.putExtra("email", account.getEmail());
                    //mainIntent.putExtra("pic_uri", account.getPhotoUrl().toString());
                    mainIntent.putExtra("page", MainActivity.PROFILE_ID);
                    startActivity(mainIntent);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse register: " + error.toString());
                    signOut();

                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };
            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loginUser(GoogleSignInAccount account) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String url = loginUrl + "?email=" + account.getEmail();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "onResponse login: " + response);
                    if ("".equals(response)) {
                        Log.d(TAG, "No user registered");
                        Toast.makeText(LoginActivity.this, "No user found, please Register", Toast.LENGTH_LONG).show();
                    } else {
                        // redirect to MainActivity
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainIntent.putExtra("name", account.getDisplayName());
                        mainIntent.putExtra("email", account.getEmail());
                        //mainIntent.putExtra("pic_uri", account.getPhotoUrl().toString());
                        //Log.d(TAG, "onResponse: " + account.getPhotoUrl().toString());
                        startActivity(mainIntent);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse login: " + error.toString());
                    signOut();
                }
            });
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}