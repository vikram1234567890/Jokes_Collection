package oj.com.ownjokes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static oj.com.ownjokes.MainActivity.getApplicationName;

public class Contact extends AppCompatActivity implements View.OnClickListener{
EditText editText;
    Button submit;
    AlertDialog.Builder builder;

    private int success=1;
    private String www="";
    private JSONObject jsonObject;
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        for(int i=new Add().a.length-1;i>=0;i--){
            www=www+new Add().a[i];
        }
        editText=(EditText) findViewById(R.id.editText4);
        submit=(Button) findViewById(R.id.submit);
        submit.setOnClickListener(this);


    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(Contact.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {

        builder=new AlertDialog.Builder(Contact.this);


            if (editText.getText().toString().trim().length()!=0 && isNetworkAvailable())
            {
                new sendFeedback(new DBtemp(Contact.this).getEmail()).execute();
                new SendMail(Contact.this,new DBtemp(Contact.this).getEmail(),"Your "+getApplicationName(Contact.this)+" feedback",editText.getText().toString()).execute();

            }
            else if (!isNetworkAvailable())
            {

                builderWithoutButton("No internet connection","Check your internet connection and try again",builder);
            }

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(Contact.this,MainActivity.class);
        startActivity(intent);
        finish();
        return true;
    }


    public class sendFeedback extends AsyncTask<Void,Void,String> {

        private  final String get_feedback= www+"/feedback.php";

        private String  email;
        private ProgressDialog pDialog;
        String feedback=editText.getText().toString();
        public sendFeedback(String email)
        {
            this.email=email;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = ProgressDialog.show(Contact.this,null,"Please Wait...",false,false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();

            showJSON(s);
        }

        @Override
        protected String doInBackground(Void... v) {
            HashMap<String,String> params = new HashMap<>();


                params.put("email_id",email);//send  email to php
                params.put("feedback",feedback);

            RequestHandler rh = new RequestHandler();
            String res = rh.sendPostRequest(get_feedback, params);
            return res;
        }
    }


    private void showJSON(String json){

        try {
            jsonObject = new JSONObject(json);
            String result = jsonObject.getString("success");
            success = Integer.parseInt(result);

            if (success==0)
                builderWithoutButton("Thank you for your feedback","If you have asked any question,you will receive reply soon on your email !!",builder);
            else Toast.makeText(getApplicationContext(),"Network error,please try again",Toast.LENGTH_LONG).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }





    }
    public void builderWithoutButton(String title,String message,AlertDialog.Builder builder)
    {

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView= inflater.inflate(R.layout.custom_builder_layout, null);
        builder.setView(dialogView);
        TextView mainTitle_textView=(TextView) dialogView.findViewById(R.id.mainTitle);

        TextView textView=(TextView) dialogView.findViewById(R.id.textView6);
        TextView textView1=(TextView) dialogView.findViewById(R.id.textView7);
        TextView textView2=(TextView) dialogView.findViewById(R.id.textView9);
        TextView textView3=(TextView) dialogView.findViewById(R.id.textView10);
        if (title==null || title.length()==0)
            mainTitle_textView.setVisibility(View.GONE);
        else
            mainTitle_textView.setText(title);
        textView1.setText(message);

        textView.setVisibility(View.GONE);
        textView2.setVisibility(View.GONE);
        textView3.setVisibility(View.GONE);

        alertDialog = builder.setCancelable(true).create();
        alertDialog.show();


    }
}
