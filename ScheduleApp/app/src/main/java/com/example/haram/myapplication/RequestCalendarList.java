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

public class RequestCalendarList extends AsyncTask<Void, Void, List<CalendarListEntry>> {
    private com.google.api.services.calendar.Calendar mService = null;
    private Exception mLastError = null;

    public interface RequestCalendarListListener {
        void onPreExecuteConcluded();
        void onPostExecuteConcluded(List<CalendarListEntry> result);
    }

    private RequestCalendarListListener listener;

    final public void setListener(RequestCalendarListListener rlistener) {
        listener = rlistener;
    }

    public RequestCalendarList(GoogleAccountCredential credential) {
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
    final protected List<CalendarListEntry> doInBackground(Void... params) {
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
    private List<CalendarListEntry> getDataFromApi() throws IOException {
        CalendarList cal = mService.calendarList().list().execute();
        return cal.getItems();
    }


    @Override
    protected void onPreExecute(){
        if (listener != null)
            listener.onPreExecuteConcluded();
    }


    @Override
    protected void onPostExecute(List<CalendarListEntry> output) {
        if (listener != null)
            listener.onPostExecuteConcluded(output);
    }


//    @Override
//    protected abstract void onCancelled();

}
