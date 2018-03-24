package oj.com.ownjokes;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by pc on 3/9/2017.
 */

public class SharedTask
{
    String key, value,shared_pref;
    SharedPreferences sharedPreferences;
    Context context;
    public SharedTask(String key, String value, String shared_pref, Context context)
    {
        this.key=key;
        this.value=value;
        this.shared_pref=shared_pref;
        this.context=context;
    }

    protected String execute() {
        if (value!=null)
            changeSharedPref(key,value,context);
        return null;
    }

    private void changeSharedPref(String key, String value, Context context)
    {
        sharedPreferences = context.getSharedPreferences(shared_pref, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, "");
        editor.putString(key, value);
        editor.commit();
    }


}