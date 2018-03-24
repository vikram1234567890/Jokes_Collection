package oj.com.ownjokes;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class Add extends AppCompatActivity implements View.OnClickListener,TextWatcher {
    private  String www="";

    private Button add;
    private TextView textView,textView2;
    private EditText joke,name;
    private Spinner language,cat;
    private ArrayAdapter arrayAdapter,arrayAdapter1;
    private ArrayList<String > arrayList=new ArrayList<>(),arrayList2=new ArrayList<String >();
    private  int spindex=0,RC_SIGN_IN=0,count=0,length=0,characters,refreshCount=0;
    private  AlertDialog.Builder alertDialogbuilder;
    private AlertDialog alertDialog;
    private  GoogleApiClient mGoogleApiClient;

    protected static String share_msg="Read funny jokes or add your joke\nhttps://play.google.com/store/apps/details?id=oj.com.ownjokes",verified[]={"yes","no"};
    private String add_to_database="",temp[]=new String[99999],robot="",sensitive,duplicate;
    protected final String r[]={"m","o","c",".","m","a","r","k","i","v","@","t","o","b","o","r"};
    protected final String[] a={"s","e","k","o","j","n","w","o","/","m","o","c",".","p","p","a","t","s","o","h","b","e","w","0","0","0",".","d","i","o","r","d","n","a","b","e","w","/","/",":","p","t","t","h"};
    private TextView belowProgressBar;
    private ProgressBar load_progressBar;


    protected final String[] message={/*0*/    "Joke already exists in this app!!\nYour provided joke",
      /*1*/      "Your joke contains some sensitive word(s) that should be in Adult category, please remove it and try again or add this joke in Adult category\nFor more information check out help",
      /*2*/      "Why email id is required to add joke?\n-To identify your joke and you will also be able to later edit or delete it if you wish .\n\nWARNING:Do not add unnecessary content or jokes that contains Religion,caste, or anything that may hurt anyone's feelings.\nSuch types of content will be deleted",
      /*3*/      "You haven't entered any joke",
      /*4*/      "Select the language of joke",
      /*5*/      "Your joke has been added successfully!!!" ,
      /*6*/      "",
      /*7*/      share_msg,
      /*8*/     "More than 5 words or more than 20 characters should be there in your joke",
       /*9*/     "Matches with this joke in this app",
      /*10*/     " \"" ,
    /*11*/         "You need to Sign In using Google+ so that you can access your jokes anytime and modify it\nPress cancel to continue anyway(you will not be able to modify your joke later, and your joke will take few hours to available in this app.)",
    /*12*/         "\nIt will be available in this app as soon as it is verified.\n\n",
    /*13*/          "Failed to add joke,check your internet connection ",
    /*14*/          "Select category"};





    private JSONObject jsonObject;

    private String old_joke;
    private String joke_id="0";
    private int success=1,catindex;
    private ProgressBar progressBar;
    private boolean status=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        belowProgressBar=(TextView) findViewById(R.id.textView11);
        progressBar=(ProgressBar) findViewById(R.id.progressBar2);
        textView=(TextView) findViewById(R.id.textView2);
        textView2=(TextView) findViewById(R.id.textView

        );
        load_progressBar=(ProgressBar) findViewById(R.id.load_progress);
        joke=(EditText) findViewById(R.id.editText);
        name=(EditText) findViewById(R.id.editText2);
         add=(Button) findViewById(R.id.button);
        cat=(Spinner) findViewById(R.id.category);
        language=(Spinner) findViewById(R.id.spinner);
        //new DBtemp(this).insertEmail("vickyramekar@gmail.com");



        for(int i=a.length-1;i>=0;i--){
            www=www+a[i];
        }
        for(int i=r.length-1;i>=0;i--){
            robot=robot+r[i];
        }
        if (new DBtemp(this).getEmail().length()!=0)
        {
            textView.setTextColor(getResources().getColor(R.color.white));
            textView.setBackgroundColor(getResources().getColor(R.color.black));
            textView.setText(new DBtemp(this).getEmail());
        }


         joke.addTextChangedListener(Add.this);

        add.setOnClickListener(Add.this);
        if (getIntent().getStringExtra("j")!=null){
            getSupportActionBar().setTitle("Update Joke");
            joke.setText(getIntent().getStringExtra("j"));
            add.setText("Update");
            old_joke=getIntent().getStringExtra("j");
        }
        if (getIntent().getStringExtra("n")!=null){
            name.setText(getIntent().getStringExtra("n"));
        }
        if (getIntent().getStringExtra("j_id")!=null){
           joke_id= getIntent().getStringExtra("j_id");
        }
        new Language().execute();

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            }
        }// Handle other intents, such as being started from the home screen

interstitialAd(this);
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            joke.setText(sharedText);

        }
    }

  /*  protected boolean DuplicateChecker(String string,String to_match_with )
    {
        boolean a=true;
        int count=0;
        float ac,bc;

        StringTokenizer a1,b1;
        a1=new StringTokenizer(string, " ");
        b1=new StringTokenizer(to_match_with, " ");
        ac=a1.countTokens();
        bc=b1.countTokens();


        if(a1.countTokens()<=b1.countTokens() && b1.countTokens()<=a1.countTokens()*2){
            while(a1.hasMoreTokens()){
                if(a1.nextToken().contains(b1.nextToken())){

                    count++;
                }
            }
        }

        else if(a1.countTokens()>b1.countTokens() &&  bc/ac*100f>=70){
            while(b1.hasMoreTokens()){
                if(b1.nextToken().contains(a1.nextToken())){

                    count++;


                }
            }
        }

        if(count>=(int)ac/(100f/80f)){
            alertDialogbuilder=new AlertDialog.Builder(this);
           builderWithoutButton("Duplicate Joke",message[0],"\""+string+"\"",message[9],"\""+to_match_with+"\"",alertDialogbuilder);
            //builderWithoutButton("Duplicate Joke",message[0]+string+message[9]+to_match_with+"\"",alertDialogbuilder);
            a=false;

        }


        return a;

    }*/






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add, menu);

        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case R.id.Help:
                alertDialogbuilder=new AlertDialog.Builder(Add.this);
                builderWithoutButton("Help",message[2],alertDialogbuilder);

                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

    }
    protected void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }
    private void SignInBuilder()
    {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(Add.this)

                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN ) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
               handleSignInResult(result);

        }
    }

    protected void handleSignInResult(GoogleSignInResult result) {

            alertDialogbuilder=new AlertDialog.Builder(this);
            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = result.getSignInAccount();
                textView.setTextColor(getResources().getColor(R.color.white));
                textView.setBackgroundColor(getResources().getColor(R.color.black));
                textView.setText(acct.getEmail());
                new DBtemp(this).insertEmail(acct.getEmail());
                new checkRemoteDuplicate().execute();

            }
            else {
                // Signed out, show unauthenticated UI.

                builderWithoutButton(null,"Sign In Failed :(",alertDialogbuilder);

            }

    }

    protected void adult(Context context){
        alertDialogbuilder=new AlertDialog.Builder(context);
        alertDialogbuilder.setTitle("Sign In").setMessage("You need to sign in to add Adult jokes.!!").setCancelable(true).setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                SignInBuilder();
                signIn();

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();
    }
    @Override
    public void onClick(View v) {
alertDialogbuilder=new AlertDialog.Builder(this);

        if (joke.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), message[3],Toast.LENGTH_SHORT).show();
        } else if (joke.getText().toString().trim().length() != 0 && spindex == 0) {
             Toast.makeText(getApplicationContext(), message[4],Toast.LENGTH_SHORT).show();

        } else if (joke.getText().toString().trim().length() != 0 && catindex == 0) {
        Toast.makeText(getApplicationContext(), message[14],Toast.LENGTH_SHORT).show();
    }
        else if (joke.getText().toString().trim().length() != 0 && spindex != 0) {

            if (length >= 5 || characters >20) {
                if (name.getText().toString().trim().length() != 0) {
                    add_to_database = joke.getText().toString() + "\n\n" + "by " + name.getText().toString();

                }
                else {

                    add_to_database = joke.getText().toString();

                }


                        if (isNetworkAvailable() && arrayList2.get(catindex).contains("Adult")){
                            if(new DBtemp(this).getEmail().length()==0) {
                                adult(this);       }
                            else {

                                new checkRemoteDuplicate().execute();

                            }
                        }else if (isNetworkAvailable()) {
                            if(new DBtemp(this).getEmail().length()==0) {
                                builderWithButton("Sign In","", message[11], null, "Continue", "Cancel");
                            }
                            else {

                                new checkRemoteDuplicate().execute();

                            }

                        } else {
                            builderWithButton("No internet connection","", message[13] , null, "Ok", null);
                        }

            }
            else
            {
                builderWithoutButton(null,message[8],alertDialogbuilder);
            }
        }
    }

private void check(){
    boolean a=true;

status=true;
    if (!sensitive.equalsIgnoreCase("false") && sensitive.trim().length()!=0){

        status=false;
           builderWithoutButton("Warning!!",message[1],alertDialogbuilder);
    }else
        a=false;
     if (status && !duplicate.equalsIgnoreCase("false") && duplicate.trim().length()!=0) {
            builderWithoutButton("Duplicate Joke",message[0],"\""+joke.getText().toString()+"\"",message[9],"\""+duplicate+"\"",alertDialogbuilder);

            status=false;
        }


        if (arrayList2.get(catindex).contains("Adult"))
            a=false;


        if (status && !a) {
            if (new DBtemp(this).getEmail().length()==0) {

                new InsertJoke(name.getText().toString(), robot, joke.getText().toString(), "no").execute();
            } else if (new DBtemp(this).getEmail().length()!=0) {
                 if (add.getText().toString().equals("Update")) {

                     new UpdateJoke(name.getText().toString(), new DBtemp(Add.this).getEmail(), joke_id, joke.getText().toString()).execute();
                 }
                else
                    new InsertJoke(name.getText().toString(),new DBtemp(Add.this).getEmail(), joke.getText().toString(), "yes").execute();
            }

        }


}





    @Override
    public void onBackPressed() {


        Intent i=new Intent(Add.this,MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        length=0;

        String s1=joke.getText().toString();
        characters=s1.trim().length();
        if (s1.contains("\n"))
        {
            s1=s1.replace("\n"," ");
        }
        if (joke.getText().toString().trim().length()!=0) {
            temp = s1.split(" ");

            for (int i = 0; i < temp.length; i++) {
                if (temp[i].contains(" "))
                    ++i;
                if (temp[i] != null && !temp[i].trim().equals("") ) {
                    length++;
                }
                else  if (temp[i] == null)
                break;
            }
            textView2.setText("Words in your joke " + length+"\nCharacters in your joke "+characters);
        }
        else if(joke.getText().toString().trim().length()==0)
        {
            textView2.setText("");
        }
    }



    @Override
    public void afterTextChanged(Editable s) {

    }


     protected void builderWithButton(String mainTitle,String title, String message, final String intent_message, String message_on_btn, String message_on_btn2)
        {

        AlertDialog.Builder alertDialogbuilder=new AlertDialog.Builder(this);



            // set dialog message
             LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       
            View dialogView= inflater.inflate(R.layout.custom_builder_layout, null);
            alertDialogbuilder.setView(dialogView);
            TextView mainTitle_textView=(TextView) dialogView.findViewById(R.id.mainTitle);

            TextView textView=(TextView) dialogView.findViewById(R.id.textView6);
            TextView textView1=(TextView) dialogView.findViewById(R.id.textView7);
            TextView textView2=(TextView) dialogView.findViewById(R.id.textView9);
            TextView textView3=(TextView) dialogView.findViewById(R.id.textView10);
            mainTitle_textView.setText(mainTitle);
            textView.setText(title);
            textView1.setText(message);
            textView2.setVisibility(View.GONE);
            textView3.setVisibility(View.GONE);

    if (title.length()==0){
        textView.setVisibility(View.GONE);
    }





            if (message_on_btn=="Continue") {

                   alertDialogbuilder.setCancelable(false).setPositiveButton(message_on_btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        dialog.dismiss();
                    dialog.cancel();
                        removeDialog(id);

                    SignInBuilder();
                    signIn();

                    }
                });
            }
            else
            {
                alertDialogbuilder.setCancelable(false).setPositiveButton(message_on_btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.dismiss();
                        dialog.cancel();
                        removeDialog(id);

                    }
                });
            }
            if (message_on_btn2=="Cancel") {
                alertDialogbuilder.setCancelable(false).setNeutralButton(message_on_btn2, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                            dialog.cancel();
                        removeDialog(id);

                        new checkRemoteDuplicate().execute();
                    }
                });
            }
            else {
                if (intent_message!=null) {
                    alertDialogbuilder.setCancelable(true).setNeutralButton(message_on_btn2, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, intent_message);
                            sendIntent.setType("text/plain");
                            startActivity(sendIntent);

                        }
                    });
                }
            }

            alertDialog = alertDialogbuilder.setCancelable(true).create();
            alertDialog.show();



        }
    public void builderWithoutButton(String mainTitle,String title,String message,String title2,String message2,AlertDialog.Builder builder)
    {

         LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       
        View dialogView= inflater.inflate(R.layout.custom_builder_layout, null);
        builder.setView(dialogView);
        TextView mainTitle_textView=(TextView) dialogView.findViewById(R.id.mainTitle);

        TextView textView=(TextView) dialogView.findViewById(R.id.textView6);
        TextView textView1=(TextView) dialogView.findViewById(R.id.textView7);
        TextView textView2=(TextView) dialogView.findViewById(R.id.textView9);
        TextView textView3=(TextView) dialogView.findViewById(R.id.textView10);
        mainTitle_textView.setText(mainTitle);
        textView.setText(title);
        textView1.setText(message);
        textView2.setText(title2);
        textView3.setText(message2);
        if (title2.length()==0){
            textView2.setVisibility(View.GONE);
            textView3.setVisibility(View.GONE);
        }


         alertDialog = builder.setCancelable(true).create();
        alertDialog.show();

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



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    class UpdateJoke extends AsyncTask<Void,Void,String> {

        private  final String Insert_joke = www+"/update_joke.php";
        private final String language =arrayList.get(spindex),category=arrayList2.get(catindex);
        private String name, email, joke_id,joke;


        public UpdateJoke(String name,String email,String joke_id,String joke)
        {
            this.name=name;
            this.email=email;
            this.joke_id=joke_id;
            this.joke=joke;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            visibleProgrogressBar("Updating joke..","Please Wait...");
            }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

           invisibleProgrogressBar();
            checkSuccess(s,"Joke Updated!!");

          }

        @Override
        protected String doInBackground(Void... v) {
            HashMap<String,String> params = new HashMap<>();

            params.put("name",name);
            params.put("email_id",email);
            params.put("joke_id",joke_id);
            params.put("joke",joke);
            params.put("language",language);
            params.put("category",category);


            RequestHandler rh = new RequestHandler();
            String res = rh.sendPostRequest(Insert_joke, params);
            return res;
        }
    }




    class InsertJoke extends AsyncTask<Void,Void,String> {

        private  final String Insert_joke = www+"/insert_joke.php";
        private final String language =arrayList.get(spindex),category=arrayList2.get(catindex);
        private String name, email, joke,verified;


        public InsertJoke(String name,String email,String joke,String verified)
        {
            this.name=name;
            this.email=email;
            this.joke=joke;
            this.verified=verified;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            visibleProgrogressBar("Adding joke..","Please Wait...");
                 }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

          invisibleProgrogressBar();
          checkSuccess(s,"Joke added!!");


        }

        @Override
        protected String doInBackground(Void... v) {
            HashMap<String,String> params = new HashMap<>();

            params.put("name",name);
            params.put("email_id",email);
            params.put("joke",joke.replace("'","\""));
            params.put("language",language);
            params.put("verified",verified);
            params.put("category",category);
            RequestHandler rh = new RequestHandler();
            String res = rh.sendPostRequest(Insert_joke, params);
            return res;
        }
    }

    private void checkSuccess(String s,String message) {
        try {
            jsonObject = new JSONObject(s);

            success=Integer.parseInt(jsonObject.getString("success"));
            if (success==0) {
                if ( new DBtemp(this).getEmail().length()!=0) {
                    builderWithButton(message, Add.this.message[5] ,add_to_database, add_to_database + "\n\n" + Add.this.message[7], "Done", "Share");
                } else if ( new DBtemp(this).getEmail().length()==0) {
                    builderWithButton(message, Add.this.message[5] + Add.this.message[12] , add_to_database, add_to_database + "\n\n" + Add.this.message[7], "Done", "Share");
                }
                new MainActivity().changeSharedPref("extra","1",Add.this);

            }
            else
                Toast.makeText(getApplicationContext(),"Failed,try again!",Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }







    public class checkRemoteDuplicate extends AsyncTask<Void,Void,String >{
//loads sensitive words and duplicate words

        private  final String check_joke = www+"/check_joke.php";

        private String temp_joke;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            visibleProgrogressBar( "Please wait", "Checking for duplicate joke....");
            temp_joke=joke.getText().toString();
                  }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
           invisibleProgrogressBar();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showJSON(s);
            }
        });

        }

        @Override
        protected String  doInBackground(Void... v) {
            HashMap<String,String> params = new HashMap<>();
            params.put("user_joke",temp_joke);
            params.put("joke_id",joke_id);
            RequestHandler rh = new RequestHandler();
            String res = rh.sendPostRequest(check_joke, params);
            return res;
        }
    }


    private void showJSON(String json){

        String sensitive_words_column="sensitive",joke_column="duplicate";

        try {
            jsonObject = new JSONObject(json);
            sensitive=jsonObject.getString(sensitive_words_column);
            duplicate=jsonObject.getString(joke_column);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (sensitive!=null && duplicate!=null) {
            refreshCount=0;

            check();
        }
        else if (refreshCount>=10){
            alertDialogbuilder=new AlertDialog.Builder(this);
            builderWithoutButton("Network Problem","Unable to connect to server maybe network problem or server may be down please try again after few minutes",alertDialogbuilder);
            refreshCount=0;
        }
        else {
            refreshCount++;
            new checkRemoteDuplicate().execute();
        }

    }




    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(Add.this,MainActivity.class);
        startActivity(intent);
        finish();
        return true;
    }




    protected  void interstitialAd(Context context){
        final InterstitialAd mInterstitialAd;
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId("ca-app-pub-1211635675454735/9991028806");
        AdRequest adRequest = new AdRequest.Builder() .build();
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }});
    }


    public class Language extends AsyncTask<Void,Void,String> {

        private  final String category = www+"/get_language.php";

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            load_progressBar.setVisibility(View.VISIBLE);

            arrayList.clear();
            Toast.makeText(getApplicationContext(),"Loading data",Toast.LENGTH_SHORT).show();
            arrayList.add("--Select Language--");


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
               category(s,"language");
          }

        @Override
        protected String doInBackground(Void... v) {
            HashMap<String,String> params = new HashMap<>();
            RequestHandler rh = new RequestHandler();
            String res = rh.sendPostRequest(category, params);
            return res;
        }
    }

    public class Category extends AsyncTask<Void,Void,String> {

        private  final String category = www+"/get_category.php",language=arrayList.get(spindex);

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            load_progressBar.setVisibility(View.VISIBLE);

            Toast toast;
            toast=Toast.makeText(getApplicationContext(),"Loading jokes category,please wait",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            arrayList2.clear();
            arrayList2.add("--Select joke category--");

            category(s,"category");
        }

        @Override
        protected String doInBackground(Void... v) {
            HashMap<String,String> params = new HashMap<>();
            RequestHandler rh = new RequestHandler();
            params.put("language",language);
            String res = rh.sendPostRequest(category, params);
            return res;
        }
    }


    private void category(String json,String input) {
        String category;
        try {
            jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(input);

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);

                category = jo.getString(input);
                if (input.equals("category"))
                    arrayList2.add(category);
                else
                    arrayList.add(category);

            }
        if (input.equals("category")) {
                     if (arrayList2.size()<=1) {
                refreshCount++;
                if (refreshCount > 6) {
                    refreshCount = 0;
                    new Add().builderWithoutButton("Something went wrong!!", "Unable to load joke category, maybe network problem,please try again within few minutes", alertDialogbuilder);
                } else
                new Category().execute();
            } else {
                         load_progressBar.setVisibility(View.GONE);
                arrayAdapter1 = new ArrayAdapter(this, R.layout.spinner_dropdown, arrayList2);

                cat.setAdapter(arrayAdapter1);
                cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        catindex = i;

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        }else {

            if (arrayList.size() <=1) {
                refreshCount++;
                if (refreshCount > 6) {
                    refreshCount = 0;
                    new Add().builderWithoutButton("Something went wrong!!", "Unable to load joke languages, maybe network problem,please try again within few minutes", alertDialogbuilder);
                }else
                new Language().execute();
            } else {
                load_progressBar.setVisibility(View.GONE);
                arrayAdapter=new ArrayAdapter(this, R.layout.spinner_dropdown,arrayList);
                language.setAdapter(arrayAdapter);
                language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        spindex=position;
                        arrayList2.clear();
                        arrayAdapter1 = new ArrayAdapter(Add.this, R.layout.spinner_dropdown, arrayList2);

                        cat.setAdapter(arrayAdapter1);
                        if (position!=0)
                            new Category().execute();




                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }
        }

        } catch (JSONException e) {
            e.printStackTrace();

        }

    }

    private void visibleProgrogressBar(String title,String message){

        progressBar.setVisibility(View.VISIBLE);
        belowProgressBar.setVisibility(View.VISIBLE);
        belowProgressBar.setText(title+"\n"+message);
    }
    private void invisibleProgrogressBar(){

        progressBar.setVisibility(View.GONE);

        belowProgressBar.setVisibility(View.GONE);

    }
}
