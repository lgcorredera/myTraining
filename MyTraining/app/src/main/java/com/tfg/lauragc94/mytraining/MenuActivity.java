package com.tfg.lauragc94.mytraining;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.app.AlertDialog;
import android.widget.Button;
import android.widget.EditText;
import com.facebook.accountkit.AccountKit;
import com.tfg.lauragc94.mytraining.Entities.Exercise;
import com.tfg.lauragc94.mytraining.FreeTraining.FreeTrainingActivity;
import com.tfg.lauragc94.mytraining.FreeTraining.Exercises_RecyclerView_Adapter;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;


public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String phoneNumberString;
    private EditText name;
    private EditText phone;
    private EditText email;
    private boolean a;
    private boolean b;
    public static Exercises_RecyclerView_Adapter adapter1;
    public static Exercises_RecyclerView_Adapter adapter2;
    public static Exercises_RecyclerView_Adapter adapter3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        new getExercisesForObjectiveOneHttpRequestTask().execute();
        new getExercisesForObjectiveTwoHttpRequestTask().execute();
        new getExercisesForObjectiveThreeHttpRequestTask().execute();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Buttons of the content menu
        Button custom_training_button = findViewById(R.id.custom_training_button);
        custom_training_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuIntent1 = new Intent().setClass(MenuActivity.this, FreeTrainingActivity.class);
                startActivity(menuIntent1);
            }
        });
        Button free_training_button = findViewById(R.id.free_training_button);
        free_training_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuIntent2 = new Intent().setClass(MenuActivity.this, FreeTrainingActivity.class);
                startActivity(menuIntent2);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {

            // setup the alert builder
            AlertDialog.Builder info = new AlertDialog.Builder(this, R.style.DialogStyle);
            info.setTitle("MyTraining");
            info.setMessage("My training es una app diseñada exclusivamente para ti.");

            // add a button
            info.setPositiveButton("OK", null);

            // create and show the alert dialog
            AlertDialog dialog = info.create();
            dialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_statistics) {
            // Handle the camera action
        } else if (id == R.id.nav_calendar) {

        } else if (id == R.id.nav_configuration) {
            Intent configurationIntent = new Intent().setClass(MenuActivity.this, ConfigurationActivity.class);
            startActivity(configurationIntent);

        } else if (id == R.id.nav_log_out) {
            AccountKit.logOut();
            Intent loginIntent = new Intent().setClass(MenuActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /***************************************************************************/
    /*************** GET EXERCISES WHERE OBJECTIVE = MUSCULACIÓN ***************/
    /***************************************************************************/

    private class getExercisesForObjectiveOneHttpRequestTask extends AsyncTask<Void, Void, ArrayList<Exercise>> {


        @Override
        protected ArrayList<Exercise> doInBackground(Void... params) {
            try {

                final String url = "http://192.168.1.87:8080/myTraining/Exercises/Objective/{objective}";
                String objective= "Musculación";



                // URI (URL) parameters
                HashMap<String, String> uriParams = new HashMap<>();
                uriParams.put("objective", objective);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());


                // Make the HTTP GET request, getting an User type and sending the phone to the URI
                Exercise[] exercises = restTemplate.getForObject(url, Exercise[].class, uriParams);

                //getItems(exercise);


                ArrayList<Exercise> list_exercises = new ArrayList<>();
                 for(int k=0; k<exercises.length; k++){

                     list_exercises.add(new Exercise(exercises[k].getID_exercise(),exercises[k].getName(),exercises[k].getDescription(),exercises[k].getObjective(), exercises[k].getLevel(),exercises[k].getSeries(), exercises[k].getRepetitions()));

                 }


                return list_exercises;


            } catch (Exception e) {
                Log.e("FreeTrainingActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Exercise> list) {
            super.onPostExecute(list);
            adapter1 = new Exercises_RecyclerView_Adapter(MenuActivity.this, list);

        }
    }

    /*******************************************************************************/
    /*************** GET EXERCISES WHERE OBJECTIVE = PÉRDIDA DE PESO ***************/
    /*******************************************************************************/

    private class getExercisesForObjectiveTwoHttpRequestTask extends AsyncTask<Void, Void, ArrayList<Exercise>> {


        @Override
        protected ArrayList<Exercise> doInBackground(Void... params) {
            try {

                final String url = "http://192.168.1.87:8080/myTraining/Exercises/Objective/{objective}";
                String objective= "Pérdida de peso";



                // URI (URL) parameters
                HashMap<String, String> uriParams = new HashMap<>();
                uriParams.put("objective", objective);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());


                // Make the HTTP GET request, getting an User type and sending the phone to the URI
                Exercise[] exercises = restTemplate.getForObject(url, Exercise[].class, uriParams);

                //getItems(exercise);


                ArrayList<Exercise> list_exercises = new ArrayList<>();
                for(int k=0; k<exercises.length; k++){

                    list_exercises.add(new Exercise(exercises[k].getID_exercise(),exercises[k].getName(),exercises[k].getDescription(),exercises[k].getObjective(), exercises[k].getLevel(),exercises[k].getSeries(), exercises[k].getRepetitions()));

                }


                return list_exercises;


            } catch (Exception e) {
                Log.e("FreeTrainingActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Exercise> list) {
            super.onPostExecute(list);
            adapter2 = new Exercises_RecyclerView_Adapter(MenuActivity.this, list);

        }
    }

    /*****************************************************************************/
    /*************** GET EXERCISES WHERE OBJECTIVE = MANTENIMIENTO ***************/
    /*****************************************************************************/

    private class getExercisesForObjectiveThreeHttpRequestTask extends AsyncTask<Void, Void, ArrayList<Exercise>> {


        @Override
        protected ArrayList<Exercise> doInBackground(Void... params) {
            try {

                final String url = "http://192.168.1.87:8080/myTraining/Exercises/Objective/{objective}";
                String objective= "Mantenimiento";



                // URI (URL) parameters
                HashMap<String, String> uriParams = new HashMap<>();
                uriParams.put("objective", objective);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());


                // Make the HTTP GET request, getting an User type and sending the phone to the URI
                Exercise[] exercises = restTemplate.getForObject(url, Exercise[].class, uriParams);

                //getItems(exercise);


                ArrayList<Exercise> list_exercises = new ArrayList<>();
                for(int k=0; k<exercises.length; k++){

                    list_exercises.add(new Exercise(exercises[k].getID_exercise(),exercises[k].getName(),exercises[k].getDescription(),exercises[k].getObjective(), exercises[k].getLevel(),exercises[k].getSeries(), exercises[k].getRepetitions()));

                }


                return list_exercises;


            } catch (Exception e) {
                Log.e("FreeTrainingActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Exercise> list) {
            super.onPostExecute(list);
            adapter3 = new Exercises_RecyclerView_Adapter(MenuActivity.this, list);

        }
    }


}
