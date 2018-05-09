package com.tfg.lauragc94.mytraining;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.accountkit.AccountKit;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.tfg.lauragc94.mytraining.CountryCodesAdapter.CountryCode;
import com.tfg.lauragc94.mytraining.Entities.User;

import org.springframework.web.client.RestTemplate;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

//Register the new user

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilName;
    private TextInputLayout tilPhone;
    private TextInputLayout tilEmail;
    private EditText fieldName;
    private EditText fieldPhone;
    private EditText fieldEmail;
    private Spinner mCountryCode;
    private String name;
    private String tel;
    private String phone;
    private String email;
    private String countryCodeNumber;


    //Country-Code Phone number
    @SuppressWarnings("unchecked")
    private Set<String> getSupportedRegions(PhoneNumberUtil util) {
        try {
            return (Set<String>) util.getClass()
                    .getMethod("getSupportedRegions")
                    .invoke(util);
        }
        catch (NoSuchMethodException e) {
            try {
                return (Set<String>) util.getClass()
                        .getMethod("getSupportedCountries")
                        .invoke(util);
            }
            catch (Exception helpme) {
                // ignored
            }
        }
        catch (Exception e) {
            // ignored
        }
        return new HashSet<>();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        // Spinner references
        mCountryCode = (Spinner) findViewById(R.id.spinner_countryCode);

        // TILs references
        tilName = findViewById(R.id.til_name);
        tilPhone = findViewById(R.id.til_phone);
        tilEmail = findViewById(R.id.til_email);

        // Buttons references
        Button accept_button = findViewById(R.id.button_accept);
        accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
        Button cancel_button = findViewById(R.id.button_cancelar);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginActivity();
            }
        });
        FloatingActionButton reset_button = findViewById(R.id.button_reset);
        reset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
            }
        });

        // ETs references
        fieldName = (EditText) findViewById(R.id.field_name);
        fieldPhone = (EditText) findViewById(R.id.field_phone);
        fieldEmail = (EditText) findViewById(R.id.field_email);

        // **************************************************************//
        // ************************ COUNTRY CODE ************************//
        // **************************************************************//

        final CountryCodesAdapter ccList = new CountryCodesAdapter(this,
                android.R.layout.simple_list_item_1,
                android.R.layout.simple_spinner_dropdown_item);
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();
        Set<String> ccSet = getSupportedRegions(util);
        for (String cc : ccSet)
            ccList.add(cc);


        mCountryCode.setAdapter(ccList);
        mCountryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ccList.setSelected(position);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Phonenumber.PhoneNumber myNum = getMyNumber(this);
        if (myNum != null) {
            CountryCode cc = new CountryCode();
            cc.regionCode = util.getRegionCodeForNumber(myNum);
            if (cc.regionCode == null)
                cc.regionCode = util.getRegionCodeForCountryCode(myNum.getCountryCode());
            mCountryCode.setSelection(ccList.getPositionForId(cc));
            fieldPhone.setText(String.valueOf(myNum.getNationalNumber()));
        }
        else {
            final TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            final String regionCode = tm.getSimCountryIso().toUpperCase(Locale.US);
            CountryCode cc = new CountryCode();
            cc.regionCode = regionCode;
            cc.countryCode = util.getCountryCodeForRegion(regionCode);
            countryCodeNumber = String.valueOf(cc.countryCode);
            mCountryCode.setSelection(ccList.getPositionForId(cc));
        }

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

        //Checking the field while the user is writing his/her phone
        fieldPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isCorrectPhone(String.valueOf(s));
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

    // Returns the (parsed) number stored in this device SIM card.
    @SuppressLint({"HardwareIds", "MissingPermission"})
    public Phonenumber.PhoneNumber getMyNumber(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String regionCode = tm.getSimCountryIso().toUpperCase(Locale.US);
            return PhoneNumberUtil.getInstance().parse(tm.getLine1Number(), regionCode);
        }
        catch (Exception e) {
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

    private boolean isCorrectPhone(String phone) {
        if (phone.length() != 9 || !Patterns.PHONE.matcher(phone).matches()){
            tilPhone.setError("Teléfono inválido");
            return false;
        } else {
            tilPhone.setError(null);
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

    // **************************************************************************//
    // ************************ HTTP REQUEST IN BACKGROUND **********************//
    // **************************************************************************//

    private class HttpRequestTask extends AsyncTask<Void, Void, Void> {
        private Void[] params;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                User user = new User();
                user.setName(name);
                user.setEmail(email);
                user.setPhone(phone);

                final String url = "http://192.168.1.87:8080/myTraining/Register//";

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate();

                // Make the HTTP POST request, marshaling the request to JSON, and the response to a String
                restTemplate.postForObject(url, user, String.class);

            } catch (Exception e) {
                Log.e("RegisterActivity", e.getMessage(), e);
            }

            return null;
        }

    }

    // ***************************************************************//
    // ************************ BUTTONS PROCESS **********************//
    // ***************************************************************//

    private void validateData() {
        name = tilName.getEditText().getText().toString();
        email = tilEmail.getEditText().getText().toString();
        tel = tilPhone.getEditText().getText().toString();

        boolean a = isCorrectName(name);
        boolean b = isCorrectPhone(tel);
        boolean c = isCorrectEmail(email);

        if (a && b && c) {

            StringBuffer sb = new StringBuffer();
            sb.append("+");
            sb.append(countryCodeNumber);
            sb.append(tilPhone.getEditText().getText().toString());
            phone = sb.toString();


            new HttpRequestTask().execute();
            // OK, go to the next activity
            Toast.makeText(this, "Se guarda el registro", Toast.LENGTH_LONG).show();

            goToMenuActivity();
            //Close the activity
            finish();
        }

    }



    //Botón RESET
    private void deleteData() {
        fieldName.setText(null);
        tilName.setError(null);
        fieldPhone.setText(null);
        tilPhone.setError(null);
        fieldEmail.setText(null);
        tilEmail.setError(null);


        Toast.makeText(this, "Campos borrados", Toast.LENGTH_LONG).show();

    }

    private void goToMenuActivity(){

        Intent menu = new Intent(this,com.tfg.lauragc94.mytraining.MenuActivity.class);
        startActivity(menu);
        finish();
    }

    private void goToLoginActivity(){

        AccountKit.logOut();
        Intent loginIntent = new Intent().setClass(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }


}