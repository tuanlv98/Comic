package com.application.tuanlv.comicapp.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.tuanlv.comicapp.MainActivity;
import com.application.tuanlv.comicapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout rellay1, rellay2;
    private ImageView imgLogo;
    private TextInputLayout edtEmail, edtPasswd;
    private Button btnLogIn;
    private TextView tvforgotPass, tvSignUp;

    //Firebase
    private FirebaseAuth mAuth;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Animation animation = AnimationUtils.loadAnimation(StartActivity.this,R.anim.fade_in);
            imgLogo.setAnimation(animation);
            rellay1.setVisibility(View.VISIBLE);
            rellay2.setVisibility(View.VISIBLE);
            rellay1.setAnimation(AnimationUtils.loadAnimation(StartActivity.this,R.anim.fade_in));
            rellay2.setAnimation(AnimationUtils.loadAnimation(StartActivity.this,R.anim.fade_in));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initView();

        handler.postDelayed(runnable, 3000);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        imgLogo.setAnimation(animation);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //Event click
        btnLogIn.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
        tvforgotPass.setOnClickListener(this);
    }

    private void initView() {
        rellay1 = findViewById(R.id.rellay1);
        rellay2 = findViewById(R.id.rellay2);
        edtEmail = findViewById(R.id.input_log_email);
        edtPasswd = findViewById(R.id.input_log_pass);
        btnLogIn = findViewById(R.id.btn_log);
        imgLogo = findViewById(R.id.img_logo);
        tvSignUp = findViewById(R.id.txt_sign_up);
        tvforgotPass = findViewById(R.id.txt_forgotPass);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_log:
                String email = edtEmail.getEditText().getText().toString().trim();
                String passwd = edtPasswd.getEditText().getText().toString().trim();
                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(passwd)) {
                    logIn(email, passwd);
                } else {
                    Toast.makeText(StartActivity.this, "Please check your input", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.txt_sign_up:
                Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.txt_forgotPass:
                // status: not working
                String mEmail = edtEmail.getEditText().getText().toString().trim();
                if(mEmail.isEmpty()||mEmail==null) {
                    Snackbar.make(btnLogIn, "Please enter an email, we will help you create a new password!", Snackbar.LENGTH_LONG).show();
                }
                try {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(mEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Snackbar.make(btnLogIn, "Success! " + "Please check new password in your email!", Snackbar.LENGTH_LONG).show();
                                        //Toast.makeText(StartActivity.this, "Please check new password in your email!", Toast.LENGTH_LONG).show();
                                    } else {
                                        Snackbar.make(btnLogIn, "Invalid email!", Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });
                } catch (IllegalArgumentException e) {

                }
                break;
        }
    }

    private void logIn(String email, String passwd) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Starting login...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, passwd)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()) {
                            Intent intent = new Intent(StartActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Snackbar.make(btnLogIn, "Error " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
