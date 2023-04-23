package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    // Widgets
    EditText userET, passET, emailET;
    Button registerBtn;

    // Firebase
    FirebaseAuth auth;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initializing Widgets:
        userET = findViewById(R.id.userEditText);
        passET = findViewById(R.id.editText2);
        emailET = findViewById(R.id.emailEditText);
        registerBtn = findViewById(R.id.buttonRegister);


        // Firebase Auth
        auth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String username_text = userET.getText().toString();
                String email_text = emailET.getText().toString();
                String pass_text = passET.getText().toString();

                if (TextUtils.isEmpty(username_text) || TextUtils.isEmpty(email_text) || TextUtils.isEmpty(pass_text)) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng điền vào tất cả các trường", Toast.LENGTH_SHORT).show();
                } else {
                    RegisterNow(username_text, email_text, pass_text);
                }
            }
        });

    }
// tạo tài khoản lên firebase
    private void RegisterNow(final String username, String email, String password) {

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Hàm này sẽ trả về thông tin người dùng hiện tại đăng nhập vào hệ thống

                            FirebaseUser firebaseUser = auth.getCurrentUser();


                            //user ID của người dùng hiện tại, được lấy từ firebaseUser bằng hàm getUid().
                            String userid = firebaseUser.getUid();
                            // khởi tạo myRef de them vao firebase

                            myRef = FirebaseDatabase.getInstance().getReference("MyUsers")
                                    .child(userid);

                            // HashMaps

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username);
                            hashMap.put("imageURL", "default");
                            hashMap.put("status", "offline");


                            // up dữ liệu lên firebase theo hasmap
                            myRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
//                                       để thêm các flag vào Intent. Flag Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                        sẽ xóa tất cả các task liên quan đến Activity
//                                        hiện tại trước khi chuyển đến MainActivity. Flag Intent.FLAG_ACTIVITY_NEW_TASK
//                                        sẽ tạo một task mới cho MainActivity.
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                        startActivity(i);
                                        finish();
                                    }

                                }
                            });


                        } else {
                            Toast.makeText(RegisterActivity.this, "đăng kí không thành công", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }


}

