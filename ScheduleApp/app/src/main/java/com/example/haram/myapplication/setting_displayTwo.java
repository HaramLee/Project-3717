package com.example.haram.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by BaikDesktop on 2/9/2016.
 */
public class setting_displayTwo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.setting_page2);

        Intent activityThatCalled = getIntent();

        String previousActivity =
                activityThatCalled.getExtras().getString("getCallingActivity");

        TextView callingActivityMessage = (TextView)
                findViewById(R.id.calling_activity_info_text_view);

        callingActivityMessage.append(" " + previousActivity);
    }

    public void onSendUsersName(View view) {

        EditText usersNameET = (EditText)
                findViewById(R.id.users_name_edit_text);

        String usersName = String.valueOf(usersNameET.getText());

        Intent goingBack = new Intent();

        goingBack.putExtra("UsersName", usersName);

        setResult(RESULT_OK, goingBack);

        finish();

    }
}
