package com.tfg.lauragc94.mytraining;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.tfg.lauragc94.mytraining.Entities.User;

import android.os.AsyncTask;
import android.widget.TextView;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

//Activity that manage the login of the user

public class LoginActivity extends AppCompatActivity {

    private String phoneNumberString;
    TextView name;
    TextView phone;
    TextView email;
    private static final long SPLASH_SCREEN_DELAY = 1800;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AccountKit.initialize(getApplicationContext());

        setContentView(R.layout.activity_login);

        AccessToken accessToken = AccountKit.getCurrentAccessToken();

        //If the session is open...
        if (accessToken != null) {

            goToMenuActivity();
        }
    }


    public void onSMSLoginFlow(View view) {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, 101);
    }

  /*  public void onEmailLoginFlow(View view) {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.EMAIL,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, 101);


        //BUTTON EMAIL FOR THE LAYOUT


        <Button
            android:id="@+id/login_EMAIL"
            android:layout_width="300dp"
            android:layout_height="wrap_content"
            android:layout_alignParentBottom="true"
            android:layout_centerHorizontal="true"
            android:layout_marginBottom="107dp"
            android:background="@color/colorWhite"
            android:onClick="onEmailLoginFlow"
            android:text="@string/bottom_email"
            android:textColor="@color/color_ss_dark"
            android:textAlignment="center"/>
    }*/

    private class HttpRequestTask extends AsyncTask<Void, Void, User[]> {

        @Override
        protected void onPreExecute() {
            setContentView(R.layout.activity_login_welcome);
        }


        @Override
        protected User[] doInBackground(Void... params) {
            try {

                final String url = "http://192.168.1.87:8080/myTraining/Account/{phone}";

                // URI (URL) parameters
               HashMap<String,String> uriParams = new HashMap<>();
                uriParams.put("phone", phoneNumberString);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());


                // Make the HTTP GET request, getting an User type and sending the phone to the URI
                User [] user = restTemplate.getForObject(url, User[].class,uriParams);

                return user;



            } catch (Exception e) {
                Log.e("LoginActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute (final User[] user) {

            super.onPostExecute(user);
            setContentView(R.layout.activity_login_welcome);

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    //Start the next Activity
                    if(user.length == 0){
                        goToRegisterActivity();
                    }
                    else
                        goToMenuActivity();
                }
            };

            //Time that the screen will be displayed
            Timer timer = new Timer();
            timer.schedule(task, SPLASH_SCREEN_DELAY);

        }
    }



    public static int APP_REQUEST_CODE = 99;

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;

            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
                showErrorActivity(loginResult.getError());
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
            } else {
                setContentView(R.layout.activity_login_welcome);
                if (loginResult.getAccessToken() != null) {
                    toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
                } else {
                    toastMessage = String.format(
                            "Success:%s...",
                            loginResult.getAuthorizationCode().substring(0, 10));
                }

                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(final Account account) {

                        // Get phone number
                        PhoneNumber phoneNumber = account.getPhoneNumber();
                        if (phoneNumber != null) {
                            phoneNumberString = phoneNumber.toString();
                            new HttpRequestTask().execute();
                        }
                    }
                    @Override
                    public void onError(final AccountKitError error) {
                        // Handle Error
                    }
                });
            }
        }
    }

    private void showErrorActivity(final AccountKitError error) {
        Log.println(Log.ASSERT, "AccountKit", error.toString());
    }

    private void goToMenuActivity(){

        Intent menu = new Intent(this,com.tfg.lauragc94.mytraining.MenuActivity.class);
        startActivity(menu);
        finish();
    }

    private void goToRegisterActivity(){
        Intent register = new Intent(this,com.tfg.lauragc94.mytraining.RegisterActivity.class);
        startActivity(register);
        finish();
    }
}
