package com.example.haram.myapplication;


import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestTask extends AsyncTask<Void, Void, List<Event>> {
    private com.google.api.services.calendar.Calendar mService = null;
    private Exception mLastError = null;

    public interface RequestTaskListener {
        void onPreExecuteConcluded();
        void onPostExecuteConcluded(List<Event> result);
    }

    private RequestTaskListener listener;

    final public void setListener(RequestTaskListener rlistener) {
        listener = rlistener;
    }

    public RequestTask(GoogleAccountCredential credential) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();
    }

    /**
     * Background task to call Google Calendar API.
     * @param params no parameters needed for this task.
     */
    @Override
    final protected List<Event> doInBackground(Void... params) {
        try {
            return getDataFromApi();
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    /**
     * Fetch a list of the next 10 events from the primary calendar.
     * @return List of Strings describing returned events.
     * @throws IOException
     */
    private List<Event> getDataFromApi() throws IOException {
        List<Events> eventStrings = new ArrayList<Events>();

        CalendarList cal = mService.calendarList().list().execute();

//        for (CalendarListEntry entry : cal.getItems()){
//            Log.d("***", entry.getSummary());
//            if (entry.getSummary().equals("Home Games")){
//                Log.d("*****", entry.getId());
//            }
//        }


        Events events = mService.events().list("4t6ltif9dt1tl65c7uj825miek@group.calendar.google.com")
                .execute();
        List<Event> items = events.getItems();

        return events.getItems();
    }


    @Override
    protected void onPreExecute(){
        if (listener != null)
            listener.onPreExecuteConcluded();
    }


    @Override
    protected void onPostExecute(List<Event> output) {
        Log.d("****", output.toString());
        if (listener != null)
            listener.onPostExecuteConcluded(output);
    }


//    @Override
//    protected abstract void onCancelled();

}
