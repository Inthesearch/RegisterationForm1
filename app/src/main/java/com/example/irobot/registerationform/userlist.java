package com.example.irobot.registerationform;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class userlist extends AppCompatActivity implements AdapterView.OnItemClickListener {

    Arrayadapter ar;
    ArrayList<UserDetails> list;
    ContentResolver resolver;
    ListView listView;
    UserDetails user;
    int position;
    EditText searchtxt;
    Intent intent;
    // ArrayList<UserDetails> userlist;
    JSONObject jsonObject;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlist);
        resolver = getContentResolver();
        listView = (ListView) findViewById(R.id.listview);
        intent = new Intent(this, MainActivity.class);
        requestQueue = Volley.newRequestQueue(this);
        //retrieveData();
        retrieveFromCloud();
        searchtxt = (EditText) findViewById(R.id.searchtxt);
        searchtxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String str = s.toString();
                if (ar != null) {
                    ar.filter(str);


                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }


    public void retrieveData() {

        list = new ArrayList<>();

        String[] projection = {Util.COL_ID, Util.COL_Email, Util.COL_fname, Util.COL_lname, Util.COL_Username, Util.COL_password, Util.COL_gender};

        Log.i("Test", "Success");
        Cursor cursor = resolver.query(Util.URI, projection, null, null, null);

        if (cursor != null) {
            int i = 0;
            String e = "", fname = "", lname = "", username = "", password = "", gender = "";

            while (cursor.moveToNext()) {
                i = cursor.getInt(cursor.getColumnIndex(Util.COL_ID));
                e = cursor.getString(cursor.getColumnIndex(Util.COL_Email));
                fname = cursor.getString(cursor.getColumnIndex(Util.COL_fname));
                lname = cursor.getString(cursor.getColumnIndex(Util.COL_lname));
                username = cursor.getString(cursor.getColumnIndex(Util.COL_Username));
                password = cursor.getString(cursor.getColumnIndex(Util.COL_password));
                gender = cursor.getString(cursor.getColumnIndex(Util.COL_gender));

                list.add(new UserDetails(i, username, password, e, fname, lname, gender));
            }

            ar = new Arrayadapter(this, R.layout.listitem, list);
            listView.setAdapter(ar);
            listView.setOnItemClickListener(this);


        }


    }

    public void retrieveFromCloud() {

        list = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Util.RETRIEVE_URL_PHP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("students");


                    int id = 0;

                    String username = "", p = "", e = "", f = "", l = "", g = "";

                    for (int i = 0; i < jsonArray.length(); i++) {


                        JSONObject obj = jsonArray.getJSONObject(i);


                        id = obj.getInt("_ID");
                        username = obj.getString("Username");
                        p = obj.getString("Password");
                        e = obj.getString("Email");
                        f = obj.getString("First_Name");
                        l = obj.getString("Last_Name");
                        g = obj.getString("Gender");

                        list.add(new UserDetails(id, username, p, e, f, l, g));

                    }

                    ar = new Arrayadapter(userlist.this, R.layout.listitem, list);
                    listView.setAdapter(ar);


                    listView.setOnItemClickListener(userlist.this);


                } catch (Exception e) {
                    e.printStackTrace();

                    Toast.makeText(userlist.this, "unsuccessful", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(userlist.this, "Sorry! some error occured.", Toast.LENGTH_LONG).show();

            }
        });


        requestQueue.add(stringRequest);


    }


    public void showOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] items = {"View", "Delete", "Update"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        showUser();
                        break;
                    case 1:
                        //deleteUser();
                        deleteFromCloud();
                        break;
                    case 2:
                        updateUser();
                        break;


                }
            }
        });

        builder.create().show();
    }

    public void showUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Details:");
        builder.setMessage(user.toString());
        builder.setPositiveButton("Done", null);
        builder.create().show();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.position = position;
        user = list.get(position);
        showOptions();
    }

    public void deleteUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + user.getUsername());
        builder.setMessage("Are you sure want to delete?");

        builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String where = Util.COL_ID + "=" + user.getId();
                int j = resolver.delete(Util.URI, where, null);
                if (j > 0) {
                    list.remove(position);
                    ar.notifyDataSetChanged();
                    Toast.makeText(userlist.this, user.getUsername() + "deleted..", Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton("cancel", null);
        builder.create().show();


    }

    public void deleteFromCloud() {

        StringRequest deleteRequest = new StringRequest(Request.Method.POST, Util.DELETE_URL_PHP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("success");
                    if (success == 1) {

                        Toast.makeText(userlist.this, "Record deleted successfully..", Toast.LENGTH_LONG).show();
                        list.remove(position);
                        ar.notifyDataSetChanged();
                    } else {
                        Toast.makeText(userlist.this, "not deleted", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(userlist.this, "some error", Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();

                map.put("id", String.valueOf(user.getId()));
                return map;

            }
        };
        requestQueue.add(deleteRequest);

    }


    public void updateUser() {

        Intent intent = new Intent(userlist.this, MainActivity.class);
        intent.putExtra("keyUser", user);
        startActivity(intent);


    }

}
