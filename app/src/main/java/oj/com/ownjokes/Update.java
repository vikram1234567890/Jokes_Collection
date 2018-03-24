package oj.com.ownjokes;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

/**
 * Created by pc on 8/20/2017.
 */

public class Update {
    Activity activity;
    String version,version_details;

    public Update(Activity activity, String version, String version_details) {
        this.activity = activity;
        this.version = version;
        this.version_details = version_details;
    }

    void message(){
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(activity);
        aBuilder.setTitle("New version available v"+version).setMessage(version_details).setCancelable(false).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.finish();
            }
        }).setPositiveButton("Update now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+activity.getPackageName())));

            }
        }).create().show();
    }
}
