package com.tfg.lauragc94.mytraining;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.PhoneNumber;
import com.tfg.lauragc94.mytraining.Entities.User;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;
import android.app.AlertDialog;
import android.content.DialogInterface;



//Activity that shows the datas of the account and allows the user to change them

public class ConfigurationActivity extends AppCompatActivity implements View.OnClickListener {


    private String phoneNumberString;
    private TextInputLayout tilName;
    private TextInputLayout tilPhone;
    private TextInputLayout tilEmail;
    private EditText fieldName;
    private EditText fieldPhone;
    private EditText fieldEmail;
    private FloatingActionButton edit_button;
    private FloatingActionButton check_button;
    private String name;
    private String phone;
    private String email;
    private TextView delete_account;
    private boolean a;
    private boolean c;
    private static final long SPLASH_SCREEN_DELAY = 1800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        // Floating action button
        edit_button = findViewById(R.id.edit_button);
        check_button = findViewById(R.id.check_button);

        edit_button.setOnClickListener(this);
        check_button.setOnClickListener(this);

        check_button.setVisibility(View.INVISIBLE);
        check_button.setEnabled(false);


        //Delete account textView
        delete_account =findViewById(R.id.delete_account);

        // TILs references
        tilName = findViewById(R.id.til_name);
        tilPhone = findViewById(R.id.til_phone);
        tilEmail = findViewById(R.id.til_email);


        // ETs references
        fieldName = (EditText) findViewById(R.id.field_name);
        fieldPhone = (EditText) findViewById(R.id.field_phone);
        fieldEmail = (EditText) findViewById(R.id.field_email);

        //Get phone number:
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(final Account account) {

                // Get phone number
                PhoneNumber phoneNumber = account.getPhoneNumber();
                if (phoneNumber != null) {
                    phoneNumberString = phoneNumber.toString();
                }
            }

            @Override
            public void onError(final AccountKitError error) {
                // Handle Error
            }
        });

        fieldName.setEnabled(false);
        fieldEmail.setEnabled(false);
        fieldPhone.setEnabled(false);

        //Get the name,email and phone of the user in background thread
        new getUserHttpRequestTask().execute();



        // *****************************************************************//
        // ************************ INPUT TEXT LAYOUT **********************//
        // *****************************************************************//

        //Checking the field while the user is writing his/her name
        fieldName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilName.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Checking the field while the user is writing his/her email
        fieldEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isCorrectEmail(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onClick(View view){

        switch (view.getId()) {

            case R.id.edit_button:
                // code for button when user clicks edit_button:

                edit_button.setVisibility(View.INVISIBLE);
                edit_button.setEnabled(false);
                check_button.setVisibility(View.VISIBLE);
                check_button.setEnabled(true);

                fieldName.setText("");
                fieldEmail.setText("");
                fieldName.setEnabled(true);
                fieldEmail.setEnabled(true);

                break;

            case R.id.check_button:
                // code for button when user clicks edit_button:


                validateData();


                check_button.setVisibility(View.INVISIBLE);
                check_button.setEnabled(false);
                edit_button.setVisibility(View.VISIBLE);
                edit_button.setEnabled(true);

                break;

            case R.id.delete_account:
                // code for button when user clicks deletene_account:
                AlertDialog.Builder delete_alert = new AlertDialog.Builder(this, R.style.DialogStyle);
                delete_alert.setTitle("¿Eliminar cuenta?");
                delete_alert.setMessage("Está a punto de eliminar su cuenta de MyTraining, ¿desea continuar?");
                delete_alert.setCancelable(false);
                delete_alert.setPositiveButton("SÍ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface delete_alert, int id) {
                        deleteData();

                    }
                });
                delete_alert.setNegativeButton("NO", null);

                delete_alert.show();
        }



    }



    // ****************************************************************//
    // ************************ GETTING THE USER **********************//
    // ****************************************************************//

    private class getUserHttpRequestTask extends AsyncTask<Void, Void, User[]> {

        @Override
        protected User[] doInBackground(Void... params) {
            try {

                final String url = "http://192.168.1.87:8080/myTraining/Account/{phone}";

                // URI (URL) parameters
                HashMap<String, String> uriParams = new HashMap<>();
                uriParams.put("phone", phoneNumberString);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());


                // Make the HTTP GET request, getting an User type and sending the phone to the URI
                User[] user = restTemplate.getForObject(url, User[].class, uriParams);

                return user;


            } catch (Exception e) {
                Log.e("ConfigurationActivity", e.getMessage(), e);
            }

            return null;
        }

        protected void onPostExecute(User[] user) {
            super.onPostExecute(user);
            ;

            fieldName.setText(user[0].getName());
            fieldPhone.setText(user[0].getPhone());
            fieldEmail.setText(user[0].getEmail());

        }

    }

    // ****************************************************************//
    // ************************ UPDATING THE USER **********************//
    // ****************************************************************//

    private class updateUserHttpRequestTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                User user = new User();
                user.setName(name);
                user.setEmail(email);


                final String url = "http://192.168.1.87:8080/myTraining/Account/{phone}";

                // URI (URL) parameters
                HashMap<String, String> uriParams = new HashMap<>();
                uriParams.put("phone", phoneNumberString);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate();

                // Make the HTTP POST request, marshaling the request to JSON, and the response to a String
                restTemplate.postForObject(url,user,String.class, uriParams);

            } catch (Exception e) {
                Log.e("ConfigurationActivity", e.getMessage(), e);
            }

            return null;
        }

    }


    // ****************************************************************//
    // ************************ DELETING THE USER **********************//
    // ****************************************************************//

    private class deleteUserHttpRequestTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                User user = new User();
                user.setName(name);
                user.setEmail(email);


                final String url = "http://192.168.1.87:8080/myTraining/Account/{phone}";

                // URI (URL) parameters
                HashMap<String, String> uriParams = new HashMap<>();
                uriParams.put("phone", phoneNumberString);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate();

                // Make the HTTP POST request, marshaling the request to JSON, and the response to a String
                restTemplate.delete(url,uriParams);

            } catch (Exception e) {
                Log.e("ConfigurationActivity", e.getMessage(), e);
            }

            return null;
        }

    }


    // ***************************************************************//
    // ************************ DATA VALIDATION **********************//
    // ***************************************************************//

    private boolean isCorrectName(String name) {
        Pattern patron = Pattern.compile("^([A-Za-zÁÉÍÓÚñáéíóúÑ]{0}?[A-Za-zÁÉÍÓÚñáéíóúÑ\\']+[\\s])+([A-Za-zÁÉÍÓÚñáéíóúÑ]{0}?[A-Za-zÁÉÍÓÚñáéíóúÑ\\'])+[\\s]?([A-Za-zÁÉÍÓÚñáéíóúÑ]{0}?[A-Za-zÁÉÍÓÚñáéíóúÑ\\'])?$");
        if (!patron.matcher(name).matches() || name.length() > 30) {
            tilName.setError("Nombre o apellidos inválido");
            return false;
        } else {
            tilName.setError(null);
        }

        return true;
    }

    private boolean isCorrectEmail(String email) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Correo electrónico inválido");
            return false;
        } else {
            tilEmail.setError(null);
        }

        return true;
    }


    private void validateData() {
        name = tilName.getEditText().getText().toString();
        email = tilEmail.getEditText().getText().toString();

        boolean a = isCorrectName(name);

        boolean c = isCorrectEmail(email);

        if (a && c) {

            new updateUserHttpRequestTask().execute();
            // OK, se pasa a la siguiente acción
            Toast.makeText(this, "Se guarda el registro", Toast.LENGTH_LONG).show();

        }

        else{

            Toast.makeText(this, "Datos no válidos", Toast.LENGTH_LONG).show();


        }

    }

    private void deleteData(){

        setContentView(R.layout.activity_configuration_goodbye);

        new deleteUserHttpRequestTask().execute();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //Start the next Activity
                AccountKit.logOut();
                Intent loginIntent = new Intent().setClass(ConfigurationActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        };

        //Time that the screen will be displayed
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);



    }

}
