package com.example.haram.myapplication;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.haram.myapplication.CaldroidCustomFragment;
import com.roomorama.caldroid.CaldroidFragment;


public class MainActivity extends AppCompatActivity {

    ViewPager viewpager;
    FragmentPageAdapter ft;
    public static GoogleAccountCredential mCredential;
    public static TextView mOutputText;
//    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };
    public static  ArrayList<String> summary;

    private CaldroidFragment caldroidFragment;

    private IntentFilter receiveFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //        mProgress = new ProgressDialog(this);
//        mProgress.setMessage("Calling Google Calendar API ...");

        setContentView(R.layout.activity_main);

//        viewpager = (ViewPager) findViewById(R.id.pager);
//        ft = new FragmentPageAdapter(getSupportFragmentManager());

//        viewpager.setAdapter(ft);

        mOutputText = (TextView) findViewById(R.id.glance);

        // Initialize credentials and service object.
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

        caldroidFragment = new CaldroidCustomFragment();

        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
        args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);

        caldroidFragment.setArguments(args);


        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        final ArrayList<String> calendars = new ArrayList<String>();
        RequestCalendarList calen = new RequestCalendarList(mCredential);
        calen.setListener(new RequestCalendarList.RequestCalendarListListener() {
            @Override
            public void onPreExecuteConcluded() {

            }

            @Override
            public void onPostExecuteConcluded(List<CalendarListEntry> result) {
                for (CalendarListEntry entry : result) {
                    calendars.add(entry.getSummary());
                }
            }
        });
        calen.execute();

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, calendars);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);



    }

    /**
     * Called whenever this activity is pushed to the foreground, such as after
     * a call to onCreate().
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            refreshResults();
        } else {
            mOutputText.setText("Google Play Services required: " +
                    "after installing, close and relaunch this app.");

        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    isGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        mCredential.setSelectedAccountName(accountName);
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    mOutputText.setText("Account unspecified.");
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode != RESULT_OK) {
                    chooseAccount();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Attempt to get a set of data from the Google Calendar API to display. If the
     * email address isn't known yet, then call chooseAccount() method so the
     * user can pick an account.
     */
    private void refreshResults() {
        if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            if (isDeviceOnline()) {
                getData();
            } else {
                mOutputText.setText("No network connection available.");
            }
        }
    }

    private void getData() {
        RequestTask makeRequestTask = new RequestTask(mCredential);
        makeRequestTask.setListener(new RequestTask.RequestTaskListener() {
            @Override
            public void onPreExecuteConcluded() {

            }

            @Override
            public void onPostExecuteConcluded(List<Event> result) {
                parseOutput(result);
            }
        });
        makeRequestTask.execute();
    }

    /**
     * Starts an activity in Google Play Services so the user can pick an
     * account.
     */
    private void chooseAccount() {
        startActivityForResult(
                mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date. Will
     * launch an error dialog for the user to update Google Play Services if
     * possible.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS ) {
            return false;
        }
        return true;
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                connectionStatusCode,
                MainActivity.this,
                REQUEST_GOOGLE_PLAY_SERVICES);
//        dialog.show();
    }
    static final String KEY_DATE = "date";
    static final String KEY_DAY = "day";
    static final String KEY_START = "start";
    static final String KEY_END = "end";
    static final String KEY_SUMMARY = "summary";
    static final String KEY_COLOR = "color";


    private void parseOutput(List<Event> output) {

        summary = new ArrayList<String>();
        ArrayList<EventDateTime> start = new ArrayList<EventDateTime>();
        ArrayList<EventDateTime> end = new ArrayList<EventDateTime>();
        ArrayList<HashMap<String, String>> datalist = new ArrayList<HashMap<String, String>>();

        String init,fin,last;
        Date dates = null;

        for (Event e : output) {
            summary.add(e.getSummary());
            start.add(e.getStart());
            end.add(e.getEnd());

            DateTime startTime = (e.getStart()).getDateTime();
            DateTime endTime = (e.getEnd()).getDateTime();

            //String color = e.getColorId();
            String Ymd = startTime.toString();
            String initTime = endTime.toString();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                dates = dateFormat.parse(Ymd);
            } catch (ParseException j) {
                j.printStackTrace();
            }

            init = dates.toString();
            fin = init.substring(0, 4);
            last = init.substring(8,11);

            String hour = Ymd.substring(11,16);
            String hour2 = initTime.substring(11,16);

            HashMap<String, String> map = new HashMap<String, String>();
            map.put(KEY_SUMMARY, e.getSummary());
            map.put(KEY_START, "Start: " + hour);
            map.put(KEY_END, "End: " + hour2);
            map.put(KEY_DATE,fin);//word
            map.put(KEY_DAY,last);//number
            //map.put(KEY_COLOR,color);//number
            datalist.add(map);

        }
        ListView listview = (ListView) findViewById(R.id.list);

        ListAdapter adapter = new ListAdapter(this, datalist);
        listview.setAdapter(adapter);

        displayOutput(summary, start, end);
        setCustomResourceForDates(start);

    }

    private void displayOutput(ArrayList<String> summary, ArrayList<EventDateTime> start, ArrayList<EventDateTime> end) {
        int numEvents = summary.size();
        String outputText = "";
        for (int i = 0; i < numEvents; i++){
          if (start.get(i).getDateTime() != null) {
              DateTime startTime = start.get(i).getDateTime();
              DateTime endTime = end.get(i).getDateTime();

              outputText += summary.get(i) + " \n"
                      + "Starting at: " + startTime.toString() + " \n"
                      + "Ending at: " + endTime.toString() + " \n"
                      + "\n";
          } else {
              DateTime startDate = start.get(i).getDate();
              outputText += summary.get(i) + " \n"
                      + "Starting at: " + startDate.toString() + " \n";
          }
        }

        mOutputText.setText(outputText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.refresh_button, menu);
        inflater.inflate(R.menu.main_menu, menu);
        inflater.inflate(R.menu.add_event, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.settings_id:
                Intent i = new Intent(MainActivity.this, setting_display.class);
                startActivity(i);
                break;
            case R.id.add_id:
                Calendar cal = Calendar.getInstance();
                Intent j = new Intent(Intent.ACTION_EDIT);
                j.setType("vnd.android.cursor.item/event");
                j.putExtra("BeginTime", cal.getTimeInMillis());
                j.putExtra("allDay",true);
                j.putExtra("rrule","FREQ=YEARLY");
                j.putExtra("EndTime",cal.getTimeInMillis()+60*60*1000);
                j.putExtra("title","Test Event");
                j.putExtra("description"," ");

                startActivity(j);
                break;
            case R.id.refresh_id:
                getData();
                caldroidFragment.refreshView();
                break;
            default:
                break;
        }
        return true;
    }


    private void setCustomResourceForDates(ArrayList<EventDateTime> events) {
        Calendar cal = Calendar.getInstance();

        for (EventDateTime date : events){
//            int event = Integer.parseInt(result.substring(date + 3, date + 5));
            Date inputDate = null;
            String Ymd;
            if (date.get("dateTime") != null) {
                Ymd = date.get("dateTime").toString().substring(0, 10);
            } else {
                Ymd = date.get("date").toString();
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                inputDate = dateFormat.parse(Ymd);
            } catch (ParseException e) {
                e.printStackTrace();
            }




//            cal.set(Calendar.DAY_OF_YEAR, date);
//            cal.add(Calendar.DATE, 0);
//            Date coloredDate = cal.getTime();
//            blueDate = cal.getTime();
            if (caldroidFragment != null) {
                ColorDrawable cell = new ColorDrawable(getResources().getColor(R.color.blue));
                caldroidFragment.setBackgroundDrawableForDate(cell, inputDate);
                caldroidFragment.setTextColorForDate(R.color.white, inputDate);
                caldroidFragment.refreshView();
            }
        }
    }
}
