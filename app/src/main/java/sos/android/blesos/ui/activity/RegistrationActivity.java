package sos.android.blesos.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import sos.android.blesos.R;
import sos.android.blesos.sendmsg.SendMessage;
import sos.android.blesos.util.SharedPreferenceUtil;
import sos.android.blesos.util.Utility;
import sos.android.blesos.util.Utils;

public class RegistrationActivity extends AppCompatActivity {

    private String userName;
    private String passwordTxt;
    private String conformPasswordTxt;

    private EditText userNumber;
    private EditText password;
    private EditText conformPassword;
    private TextView forgetPassword;
    private Button registration;
    private Button signin;

    private String storedUserName;
    private String storedUserPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storedUserName = SharedPreferenceUtil.getInstance().getStringValue(SharedPreferenceUtil.USER_NAME, "");
        storedUserPassword = SharedPreferenceUtil.getInstance().getStringValue(SharedPreferenceUtil.USER_PASSWORD, "");

        if (storedUserName == null || storedUserName.isEmpty()) {
            setContentView(R.layout.activity_registration);
            userNumber = (EditText) findViewById(R.id.activityRegistration_editTextName);
            password = (EditText) findViewById(R.id.activityRegistration_editTextPassword);
            conformPassword = (EditText) findViewById(R.id.activityRegistration_editTextConformPassword);
            registration = (Button) findViewById(R.id.activityRegistration_buttonRegister);

            registration.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setRegistration();
                }
            });

        } else {
            setContentView(R.layout.activity_signin);
            userNumber = (EditText) findViewById(R.id.activitySignin_editTextName);
            password = (EditText) findViewById(R.id.activitySignin_editTextPassword);
            signin = (Button) findViewById(R.id.activitySignin_buttonSignin);
            forgetPassword = (TextView) findViewById(R.id.activitySignin_forgetpassword);

            signin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSignin();
                }
            });
            forgetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setForgetPassword();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setRegistration() {
        userName = userNumber.getText().toString();
        passwordTxt = password.getText().toString();
        conformPasswordTxt = conformPassword.getText().toString();
        if (registrationValidation()) {
            SharedPreferenceUtil.getInstance().setStringValue(SharedPreferenceUtil.USER_NAME, userName);
            SharedPreferenceUtil.getInstance().setStringValue(SharedPreferenceUtil.USER_PASSWORD, passwordTxt);

            startActivity(new Intent(RegistrationActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        } else {
            Utils.showAlertDialog(getBaseContext(), "One of the field is empty are the fields are mismatched");
        }
    }

    private boolean registrationValidation() {
        if (userName.isEmpty() || passwordTxt.isEmpty() || conformPasswordTxt.isEmpty()) {
            return false;
        } else if (passwordTxt.equals(conformPasswordTxt)) {
            return true;
        } else {
            return false;
        }
    }

    private void setSignin() {
        userName = userNumber.getText().toString();
        passwordTxt = password.getText().toString();
        if (userName.isEmpty() || passwordTxt.isEmpty()) {
            showAlertDialog("One of the field is empty");
        } else if (!storedUserName.equals(userName)) {
            showAlertDialog("Register Mobile Number is mismatched");
        } else if (!storedUserPassword.equals(passwordTxt)) {
            showAlertDialog("Please check your Password is mismatched");
        } else {
            startActivity(new Intent(RegistrationActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }
    }

    private void setForgetPassword() {
        ArrayList<String> receipientList = new ArrayList<>();
        receipientList.add(storedUserName);
        new SendMessage(receipientList, "Your Password for SoS is " + storedUserPassword);
        Utility.showToast("your Password has been sent through sms");
    }

    private void showAlertDialog(final String textToShow) {
        new AlertDialog.Builder(RegistrationActivity.this)
                .setTitle(R.string.app_name)
                .setMessage(textToShow)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
