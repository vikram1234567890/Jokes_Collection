package oj.com.ownjokes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class JokeDetails extends MainActivity  implements View.OnClickListener{
TextView textView1,textView2,textView3;
    ImageButton imageButton,imageButton1,imageButton2;

    private int rateCount=0,likes,dislikes;
    String name,joke,joke_id;
    private JSONObject jsonObject;

    private MenuItem progressbar;
    private String www="";
    private int success=1;

    private int RC_SIGN_IN=0;
    private AlertDialog.Builder alertDialogbuilder;


    private  Context context;
    private View view;

    private AlertDialog alertDialog;
    SimpleItemRecyclerViewAdapter.ViewHolder holder;
    private int holderPositoin;

    UpdateMainClass updateMainClass;
 

    public JokeDetails(String likes, String dislikes, String name, String joke, String joke_id, View view, Context context, AlertDialog alertDialog, SimpleItemRecyclerViewAdapter.ViewHolder holder, UpdateMainClass updateMainClass) {
      try {

        this.likes = Integer.parseInt(likes);
        this.dislikes = Integer.parseInt(dislikes);
        this.name = name;
        this.joke = joke;
        this.joke_id = joke_id;

        this.view = view;
          this.context = context;
        this.alertDialog=alertDialog;
          this.holder=holder;
          this.updateMainClass=updateMainClass;

      }catch (Exception e){

      }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageButton:
                tag = "like";
                if (new DBtemp(context).getEmail().length() == 0) {
                    signMessage();
                } else
                    new likeStatus("1", "0", new DBtemp(context).getEmail(), joke_id).execute();
                break;
            case R.id.imageButton2:
                tag = "dislike";
                if (new DBtemp(context).getEmail().length() == 0) {
                 signMessage();
                } else
                    new likeStatus("0", "1", new DBtemp(context).getEmail(), joke_id).execute();
                break;
            case R.id.imageButton3:

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, textView1.getText().toString() + "\n\n\n" + Add.share_msg);
                sendIntent.setType("text/plain");
                new share(joke_id).execute();
                context.startActivity(sendIntent);

                break;
            case R.id.inappropriate:
                flagInapropriate();


                break;

        }
    }

    public void onCreateOptionsMenu(Toolbar toolbar) {
        // Inflate the menu items for use in the action bar
     toolbar.inflateMenu(R.menu.menu4);
     Menu menu=toolbar.getMenu();
        MenuItem progressbar=menu.getItem(0);
        this.progressbar=progressbar;

        if (new DBtemp(context).getEmail().length() == 0 && !getSharedPref("my_jokes", context).equalsIgnoreCase("yes"))//not signed in
        {
         //edit,delete,progressbar invisible
            MenuItem menuItem[]={menu.getItem(0),menu.getItem(1),menu.getItem(2)};
           for (int i=0;i<menuItem.length;i++){
               menuItem[i].setVisible(false);
           }

        } else if (new DBtemp(context).getEmail().length() != 0 && getSharedPref("my_jokes", context).equalsIgnoreCase("yes"))// signed in and my jokes
        {

            progressbar.setVisible(false);

            //edit,delete visible,flag inappropriate invisible
            MenuItem menuItem[]={menu.getItem(0),menu.getItem(4)};
            for (int i=0;i<menuItem.length;i++){
                menuItem[i].setVisible(false);
            }


        }else{
            MenuItem menuItem[]={menu.getItem(0),menu.getItem(1),menu.getItem(2)};
            for (int i=0;i<menuItem.length;i++){
                menuItem[i].setVisible(false);
            }
        }

      toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
          @Override
          public boolean onMenuItemClick(MenuItem item) {
              switch (item.getItemId()) {
                  case R.id.inappropriate:
                      flagInapropriate();
                      return true;
                  case R.id.edit:
                      Intent intent = new Intent(context, Add.class);
                      intent.putExtra("j", joke);
                      intent.putExtra("n", name);
                      intent.putExtra("j_id", joke_id);

                      context.startActivity(intent);
                      finish();
                      return true;
                  case R.id.delete:
                      AlertDialog.Builder aBuilder = new AlertDialog.Builder(context);
                      aBuilder.setCancelable(false).setMessage("Are you sure you want to delete this joke?").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialogInterface, int i) {
                              new delete(joke_id).execute();
                          }
                      }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialogInterface, int i) {
                              dialogInterface.dismiss();

                          }
                      }).create().show();

                      return true;
                  case R.id.close:

                      alertDialog.dismiss();
                      return true;
                  default:
                      return JokeDetails.super.onOptionsItemSelected(item);
              }
          }
      });


    }


    protected void onCreate() {


        holderPositoin = holder.getAdapterPosition();
        if (!getSharedPref("Rate", context).contains("yes")) {
            rateCount = Integer.parseInt(getSharedPref("rcount", context)) + 1;
            changeSharedPref("rcount", String.valueOf(rateCount), context);

            if (rateCount >= 10) {
                changeSharedPref("rcount", "0", context);
                rateApp(context);
            }
        }
        textView1 = (TextView) view.findViewById(R.id.textView4);
        textView1.setMovementMethod(new ScrollingMovementMethod());
        textView2 = (TextView) view.findViewById(R.id.textView3);
        textView3 = (TextView) view.findViewById(R.id.textView5);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar1);

        onCreateOptionsMenu(toolbar);

        for (int i = new Add().a.length - 1; i >= 0; i--) {
            www = www + new Add().a[i];
        }
        imageButton = (ImageButton) view.findViewById(R.id.imageButton);
        imageButton1 = (ImageButton) view.findViewById(R.id.imageButton2);
        imageButton2 = (ImageButton) view.findViewById(R.id.imageButton3);

        imageButton.setOnClickListener(JokeDetails.this);
        imageButton1.setOnClickListener(JokeDetails.this);
        imageButton2.setOnClickListener(JokeDetails.this);


        if (name != null && name.length() != 0)
            textView1.setText(joke + "\n\nUploaded by " + name);
        else
            textView1.setText(joke);

        textView2.setText(String.valueOf(likes));
        textView3.setText(String.valueOf(dislikes));


        bannerAd();

    }


    private void signMessage() {
        alertDialogbuilder = new AlertDialog.Builder(context);
        alertDialogbuilder.setTitle("Sign in").setMessage("Hey you need to sign in to like/dislike joke.Press continue to select your email id").setCancelable(false).setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SignInBuilder();
                signIn();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).create().show();
    }
    protected void SignInBuilder()
    {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(context)

                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }
    protected void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        ((Activity)context).startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    private void flagInapropriate() {


        final AlertDialog.Builder aBuilder = new AlertDialog.Builder(context);
        aBuilder.setTitle("Flag Inapropriate?").setCancelable(false).setMessage("If you think this joke is inappropriate then proceed,you will not be able to change it again.").setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (new DBtemp(context).getEmail().length() == 0) {
                    signMessage();
                } else
                    new block(joke_id).execute();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();


    }







    private void checkSuccess(String s, String message) {
        try {
            jsonObject = new JSONObject(s);

            success = Integer.parseInt(jsonObject.getString("success"));
            if (success == 0) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                updateMainClass.updateItemList(holderPositoin);

                //  changeSharedPref("extra","1",context);
                alertDialog.dismiss();
            } else {
                Toast.makeText
                        (context, "Failed,try again", Toast.LENGTH_LONG).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    private void change() {
        try {
            textView2.setText(String.valueOf(likes));
            textView3.setText(String.valueOf(dislikes));

            updateMainClass.updateListBackground(holderPositoin, false, new String[]{textView2.getText().toString(), textView3.getText().toString()});
        } catch (Exception e) {

        }
    }






    protected void bannerAd() {
        AdView mAdView = (AdView) view.findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }


    public class share extends AsyncTask<Void, Void, String> {

        private String robot = "";
        String joke_id;
        private final String check_joke = www + "/total_shares.php";
        private String email_id = new DBtemp(context).getEmail();

        public share(String joke_id) {
            for (int i = new Add().r.length - 1; i >= 0; i--) {
                robot = robot + new Add().r[i];
            }
            this.joke_id = joke_id;
        }

        @Override
        protected String doInBackground(Void... v) {
            HashMap<String, String> params = new HashMap<>();

            params.put("joke_id", joke_id);
            if (email_id.length() != 0)
                params.put("email_id", email_id);
            else {
                email_id = robot;
                params.put("email_id", email_id);
            }
            RequestHandler rh = new RequestHandler();
            String res = rh.sendPostRequest(check_joke, params);

            return null;
        }
    }


    public class likeStatus extends AsyncTask<Void, Void, String> {
        String like;
        String dislike;
        String email, joke_id;
        private final String check_joke = www + "/like_dislike_joke.php";

        public likeStatus(String like, String dislike, String email, String joke_id) {
            this.like = like;
            this.dislike = dislike;
            this.email = email;
            this.joke_id = joke_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressbar.setVisible(true);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressbar.setVisible(false);
         showJSON(s);
        }

        @Override
        protected String doInBackground(Void... v) {
            HashMap<String, String> params = new HashMap<>();
            params.put("likes", like);
            params.put("dislikes", dislike);
            params.put("email_id", email);
            params.put("joke_id", joke_id);
            RequestHandler rh = new RequestHandler();
            String res = rh.sendPostRequest(check_joke, params);
            return res;
        }
    }


    private void showJSON(String json) {

        String likes_column = "likes", dislike_column = "dislikes";


        try {
            jsonObject = new JSONObject(json);

            likes = jsonObject.getInt(likes_column);
            dislikes = jsonObject.getInt(dislike_column);

           change();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    public class delete extends AsyncTask<Void, Void, String> {

        String joke_id;
        private final String check_joke = www + "/delete_joke.php";

        public delete(String joke_id) {

            this.joke_id = joke_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          progressbar.setVisible(true);   }


        @Override
        protected String doInBackground(Void... v) {
            HashMap<String, String> params = new HashMap<>();

            params.put("joke_id", joke_id);

            RequestHandler rh = new RequestHandler();
            String res = rh.sendPostRequest(check_joke, params);

            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
progressbar.setVisible(false);
           checkSuccess(s, "Deleted");
        }
    }


    public class block extends AsyncTask<Void, Void, String> {

        String joke_id;
        private final String check_joke = www + "/block.php";

        public block(String joke_id) {

            this.joke_id = joke_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           progressbar.setVisible(true);
        }


        @Override
        protected String doInBackground(Void... v) {
            HashMap<String, String> params = new HashMap<>();

            params.put("joke_id", joke_id);
            params.put("email_id", new DBtemp(context).getEmail());
            RequestHandler rh = new RequestHandler();
            String res = rh.sendPostRequest(check_joke, params);

            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressbar.setVisible(false);
            try {
                jsonObject = new JSONObject(s);

                if (jsonObject.getString("done").equals("0") && jsonObject.getString("success").equals("1")) {
                    Toast.makeText(context, "Request sent", Toast.LENGTH_LONG).show();

                } else {
                checkSuccess(s, "Requested");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
