package com.example.haram.myapplication;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import com.roomorama.caldroid.CaldroidFragment;


public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    public static GoogleAccountCredential mCredential;

//    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };
    private final String PREF_CAL_ID = "calendarId";
    private final String PREF_CAL_ID_POS = "calendarIdPos";
    public static  ArrayList<String> summary;
    private ArrayList<Date> currentlyDisplayedDates = new ArrayList<Date>();

    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;

    private CaldroidFragment caldroidFragment;
    public static int lay = setting_display.layoutId;
    private IntentFilter receiveFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences(PREF_CAL_ID, Context.MODE_PRIVATE);

        // Initialize credentials and service object.
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

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


        getData();
        final ArrayList<String> calendars = new ArrayList<String>();

        RequestCalendarList requestCalendarList = new RequestCalendarList(mCredential);
        requestCalendarList.setListener(new RequestCalendarList.RequestCalendarListListener() {
            @Override
            public void onPreExecuteConcluded() {

            }

            @Override
            public void onPostExecuteConcluded(List<CalendarListEntry> result) {
                for (CalendarListEntry entry : result) {
                    calendars.add(entry.getSummary());
                }
                final List<CalendarListEntry> calendarList = result;
                Spinner spinner = (Spinner) findViewById(R.id.spinner);

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(MainActivity.this,
                        android.R.layout.simple_spinner_item, calendars);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.
                        simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerArrayAdapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        editor = sharedpreferences.edit();
                        editor.putString(PREF_CAL_ID, calendarList.get(pos).getId());
//                        editor.putInt(PREF_CAL_ID_POS, pos);
                        editor.commit();
                        getData();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }

                });

            }
        });
        requestCalendarList.execute();

    }

    /**
     * Called whenever this activity is pushed to the foreground, such as after
     * a call to onCreate().
     */
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (isGooglePlayServicesAvailable()) {
//            refreshResults();
//        } else {
////            mOutputText.setText("Google Play Services required: " +
////                    "after installing, close and relaunch this app.");
//        }
//    }

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
                    Log.d("**********", "results not ok");
                } else {
                    getData();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getData();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getData();
                }
                break;
        }
    }
    /*
    * Respond to requests for permissions at runtime for API 23 and above.
    * @param requestCode The request code passed in
    *     requestPermissions(android.app.Activity, String, int, String[])
    * @param permissions The requested permissions. Never null.
    * @param grantResults The grant results for the corresponding permissions
    *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
    */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Attempt to get a set of data from the Google Calendar API to display. If the
     * email address isn't known yet, then call chooseAccount() method so the
     * user can pick an account.
     */
    private void refreshResults() {
        if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
            Log.d("******", "in6");
        } else {
            if (isDeviceOnline()) {
                getData();
            } else {
//                mOutputText.setText("No network connection available.");
            }
        }
    }

    private void getData() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            Log.d("***", "Must be online!");
        } else {

            RequestTask makeRequestTask = new RequestTask(mCredential, getApplicationContext());
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
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getData();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
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
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
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
    static final String KEY_MONTH = "month";
    static final String KEY_YEAR = "year";


    private void parseOutput(List<Event> output) {

        summary = new ArrayList<String>();
        ArrayList<EventDateTime> start = new ArrayList<EventDateTime>();
        ArrayList<HashMap<String, String>> datalist = new ArrayList<HashMap<String, String>>();

        String init="",fin="",last="",startHour="",endHour="",month="",year="";
        Date dates = null;
        DateTime startTime = null, endTime = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (Event e : output) {

            if (e.getStatus().equals("cancelled")){
                continue;
            } else if (e.getStart().containsKey("date")) {
                start.add(e.getStart());
                summary.add(e.getSummary());
                startTime = (DateTime) e.getStart().get("date");
                String Ymd = startTime.toString();


                try {
                    dates = dateFormat.parse(Ymd);
                } catch (ParseException j) {
                    j.printStackTrace();
                }

                init = dates.toString();
                fin = init.substring(0, 4);
                last = init.substring(8,11);

                startHour = "00:00";
                endHour = "23:59";

            } else if (e.getStart().containsKey("dateTime")) {
                start.add(e.getStart());
                summary.add(e.getSummary());
                startTime = (DateTime) e.getStart().get("dateTime");
                endTime = (DateTime) e.getEnd().get("dateTime");

                String Ymd = startTime.toString();
                String initTime = endTime.toString();

                try {
                    dates = dateFormat.parse(Ymd);
                } catch (ParseException j) {
                    j.printStackTrace();
                }

                init = dates.toString();
                fin = init.substring(0, 4);
                last = init.substring(8,11);

                startHour = Ymd.substring(11,16);
                endHour = initTime.substring(11,16);

            }

            month = init.substring(4,8);
            year = startTime.toString().substring(0,4);


            HashMap<String, String> map = new HashMap<String, String>();
            map.put(KEY_SUMMARY, e.getSummary());
            map.put(KEY_START, "Start: " + startHour);
            map.put(KEY_END, "End:  " + endHour);
            map.put(KEY_DATE,fin);//word
            map.put(KEY_DAY,last);//number
            map.put(KEY_YEAR,year);
            map.put(KEY_MONTH,month);

            datalist.add(map);


        }
        ListView listview = (ListView) findViewById(R.id.list);

        ListAdapter adapter = new ListAdapter(this, datalist);
        listview.setAdapter(adapter);

        setCustomResourceForDates(start);

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
                break;
            default:
                break;
        }
        return true;
    }


    private void setCustomResourceForDates(ArrayList<EventDateTime> events) {

        //reset background to white, text to black
        ColorDrawable whiteCell = new ColorDrawable(getResources().getColor(R.color.white));
        for(Date date : currentlyDisplayedDates){
            caldroidFragment.setBackgroundDrawableForDate(whiteCell, date);
            caldroidFragment.setTextColorForDate(R.color.caldroid_black, date);
        }

        currentlyDisplayedDates.clear();
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
                currentlyDisplayedDates.add(inputDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (caldroidFragment != null) {
                ColorDrawable cell = new ColorDrawable(getResources().getColor(R.color.blue));
                caldroidFragment.setBackgroundDrawableForDate(cell, inputDate);
                caldroidFragment.setTextColorForDate(R.color.white, inputDate);
                caldroidFragment.refreshView();
            }
        }
    }
}
