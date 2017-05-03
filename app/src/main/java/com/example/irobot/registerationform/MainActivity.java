package com.example.irobot.registerationform;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    EditText username, password, cpassword, email, fname, lname;
    Spinner spGender;
    ArrayAdapter<String> adapter;
    CheckBox news, terms;
    Button submit;
    UserDetails ud;
    ContentResolver resolver;
    ContentValues values;
    Intent rcv ;
    Boolean updateMode;

    UserDetails userdetails;
   // Intent rcv;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
   // Intent rcv = getIntent();
    //Boolean updateMode = rcv.hasExtra("object");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);
        spGender = (Spinner) findViewById(R.id.gender);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
        adapter.add("-----Select Gender-----");
        adapter.add("Male");
        adapter.add("Female");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Proceeding..");
        progressDialog.setCancelable(false);
        rcv  = getIntent();
        updateMode = rcv.hasExtra("keyUser");



        spGender.setAdapter(adapter);
        spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    ud.setGender(adapter.getItem(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        resolver = getContentResolver();

        if (updateMode) {
            updateUser();
        }

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        cpassword = (EditText) findViewById(R.id.confirmPassword);
        email = (EditText) findViewById(R.id.email);
        fname = (EditText) findViewById(R.id.firstName);
        lname = (EditText) findViewById(R.id.lastName);

        ud = new UserDetails();


    }


    public void clickhandler(View view) {


        ud.setUsername(username.getText().toString().trim());


        ud.setPassword(password.getText().toString());


        String confirm = cpassword.getText().toString();


        ud.setEmail(email.getText().toString().trim());


        ud.setFname(fname.getText().toString().trim());


        ud.setLname(lname.getText().toString().trim());


        /*if (!updateMode) {

            insertDB();
        } else {
            String where = Util.COL_ID + "=" + userdetails.getId();
            values = new ContentValues();
            values.put(Util.COL_Username, ud.getUsername());
            values.put(Util.COL_password, ud.getPassword());
            values.put(Util.COL_Email, ud.getEmail());
            values.put(Util.COL_fname, ud.getFname());
            values.put(Util.COL_lname, ud.getLname());
            values.put(Util.COL_gender, ud.getGender());
            int f = resolver.update(Util.URI, values, where, null);
            if (f > 0) {
                Toast.makeText(this, "Updated successfully..", Toast.LENGTH_SHORT).show();
                Log.i("value", String.valueOf(f));
                clearFields();
            }
        }*/




            insertIntoCloud();





    }

    public void insertIntoCloud() {
        String url;
        final String msg;
        rcv= getIntent();
        final UserDetails uf = (UserDetails) rcv.getSerializableExtra("keyUser");

        updateMode = rcv.hasExtra("keyUser");
        Log.i("update mode", String.valueOf(updateMode));


        if (!updateMode){
            url = Util.INSERT_URL_PHP;
            msg = "registerd successfully..";

        }else{
            url = Util.UPDATE_URL_PHP;
            msg = "updated successfully";
        }
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(MainActivity.this,response + msg,Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();


            }

        }, new Response.ErrorListener() {
            @Override

            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Sorry! some error occured",Toast.LENGTH_LONG).show();
            }
        }){
        protected Map<String, String> getParams () throws AuthFailureError {
           Map<String, String > map = new HashMap<>();
            if(updateMode){

            map.put("id",String.valueOf(uf.getId()));

            map.put("name1",ud.getUsername());
            map.put("pass",ud.getPassword());
            map.put("email",ud.getEmail());
            map.put("gender",ud.getGender());
            map.put("fname",ud.getFname());
            map.put("lname",ud.getLname());

            return map;}
            else{
                map.put("name1",ud.getUsername());
                map.put("pass",ud.getPassword());
                map.put("email",ud.getEmail());
                map.put("gender",ud.getGender());
                map.put("fname",ud.getFname());
                map.put("lname",ud.getLname());
                return map;
            }

        }


         }

         ;

            requestQueue.add(request);
        clearFields();
}


    public void insertDB(){
        values = new ContentValues();
        values.put(Util.COL_Username,ud.getUsername());
        values.put(Util.COL_password,ud.getPassword());
        values.put(Util.COL_Email,ud.getEmail());
        values.put(Util.COL_fname,ud.getFname());
        values.put(Util.COL_lname,ud.getLname());
        values.put(Util.COL_gender,ud.getGender());

        Uri dummy = resolver.insert(Util.URI,values);

        Toast.makeText(this,ud.getUsername()+"registered successfully."+dummy.getLastPathSegment(),Toast.LENGTH_SHORT).show();
        clearFields();
    }


    public void clearFields(){

        username.setText("");
        password.setText("");
        cpassword.setText("");
        email.setText("");
        lname.setText("");
        fname.setText("");
        spGender.setSelection(0);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0,101,0,"Details");


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id==101){
            Intent i = new Intent(MainActivity.this, userlist.class);
            startActivity(i);
        }



        return super.onOptionsItemSelected(item);
    }


    public void updateUser(){
        rcv = getIntent();
        userdetails = (UserDetails)rcv.getSerializableExtra("keyUser");
        Log.i("values",userdetails.toString());
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        cpassword = (EditText)findViewById(R.id.confirmPassword);
        email = (EditText)findViewById(R.id.email);
        fname = (EditText)findViewById(R.id.firstName);
        lname = (EditText)findViewById(R.id.lastName);
        username.setText(userdetails.getUsername());
        password.setText(userdetails.getPassword());
        email.setText(userdetails.getEmail());
        fname.setText(userdetails.getFname());
        lname.setText(userdetails.getLname());

        int p = 0;
        for(int i= 0 ;i<adapter.getCount();i++){
            if(adapter.getItem(i).equals(userdetails.getGender())){
                p= i;
            }

        }
        spGender.setSelection(p);
        submit = (Button) findViewById(R.id.submit);

        submit.setText("Update");



    }

}
