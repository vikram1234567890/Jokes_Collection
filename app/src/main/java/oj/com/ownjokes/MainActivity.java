package oj.com.ownjokes;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static oj.com.ownjokes.R.layout.jokes;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,UpdateMainClass {
    private int count=0,rateCount=0;
    protected Menu menu;
    ArrayList<String> lang=new ArrayList<>(),category=new ArrayList<>();
    private android.support.v7.app.AlertDialog.Builder alertDialogbuilder;


    protected final static ArrayList<Item> arrayList=new ArrayList<Item>();
    private final  ArrayList<Item> memorySavingList=new ArrayList<Item>();

    int index,refreshCount=0;
    private String shared_pref="shared_pref";
    public TextView textView,textView1;
    EditText editText;
    private RecyclerView recyclerView;
    HashMap<String, String> hashMap=new HashMap<>();
    ImageButton imageButton;
    private JSONObject jsonObject;
    protected GoogleApiClient mGoogleApiClient;
    private int RC_SIGN_IN=0;
    private String www="";
    protected static String tag;
    private String spData[]={"","no","0","","no","1","0","0","no","","0","yes"};
    private String sharedPref[]={"language","Rate","rcount","category","my_jokes","extra","J_tag","N_tag","search","search_data","position","change"};
    Toast toast = null;

    private int catindex,i;
    NavigationView navigationView;
    protected AlertDialog alertDialog;
    private RelativeLayout relativeLayout;
   // ScrollView scrollView;

    private SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar,loadProgressBar;
    TextView belowProgressBar;

    private int totalItemCount,lastVisibleItem;
    private boolean isLoading;

    private SimpleItemRecyclerViewAdapter simpleItemRecyclerViewAdapter;

    private String version;
    private static int jokes_count;
    private static  boolean initial=true;
    static int start;
    private int end;

    JokeDetails jokeDetails;
    private void memorySaving(){
        int index = memorySavingList.size();
        int end = index + 30;

        for (int i = index; i <=end; i++) {
            if (i<arrayList.size())
                memorySavingList.add( arrayList.get(i));
        }
    }
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         if (getIntent().getExtras()!=null){
            for (String key:getIntent().getExtras().keySet()){
                if (key.equals("title")){
                    Toast.makeText(getApplicationContext(),getIntent().getExtras().getString(key),Toast.LENGTH_SHORT).show();
                }
            }
        }
        memorySavingList.clear();
        memorySaving();

        if (!getSharedPref(sharedPref[sharedPref.length-1],MainActivity.this).equals(spData[spData.length-1])) {
            for (int i = 0; i < sharedPref.length; i++) {
                changeSharedPref(sharedPref[i], spData[i], MainActivity.this);
            }
        }
        loadProgressBar=(ProgressBar) findViewById(R.id.load_progress);
        progressBar=(ProgressBar) findViewById(R.id.progressBar3);
        belowProgressBar=(TextView) findViewById(R.id.textView12);
        swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                reset();
                cancel();
            }
        });
        relativeLayout=(RelativeLayout) findViewById(R.id.relative);
        textView1=(TextView) findViewById(R.id.total_jokes);
        editText=(EditText) findViewById(R.id.Search);
        imageButton=(ImageButton) findViewById(R.id.cancel);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
                changeSharedPref("search","no",MainActivity.this);
                changeSharedPref("search_data","",MainActivity.this);
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    if (editText.getText().toString().trim().length()!=0) {
                        reset();
                        j();
                    }
                    return true;
                }
                return false;
            }
        });




        for(int i=new Add().a.length-1;i>=0;i--){
            www=www+new Add().a[i];
        }
           Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            AddJoke();
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(MainActivity.this,"Add Joke",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();


         navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        View header=navigationView.getHeaderView(0);
        textView= (TextView) header.findViewById(R.id.email);
        if (new DBtemp(MainActivity.this).getEmail().length()!=0)
            textView.setText(new DBtemp(MainActivity.this).getEmail());
        recyclerView = (RecyclerView) findViewById(R.id.item_list);

        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(getResources().getDrawable(R.drawable.line_divider));

        recyclerView.addItemDecoration(dividerItemDecoration);

        Handler handler = new Handler();
        handler.postDelayed(task, 2);














        }


    public class UpdateChecker extends AsyncTask<Void,Void,String> {

        private  final String url = www+"/update_app.php";





        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            verifyInternetPermissions();
            PackageInfo pInfo = null;
            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                int verCode = pInfo.versionCode;
                e.printStackTrace();
            }

            version = pInfo.versionName;}

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            showJSONUpdate(s);
        }

        @Override
        protected String doInBackground(Void... v) {
            HashMap<String,String> params = new HashMap<>();

            params.put("version",version);
            params.put("u_id","1");

            RequestHandler rh = new RequestHandler();
            String res = rh.sendPostRequest(url, params);
            return res;
        }
    }
    private void showJSONUpdate(String json) {
             String  s="",s1="";
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray result = jsonObject.getJSONArray("version");
                JSONArray result1 = jsonObject.getJSONArray("version_detail");

                for(int i = 0; i<result.length(); i++) {

                    s = result.getString(i);
                    s1 = result1.getString(i);
                }
                if (Float.parseFloat(s)>Float.parseFloat(version))
                    new Update(this,s,s1).message();
            }
            catch (Exception e){

            }

        }
    public void verifyInternetPermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        String mPermission = Manifest.permission.ACCESS_NETWORK_STATE;

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this, new String[]{mPermission},
                    2);

        }
    }
    private Runnable task = new Runnable() {
        public void run() {


            if (getSharedPref("search",MainActivity.this).equals("yes") && arrayList.size()!=0){
                relativeLayout.setVisibility(View.VISIBLE);
                editText.setText(getSharedPref("search_data",MainActivity.this));


            }
            else {

                changeSharedPref("search","no",MainActivity.this);
                changeSharedPref("search_data","",MainActivity.this);
                ConnectToInternet();

            }
            bannerAd(R.id.adView);

        }
    };

    protected void bannerAd(int id){
        AdView mAdView = (AdView) findViewById(id);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


    }

    private void connectToInternetMsg(){

        alertDialogbuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView= inflater.inflate(R.layout.custom_builder_layout, null);
        alertDialogbuilder.setView(dialogView);
        TextView mainTitle_textView=(TextView) dialogView.findViewById(R.id.mainTitle);

        TextView textView=(TextView) dialogView.findViewById(R.id.textView6);
        TextView textView1=(TextView) dialogView.findViewById(R.id.textView7);
        TextView textView2=(TextView) dialogView.findViewById(R.id.textView9);
        TextView textView3=(TextView) dialogView.findViewById(R.id.textView10);

        mainTitle_textView.setText("No internet connection");
        textView1.setText("Connect to internet connection to load jokes");

        textView.setVisibility(View.GONE);
        textView2.setVisibility(View.GONE);
        textView3.setVisibility(View.GONE);


        alertDialogbuilder.setCancelable(false).setPositiveButton("Try again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ConnectToInternet();
            }
        });
        alertDialog = alertDialogbuilder.create();
        alertDialog.show();
    }

    private void ConnectToInternet() {


        if (!isNetworkAvailable() && arrayList.size()==0) {
        connectToInternetMsg();

        } else {  if (getSharedPref("extra",this).contains("1") || arrayList.size()==0) {
                if (getSharedPref("language",this).length()==0 )
                {
                    reset();
                    new Language().execute();
                    changeSharedPref("extra","0",this);
                }
                else {
                    reset();
               j();
                }

            }else  if (arrayList.size()!=0){
            setupRecyclerView(recyclerView,memorySavingList);
        }


        }

    }


    public class SearchJoke extends AsyncTask<Void,Void,String> {

        private final String get_jokes = www+"/search_jokes.php";

        private final String language =getSharedPref("language",MainActivity.this),category=getSharedPref("category",MainActivity.this);
        String joke_to_search=editText.getText().toString();



        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            if (initial) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);



                    if (!getSharedPref("my_jokes", MainActivity.this).contains("yes"))

                        visibleProgrogressBar("Searching jokes..", "Please Wait...");
                    else {
                        visibleProgrogressBar("Searching jokes of " + new DBtemp(MainActivity.this).getEmail(), "Please Wait...");

                    }
                arrayList.clear();
                memorySavingList.clear();
            }
            else {
                loadProgressBar.setVisibility(View.VISIBLE);
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            invisibleProgrogressBar();
            swipeRefreshLayout.setRefreshing(false);
            loadProgressBar.setVisibility(View.GONE);
            showJSON(String.valueOf(s));
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            //  Toast.makeText(getApplicationContext(),String .valueOf(memorySavingList.size())+" "+String .valueOf(arrayList.size()),Toast.LENGTH_SHORT).show();

        }

        @Override
        protected String doInBackground(Void... v) {
            HashMap<String,String> params = new HashMap<>();


            params.put("language",language);
            params.put("category",category);
            params.put("start",String.valueOf(start));
            params.put("end",String.valueOf(end));
            params.put("joke_to_search",joke_to_search);
            if (getSharedPref("my_jokes", MainActivity.this).contains("yes"))

                params.put("email_id",new DBtemp(MainActivity.this).getEmail().trim());

            RequestHandler rh = new RequestHandler();
            String res = rh.sendPostRequest(get_jokes, params);
            return res;
        }
    }


    private void cancel(){

        editText.setText("");
        relativeLayout.setVisibility(View.GONE);
        reset();
        changeSharedPref("search","no",MainActivity.this);
        changeSharedPref("search_data","",MainActivity.this);
        if (!isNetworkAvailable()) {
            swipeRefreshLayout.setRefreshing(false);
            connectToInternetMsg();

        } else {
            j();
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (alertDialog!=null && alertDialog.isShowing()){
            alertDialog.dismiss();
        }
       else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (relativeLayout.isShown())
            {
                if (editText.getText().length()!=0)
            {
                cancel();

            }else {

                    relativeLayout.setVisibility(View.GONE);
                }

            }
            else {
                count++;
                if (count == 2) {

                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Press again to exit", Toast.LENGTH_SHORT).show();
                    changeSharedPref("extra","1",this);
                }

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        count=0;
        menu.clear();


        final MenuInflater inflater = getMenuInflater();
        if ( isNetworkAvailable() && getSharedPref("my_jokes",this).contains("no") && new DBtemp(MainActivity.this).getEmail().length()!=0 ){//home button is invisible

            inflater.inflate(R.menu.main, menu);

        }

        else if ( new DBtemp(MainActivity.this).getEmail().length()!=0 && getSharedPref("my_jokes",this).contains("yes") ) {//home button is visible i.e signed in

            inflater.inflate(R.menu.main, menu);
            inflater.inflate(R.menu.main3, menu);
            MenuItem menuItem=menu.getItem(1);//home ivisible
            menuItem.setVisible(false);

        }
        else if ( new DBtemp(MainActivity.this).getEmail().length()==0 ){//not signed in

            inflater.inflate(R.menu.main, menu);
            MenuItem menuItem=menu.getItem(2);//myjokes invisible
            menuItem.setVisible(false);
        }
        else if ( isNetworkAvailable() && new DBtemp(MainActivity.this).getEmail().length()==0 ){//not signed in

            inflater.inflate(R.menu.main, menu);
            MenuItem menuItem=menu.getItem(3);//signout invisible
            menuItem.setVisible(false);
        }  else if ( !isNetworkAvailable() && new DBtemp(MainActivity.this).getEmail().length()!=0 ){//not signed in

            inflater.inflate(R.menu.main, menu);
            MenuItem menuItem=menu.getItem(2);//myjokes invisible
            menuItem.setVisible(false);
        }

        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        count=0;
        try {


        switch (item.getItemId()) {


            case R.id.home:
                arrayList.clear();
                memorySavingList.clear();
                initial=true;
                changeSharedPref("my_jokes","no",MainActivity.this);
                onCreateOptionsMenu(menu);
                j();
                return true;
            case R.id.my_jokes:


                if (isNetworkAvailable())
                {
                    reset();
                    if ( new DBtemp(MainActivity.this).getEmail().length()==0) {
                        alertDialogbuilder = new AlertDialog.Builder(MainActivity.this);
                         LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);        
                        View dialogView= inflater.inflate(R.layout.custom_builder_layout, null);
                        alertDialogbuilder.setView(dialogView);
                        TextView mainTitle_textView=(TextView) dialogView.findViewById(R.id.mainTitle);

                        TextView textView=(TextView) dialogView.findViewById(R.id.textView6);
                        TextView textView1=(TextView) dialogView.findViewById(R.id.textView7);
                        TextView textView2=(TextView) dialogView.findViewById(R.id.textView9);
                        TextView textView3=(TextView) dialogView.findViewById(R.id.textView10);

                        mainTitle_textView.setText("Sign in");
                        textView1.setText("You need to sign in using google account to access your joke");

                        textView.setVisibility(View.GONE);
                        textView2.setVisibility(View.GONE);
                        textView3.setVisibility(View.GONE);


                        alertDialogbuilder.setCancelable(false).setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ConnectToInternet();
                            }
                        });

                        alertDialogbuilder.setCancelable(true).setPositiveButton("Continue",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                tag="myjokes";
                                SignInBuilder();
                                signIn();


                            }
                        });
                        alertDialog = alertDialogbuilder.create();
                        alertDialog.show();


                    }else {
                        changeSharedPref("my_jokes","yes",this);

                        
                        onCreateOptionsMenu(menu);
                     j();
                    }

                }
                return true;



            case R.id.search:
                if (!relativeLayout.isShown()) {

                    relativeLayout.setVisibility(View.VISIBLE);

                }
                else if (editText.getText().length()!=0)
                {
                    cancel();
                }else {
                     relativeLayout.setVisibility(View.GONE);
                }

                break;

            case R.id.signOut:
                arrayList.clear();
                memorySavingList.clear();
                initial=true;
                new DBtemp(this).deleteAllEmail();
                changeSharedPref("my_jokes","no",MainActivity.this);



               signout();

                break;
        }
        }
catch (Exception e){

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

    protected void SignInBuilder()
    {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)

                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    protected void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }
    private   void signout(){


        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        mGoogleApiClient.connect();




        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                if(mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            Toast.makeText(getApplicationContext(),"Signed Out",Toast.LENGTH_SHORT).show();
                            new DBtemp(MainActivity.this).deleteAllEmail();
                            textView.setText(new DBtemp(MainActivity.this).getEmail());
                            onCreateOptionsMenu(menu);
                             j();
                            mGoogleApiClient.disconnect();
                            mGoogleApiClient=null;
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        });



    }

    private void handleSignInResult(GoogleSignInResult result) {

        alertDialogbuilder=new AlertDialog.Builder(this);
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            new DBtemp(this).insertEmail(acct.getEmail());
            new InsertEmail(acct.getEmail()).execute();
            if (new DBtemp(this).getEmail().length()!=0)
                textView.setText(new DBtemp(this).getEmail());
            Toast.makeText(getApplicationContext(),"Signed in as: "+acct.getEmail(),Toast.LENGTH_SHORT);

            goTo();
        }
        else {
            // Signed out, show unauthenticated UI.
            alertDialogbuilder=new AlertDialog.Builder(this);
            builderWithoutButton(null,"Sign In Failed :(",alertDialogbuilder);

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN ) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
           handleSignInResult(result);

        }
    }


    private  void goTo()
    {

        if (tag.equals("myjokes"))
        {


               changeSharedPref("my_jokes","yes",MainActivity.this);
            j();
            onCreateOptionsMenu(menu);
        }

        else if (tag.equals("feedback"))
        {
            startActivity(new Intent(this,Contact.class));
        }else if (tag.equals("A")){

                j();
            onCreateOptionsMenu(menu);

        }     else    if (tag == "like") {
            jokeDetails.new likeStatus("1", "0", new DBtemp(this).getEmail(), jokeDetails.joke_id).execute();
            onCreateOptionsMenu(menu);
        } else if (tag == "dislike") {
            jokeDetails.new likeStatus("0", "1", new DBtemp(this).getEmail(), jokeDetails.joke_id).execute();
            onCreateOptionsMenu(menu);
        } else if (tag == "block") {
            jokeDetails.new block(jokeDetails.joke_id).execute();
            onCreateOptionsMenu(menu);
        }

    }
    void toastMessage(){

        if (toast!=null){
            toast.cancel();
        }
        toast = Toast.makeText(getApplicationContext(), "Connect to internet", Toast.LENGTH_LONG);;

        toast.show();
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
          if (id == R.id.language) {
            if (isNetworkAvailable())
            new Language().execute();
            else {
             toastMessage();
                   }
        } else if (id == R.id.joke_category) {
              if (isNetworkAvailable())

                  new Category().execute();
              else
                  toastMessage();
        }  else if (id == R.id.share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,Add.share_msg);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } else if (id == R.id.rate) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=oj.com.ownjokes")));

        } else if (id == R.id.feedback) {
            if ( new DBtemp(MainActivity.this).getEmail().length()==0){
                alertDialogbuilder=new AlertDialog.Builder(MainActivity.this);
                tag="feedback";
                SignInBuilder();
                signIn();

            }else {
                Intent intent = new Intent(MainActivity.this, Contact.class);
                startActivity(intent);
                finish();
            }
        }  else if (id == R.id.more_apps) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=VR+apps")));

        }  else if (id == R.id.about) {
            alertDialogbuilder=new AlertDialog.Builder(MainActivity.this);

            PackageInfo pInfo = null;
            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                int verCode = pInfo.versionCode;
                e.printStackTrace();
            }

            String version = pInfo.versionName;
            builderWithoutButton("About",getApplicationName(this)+" v"+version+"\n" +
                    "\n" +
                            "Developer\n" +
                            "\"VR apps\"",alertDialogbuilder);




    }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    UpdateMainClass updateMainClass;
    @Override
    public void updateItemList(int position) {
        arrayList.remove(position);
        simpleItemRecyclerViewAdapter.notifyItemRemoved(position);

      //  jokeAllDetails(arrayList.size());
    }
    @Override
    public void updateListBackground(int position, boolean isChecked,String array[]) {

            arrayList.get(position).no_of_likes=array[0];
            arrayList.get(position).no_of_dislikes=array[1];

            simpleItemRecyclerViewAdapter.notifyItemChanged(position);

    }
    @Override
    public void itemInserted() {
        memorySaving();

        simpleItemRecyclerViewAdapter.notifyItemRangeInserted(start,memorySavingList.size());
     //   simpleItemRecyclerViewAdapter.notifyDataSetChanged();

      //  simpleItemRecyclerViewAdapter.notifyDataSetChanged();

        simpleItemRecyclerViewAdapter.setLoaded();
        loadProgressBar.setVisibility(View.GONE);
        jokeAllDetails(jokes_count);

        //TODO edit inserted
    }


    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>  {

        private final List<Item> mValues;

        private Context c;

        private OnLoadMoreListener onLoadMoreListener;
        public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.onLoadMoreListener = mOnLoadMoreListener;
        }

        public SimpleItemRecyclerViewAdapter(Context context,List<Item> objects) {

            c = context;
            mValues = objects;
            if(context instanceof UpdateMainClass){
                updateMainClass = (UpdateMainClass)context;
            }

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                       totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!isLoading && totalItemCount <= (lastVisibleItem + 5)) {

                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }
            });
        }

        //creating the interface

        //declaring and initializing the interface variable in Adapter class

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(jokes, parent, false);
            c=parent.getContext();
            return new ViewHolder(view);
        }



        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            //// TODO: access variables from another calss by handler
            final int i=position;
           Item o=mValues.get(i);
            String uri = "drawable/"+o.like_img,uri1 = "drawable/"+o.dislike_img;;
            int imageResource = c.getResources().getIdentifier(uri, null, c.getPackageName());

            int imageResource1 = MainActivity.this.getResources().getIdentifier(uri1, null, c.getPackageName());

            Drawable image = MainActivity.this.getResources().getDrawable(imageResource);
            Drawable image1 = c.getResources().getDrawable(imageResource1);

            holder.imageView.setImageDrawable(image);
            holder.imageView1.setImageDrawable(image1);
            if (holder.textView != null)
                holder.textView.setText(mValues.get(i).joke);
            if (holder.textView1 != null)
                holder.textView1.setText(mValues.get(i).no_of_likes);
            if (holder.textView2 != null)
                holder.textView2.setText(mValues.get(i).no_of_dislikes);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (editText.getText().toString().length()!=0){
                        changeSharedPref("search","yes",MainActivity.this);
                        changeSharedPref("search_data",editText.getText().toString(),MainActivity.this);
                    }
                    index=i;


                    alertDialogbuilder = new AlertDialog.Builder(MainActivity.this, R.style.CustomDialog);
                    LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View dialogView= inflater.inflate(R.layout.activity_joke_details, null);
                    alertDialogbuilder.setView(dialogView);

                    alertDialog = alertDialogbuilder.create();

         /*           Dialog dlg = alertDialog;

                    Window window = dlg.getWindow();
                    WindowManager.LayoutParams wlp = window.getAttributes();

                    wlp.gravity = Gravity.TOP;
                    wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                    window.setAttributes(wlp);*/

                    alertDialog.show();



                     jokeDetails=new JokeDetails(mValues.get(i).no_of_likes,mValues.get(i).no_of_dislikes,hashMap.get(mValues.get(i).joke_id),mValues.get(i).joke,mValues.get(i).joke_id,dialogView,holder.mView.getContext(),alertDialog,holder,updateMainClass);

                       jokeDetails.onCreate();


                }
            });
      /*      holder.row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    updateMainClass.updateItemList(holder.getAdapterPosition());
                }
            });*/
        }
        @Override
        public int getItemCount() {
            return mValues.size();
        }

        protected class ViewHolder extends RecyclerView.ViewHolder {
            private final View mView;
            private TextView textView,textView1,textView2;
            private ImageView imageView,imageView1;
            private Item mItem;
            private RelativeLayout row;

            private ViewHolder(View v) {
                super(v);
                mView = v;
                textView=(TextView) v.findViewById(R.id.Joke);
                textView1=(TextView)v.findViewById(R.id.TextViewLikes);
                textView2=(TextView) v.findViewById(R.id.TextViewDislikes);

                imageView=(ImageView) v.findViewById(R.id.likes);
                imageView1=(ImageView) v.findViewById(R.id.dislikes);
                row=(RelativeLayout) v.findViewById(R.id.row);
            }


        }
        public void setLoaded() {
            isLoading = false;
        }
    }


    protected void jokeAllDetails(int size){
            String s=getSharedPref("category",MainActivity.this);
            if (s.length()==0){
                s="All";
            }
            textView1.setText("Language:  "+getSharedPref("language",MainActivity.this)+"\nCategory:    "+s+"\n"+"Total jokes: "+size);

        }




    private void setupRecyclerView(@NonNull final RecyclerView recyclerView, final ArrayList<Item> arrayList) {

        if (getSharedPref("language",this).length()==0)
        {
            new Language().execute();
        }
        else {

            simpleItemRecyclerViewAdapter=new SimpleItemRecyclerViewAdapter(this,memorySavingList);
                recyclerView.setAdapter(simpleItemRecyclerViewAdapter);

            isLoading=false;
            simpleItemRecyclerViewAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                   /* if(arrayList.size()<jokes_count) {
                      loadProgressBar.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                //Generating more data
                                int index = memorySavingList.size();
                                int end = index + 30;
                                for (int i = index; i < end; i++) {
                                    if (i<arrayList.size())
                                    memorySavingList.add(arrayList.get(i));
                                }
                                simpleItemRecyclerViewAdapter.notifyDataSetChanged();
                                simpleItemRecyclerViewAdapter.setLoaded();
                                loadProgressBar.setVisibility(View.GONE);

                            }
                        }, 5000);*/
                    if(arrayList.size()<jokes_count) {

                        j();


                    }

                }
            });
            jokeAllDetails(jokes_count);
               }

    }
    protected void adult(Context context){

        alertDialogbuilder = new AlertDialog.Builder(MainActivity.this);
         LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);        
        View dialogView= inflater.inflate(R.layout.custom_builder_layout, null);
        alertDialogbuilder.setView(dialogView);
        TextView mainTitle_textView=(TextView) dialogView.findViewById(R.id.mainTitle);

        TextView textView=(TextView) dialogView.findViewById(R.id.textView6);
        TextView textView1=(TextView) dialogView.findViewById(R.id.textView7);
        TextView textView2=(TextView) dialogView.findViewById(R.id.textView9);
        TextView textView3=(TextView) dialogView.findViewById(R.id.textView10);

        mainTitle_textView.setText("Sign in");
        textView1.setText("You have selected adult category.You agree that your age is above 18+ for reading Adult jokes.!!");

        textView.setVisibility(View.GONE);
        textView2.setVisibility(View.GONE);
        textView3.setVisibility(View.GONE);


        alertDialogbuilder.setCancelable(false).setPositiveButton("Try again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ConnectToInternet();
            }
        });

        alertDialogbuilder.setCancelable(true).setPositiveButton("Continue",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tag="myjokes";
                SignInBuilder();
                signIn();


            }
        });

        alertDialogbuilder.setCancelable(true).setPositiveButton("Yes I am 18+", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tag="A";
                 SignInBuilder();
                signIn();

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }) ;
        alertDialog = alertDialogbuilder.create();
        alertDialog.show();
    }

    private void category(){
        alertDialogbuilder=new android.support.v7.app.AlertDialog.Builder(MainActivity.this);

        alertDialogbuilder.setTitle("Select joke category");
        alertDialogbuilder.setCancelable(false);
        alertDialogbuilder.setItems(category.toArray(new String[category.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                catindex=which;
                    if (category.get(which).equals("All")){
                        changeSharedPref("category","", MainActivity.this);

                    }
                    else if (new DBtemp(MainActivity.this).getEmail().length()==0 && category.get(which).contains("Adult")){
                        changeSharedPref("category", category.get(catindex), MainActivity.this);

                    }
                    else {
                        changeSharedPref("category", category.get(which), MainActivity.this);

                    }
j();
            }
        });
        alertDialogbuilder.create().show();
    }
    private void j(){
      /*  StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String message="";
        for (int i=0;i<stackTraceElements.length;i++){
            message=message+"\n"+stackTraceElements[i].getMethodName();
        }
        Log.i("search joke",message);
        alertDialogbuilder=new AlertDialog.Builder(this);
        builderWithoutButton("method",message,alertDialogbuilder);*/

        start=arrayList.size() ;
            end = 30;

        new UpdateChecker().execute();
        if (getSharedPref("device",this).equals("0"))//false
        new GetDeviceDetails().execute();
        if ( editText.isShown() && editText.getText().toString().trim().length()!=0) {

             new SearchJoke().execute();
        }else {
            if (getSharedPref("my_jokes", MainActivity.this).contains("yes"))
                new myJokes(new DBtemp(MainActivity.this).getEmail()).execute();
            else if (new DBtemp(this).getEmail().length() == 0 && getSharedPref("category", this).contains("Adult")) {
                adult(this);
            } else {

                new LoadAllJokees().execute();
                changeSharedPref("extra", "0", MainActivity.this);
            }
        }
    }
    private void lang()
    {
        alertDialogbuilder=new android.support.v7.app.AlertDialog.Builder(MainActivity.this);

        alertDialogbuilder.setTitle("Select language");
        alertDialogbuilder.setCancelable(false);
        alertDialogbuilder.setItems(lang.toArray(new String[lang.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                changeSharedPref("language",lang.get(which),MainActivity.this);
                if (!isNetworkAvailable())
                    toastMessage();
                else
                    new Category().execute();

            }
        });
        alertDialogbuilder.create().show();
    }

    private void AddJoke()
    {
        Intent intent=new Intent(MainActivity.this,Add.class);

        startActivity(intent);
        finish();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected String getSharedPref(String key, Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_pref, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }
    protected void changeSharedPref(String key,String value,Context context)
    {
        new SharedTask(key,value,shared_pref,context).execute();
    }

    void rateApp(final Context context)
    {

        if (getSharedPref("Rate",context).contains("no")) {

            String rate[] = { "Ok,Let's rate it","Later", "Already done"};

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Love this app!! Let's rate it :)");
            builder.setCancelable(false);
            builder.setItems(rate, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case 0:
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=oj.com.ownjokes")));

                            break;
                        case 1:
                            dialogInterface.cancel();
                            break;
                        case 2:

                            changeSharedPref("Rate","yes",context);

                            break;
                    }
                }
            });

            builder.create().show();
        }
    }

    private String  getDeviceDetails(){

        String OS_version= System.getProperty("os.version"),API_Level=
        android.os.Build.VERSION.SDK  ,  Device=
        android.os.Build.DEVICE  ,Model=
        android.os.Build.MODEL     ,Product=
        android.os.Build.PRODUCT   ;

        String address = getMacAddr();
          return  "{\"OS_version\":\""+OS_version+"\",\"API_Level\":\""+API_Level+"\",\"Device\":\""+Device+"\",\"Model\":\""+Model+"\",\"Product\":\""+Product+"\",\"Mac_addr\":\"" + address + "\"}";
    }

        public static String getMacAddr() {
            try {
                List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface nif : all) {
                    if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }

                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(Integer.toHexString(b & 0xFF) + ":");
                    }

                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString();
                }
            } catch (Exception ex) {
            }
            return "02:00:00:00:00:00";
        }

    public class GetDeviceDetails extends AsyncTask<Void,Void,String> {

        private final String store_details = "https://webandroid.000webhostapp.com/device_info.php";




        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            changeSharedPref("device","1",MainActivity.this);//device data is stored
        }

        @Override
        protected String doInBackground(Void... v) {
            HashMap<String,String> params = new HashMap<>();


            params.put("details",getDeviceDetails());
            RequestHandler rh = new RequestHandler();
            String res = rh.sendPostRequest(store_details, params);
            return res;
        }
    }


    public class LoadAllJokees extends AsyncTask<Void,Void,String> {

        private final String get_jokes = www+"/get_all_jokes.php";

        private final String language =getSharedPref("language",MainActivity.this),category=getSharedPref("category",MainActivity.this);




        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            if (initial ) {

                 getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


                visibleProgrogressBar("Loading jokes..", "Please Wait...");
                reset();
                 }
                     else  {
                loadProgressBar.setVisibility(View.VISIBLE);
            }

           }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            swipeRefreshLayout.setRefreshing(false);

            invisibleProgrogressBar();
             loadProgressBar.setVisibility(View.GONE);
            showJSON(String.valueOf(s));
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            //  Toast.makeText(getApplicationContext(),String .valueOf(memorySavingList.size())+" "+String .valueOf(arrayList.size()),Toast.LENGTH_SHORT).show();

        }

        @Override
        protected String doInBackground(Void... v) {
            HashMap<String,String> params = new HashMap<>();


            params.put("language",language);
            params.put("category",category);
            params.put("start",String.valueOf(start));
            params.put("end",String.valueOf(end));

            if (new DBtemp(MainActivity.this).getEmail().trim().length()!=0){
                params.put("email_id",new DBtemp(MainActivity.this).getEmail().trim());
            }
            RequestHandler rh = new RequestHandler();
            String res = rh.sendPostRequest(get_jokes, params);
            return res;
        }
    }




    private void showJSON(String json){
    String name_joke_id_column="name_joke_id",joke_id_column="joke_id",joke_column="joke",like_column="total_likes",dislike_column="total_dislikes",user_name="user_name";
        String joke_id="",joke ="",likes="", dislikes ="",name="";
        try {
            jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(joke_column);
            JSONArray result1 = jsonObject.getJSONArray(like_column);
            JSONArray result2 = jsonObject.getJSONArray(dislike_column);
            JSONArray result3 = jsonObject.getJSONArray(joke_id_column);
            JSONArray result4 = jsonObject.getJSONArray(user_name);
            JSONArray result5 = jsonObject.getJSONArray(name_joke_id_column);

            jokes_count= Integer.parseInt(jsonObject.getJSONArray("total_jokes").getString(0));
     //       Toast.makeText(getApplicationContext(),String.valueOf(jokes_count),Toast.LENGTH_SHORT).show();
            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                JSONObject jo1 = result1.getJSONObject(i);
                JSONObject jo2 = result2.getJSONObject(i);
                JSONObject jo3 = result3.getJSONObject(i);


                 joke = jo.getString("get_jokes");
                likes=jo1.getString("get_total_likes");
                dislikes=jo2.getString("get_total_dislikes");
                joke_id = jo3.getString("get_joke_id");

                arrayList.add(new Item(joke_id,joke, "like", likes, "dislike", dislikes));

            }

            for(int i = 0; i<result4.length(); i++){
                JSONObject jo4 = result4.getJSONObject(i);
                    JSONObject jo5 = result5.getJSONObject(i);
                name=jo4.getString("get_name");
                 joke_id=jo5.getString("get_name_joke_id");
                    hashMap.put(joke_id, name);
                }

                 } catch (JSONException e) {
            e.printStackTrace();
        }
        if (initial) {

            if (arrayList.size() > 0 && arrayList.size() <= 30) {
                initial=false;
                changeSharedPref("extra", "0", this);
                memorySaving();
                setupRecyclerView(recyclerView, memorySavingList);

            }
            else if (arrayList.size() == 0 && editText.isShown() && editText.getText().toString().trim().length()!=0 ) {
                if (refreshCount < 4) {
                    j();

                    refreshCount++;

                } else {
                    refreshCount = 0;


                     Toast.makeText(getApplicationContext(),"No search result",Toast.LENGTH_SHORT).show();
reset();
                }
            }
            else if (arrayList.size() == 0 && getSharedPref("my_jokes", this).contains("no")) {
                if (refreshCount < 10) {
                    j();
                    refreshCount++;
                } else {
                    refreshCount = 0;
                    alertDialogbuilder = new AlertDialog.Builder(this);
                    builderWithoutButton("Network Problem", "Looks like network problem or server may be down,please try again in few minutes", alertDialogbuilder);
reset();
                }
            } else if (arrayList.size() == 0 && getSharedPref("my_jokes", this).contains("yes")) {
                if (refreshCount < 4) {
                    j();
                    refreshCount++;
                } else {
                    refreshCount = 0;
                    alertDialogbuilder = new AlertDialog.Builder(this);
                    builderWithoutButton("No jokes found", "No jokes found for email: " + new DBtemp(this).getEmail() + "\n try to refresh again or add your joke", alertDialogbuilder);
reset();

                }
            }
        }else {
            itemInserted();
        }
        //TODO updateinserted
    }

public void reset(){

    initial=true;
    arrayList.clear();
    memorySavingList.clear();
    recyclerView.removeAllViews();
    jokeAllDetails(jokes_count);
}

    public class InsertEmail extends AsyncTask<Void,Void,String> {

        private  final String insert_email = www+"/insert_email.php";
        private final String email;


        public InsertEmail(String email)
        {

            this.email=email;

        }

        @Override
        protected String doInBackground(Void... v) {
            HashMap<String,String> params = new HashMap<>();

            params.put("email_id",email);
            RequestHandler rh = new RequestHandler();

            String res = rh.sendPostRequest(insert_email, params);
            return res;
        }
    }

    public class myJokes extends AsyncTask<Void,Void,String> {

        private  final String get_jokes = www+"/my_jokes.php";
        private final String language =new MainActivity().getSharedPref("language",MainActivity.this);
        private String  email="",category=getSharedPref("category",MainActivity.this);;


        public myJokes(String email)
        {

            this.email=email;

        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            if (initial ) {

                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


                visibleProgrogressBar("Loading jokes of " + email, "Please Wait...");
                reset();
               }
            else {
                loadProgressBar.setVisibility(View.VISIBLE);
            }

             }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            swipeRefreshLayout.setRefreshing(false);

            invisibleProgrogressBar();
             loadProgressBar.setVisibility(View.GONE);

           showJSON(s);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }

        @Override
        protected String doInBackground(Void... v) {
            HashMap<String,String> params = new HashMap<>();

                params.put("email_id",email);//for my jokes
                params.put("language",language);
            params.put("start",String.valueOf(start));
            params.put("end",String.valueOf(end));

            params.put("category",category);
            RequestHandler rh = new RequestHandler();
            String res = rh.sendPostRequest(get_jokes, params);
            return res;
        }
    }

    public class Language extends AsyncTask<Void,Void,String> {

        private  final String category = www+"/get_language.php";

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            lang.clear();
            initial=true;
            arrayList.clear();
            memorySavingList.clear();
            visibleProgrogressBar("Loading Languages...","Please Wait...");


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            invisibleProgrogressBar();
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

        private  final String category = www+"/get_category.php",language=getSharedPref("language",MainActivity.this);

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            MainActivity.this.category.clear();
            MainActivity.this.category.add("All");
            initial=true;
            arrayList.clear();

            memorySavingList.clear();
            visibleProgrogressBar("Loading Jokes Category...","Please Wait...");


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

       invisibleProgrogressBar();
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
                if (input.equals("category")){
                    this.category.add(category);
                }
                else
                lang.add(category);
            }
            if (input.equals("category")) {
                if (this.category.size()<=1){
                    refreshCount++;
                    if (refreshCount>6) {
                        refreshCount=0;
                        builderWithoutButton("Something went wrong!!", "Unable to load languages, maybe network problem,please try again within few minutes", alertDialogbuilder);
                    }else
                    new Category().execute();
                }else
                category();
            }
            else {
                if (this.lang.size() == 0) {
                    refreshCount++;
                    if (refreshCount > 6) {
                        refreshCount = 0;
                        builderWithoutButton("Something went wrong!!", "Unable to load joke category, maybe network problem,please try again within few minutes", alertDialogbuilder);
                    } else
                        new Language().execute();

                } else

                    lang();
            }


        } catch (JSONException e) {
            e.printStackTrace();

        }

    }




/*
private void memorySavingMethod(final ArrayList<Item> arrayList ){

    final int start=memorySavingList.size();
    i=start;

    jokeAllDetails(arrayList.size());

        simpleItemRecyclerViewAdapter=new SimpleItemRecyclerViewAdapter(this,arrayList);
        simpleItemRecyclerViewAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (i<arrayList.size()) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            simpleItemRecyclerViewAdapter.notifyItemRemoved(memorySavingList.size());


                            Toast.makeText(getApplicationContext(), "loading moew jokes", Toast.LENGTH_SHORT).show();


                            simpleItemRecyclerViewAdapter.notifyDataSetChanged();
                            simpleItemRecyclerViewAdapter.setLoaded();
                        }
                    }, 1000);
                }



            }
        });

        recyclerView.setAdapter(simpleItemRecyclerViewAdapter);



}*/


    /*@Override
    protected void onResume() {
        super.onResume();
        recyclerView.scrollToPosition(Integer.parseInt(getSharedPref("position",this)));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                setupRecyclerView(recyclerView,arrayList);
                recyclerView.scrollBy(0, - Integer.parseInt(getSharedPref("position",MainActivity.this)));
            }
        }, 500);

    }

    @Override
    protected void onPause() {
        super.onPause();


        View firstChild = recyclerView.getChildAt(0);

        int firstVisiblePosition = recyclerView.getChildAdapterPosition(firstChild);

        changeSharedPref("position",String.valueOf(firstVisiblePosition),MainActivity.this);


    }*/


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

    private void visibleProgrogressBar(String title,String message){

        progressBar.setVisibility(View.VISIBLE);
         belowProgressBar.setVisibility(View.VISIBLE);
        belowProgressBar.setText(title+"\n"+message);
    }
    private void invisibleProgrogressBar(){

        progressBar.setVisibility(View.GONE);

        belowProgressBar.setVisibility(View.GONE);

    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    }
