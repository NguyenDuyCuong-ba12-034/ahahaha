package vn.edu.usth.new_flick_image;



import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import vn.edu.usth.new_flick_image.R;
import vn.edu.usth.new_flick_image.Model.User;


public class SignUpActivity extends AppCompatActivity {
    private Button register;
    private Button login;
    private EditText txtFirstName;
    private EditText txtLastName;
    private EditText txtBirthDay;
    private EditText txtEmail;
    private EditText txtPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtBirthDay = findViewById(R.id.txtBirthDay);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);


        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String pass = charSequence.toString();
                TextView valid1 = findViewById(R.id.validPass1);
                TextView valid2 = findViewById(R.id.validPass2);

                if (pass.length() < 12) valid1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.uncheck, 0,0,0);
                else valid1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_check_circle_24, 0,0,0);

                if(pass.contains(" ")) valid2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.uncheck, 0, 0, 0);
                else valid2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_check_circle_24, 0, 0, 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this , LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
                progressDialog.setMessage("Loading registed email");
                progressDialog.setCancelable(false);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference usersRef = database.getReference("users");

                String firstName = txtFirstName.getText().toString();
                String lastName = txtLastName.getText().toString();
                String birthday = txtBirthDay.getText().toString();
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();
                User user = new User(firstName, lastName, birthday, email, password, "");

                TextView errorMessageFirstName = findViewById(R.id.errorMessageFirstName);
                TextView errorMessageLastName = findViewById(R.id.errorMessageLastName);
                TextView errorMessageBirthday = findViewById(R.id.errorMessageBirthday);
                TextView errorMessageEmail = findViewById(R.id.errorMessageEmail);
                TextView errorMessagePassword = findViewById(R.id.errorMessagePass);

                if(TextUtils.isEmpty(firstName))
                {
                    errorMessageFirstName.setVisibility(View.VISIBLE);
                    errorMessageFirstName.setTextColor(Color.RED);
                    return;
                }
                else errorMessageFirstName.setVisibility(View.GONE);

                if(TextUtils.isEmpty(lastName))
                {
                    errorMessageLastName.setVisibility(View.VISIBLE);
                    errorMessageLastName.setTextColor(Color.RED);
                    return;
                }
                else errorMessageLastName.setVisibility(View.GONE);

                if(TextUtils.isEmpty(birthday))
                {
                    errorMessageBirthday.setVisibility(View.VISIBLE);
                    errorMessageBirthday.setTextColor(Color.RED);
                    return;
                }
                else errorMessageBirthday.setVisibility(View.GONE);

                if(TextUtils.isEmpty(email))
                {
                    errorMessageEmail.setVisibility(View.VISIBLE);
                    errorMessageEmail.setTextColor(Color.RED);
                    return;
                }
                else errorMessageEmail.setVisibility(View.GONE);

                if(TextUtils.isEmpty(password))
                {
                    errorMessagePassword.setVisibility(View.VISIBLE);
                    errorMessagePassword.setTextColor(Color.RED);
                    return;
                }
                else errorMessagePassword.setVisibility(View.GONE);

                progressDialog.show();
                usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        progressDialog.dismiss();
                        if (dataSnapshot.exists()) {
                            Toast.makeText(getApplicationContext(), "Account has already exist!", Toast.LENGTH_SHORT).show();
                        } else {
                            usersRef.child(email.replace(".", ",")).setValue(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(), "Sign up successfully", Toast.LENGTH_SHORT).show();

                                            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                            builder.setTitle("Confirmation!");
                                            builder.setMessage("Returning to Log in?");

                                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    startActivity(new Intent(SignUpActivity.this , LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                }
                                            });

                                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            });

                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Sign up fail", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Invalid email", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}

