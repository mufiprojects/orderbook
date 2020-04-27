package com.example.android.saffronfromzr;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class signInActivity  extends AppCompatActivity {
    private static final int RC_SIGN_IN=234;

    TextInputEditText designerNameText;
    LinearLayout googleSignInLayout;
    String designerName;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    SignInButton googleSignInButton;

    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference users;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mAuth=FirebaseAuth.getInstance();
        users=database.getReference("users");
        designerNameText=findViewById(R.id.designerNameText);
        googleSignInLayout=findViewById(R.id.googleSignInLayout);
        googleSignInButton=findViewById(R.id.googleSignInButton);
        inVisibileGoogleSignInLayout();
        designerNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!getDesignerName().isEmpty())
                {
                    visibileGoogleSignInLayout();
                }
                else {
                    inVisibileGoogleSignInLayout();
                }

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!getDesignerName().isEmpty())
                {
                    visibileGoogleSignInLayout();
                }
                else
                {
                    inVisibileGoogleSignInLayout();
                }
            }
        });
        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient= GoogleSignIn.getClient(this,googleSignInOptions);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                Toast.makeText(this, "try block executed", Toast.LENGTH_SHORT).show();
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            }
            catch (ApiException e)
            {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
private void firebaseAuthWithGoogle(GoogleSignInAccount account)
{

    Log.d("firebaseSignIn","firebaseAuthWithGoogle:"+account.getId());
    AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
    mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {


                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(signInActivity.this, "User Signed In",
                                Toast.LENGTH_SHORT).show();
                        String currentUser=mAuth.getCurrentUser().getUid();
                        users.child(currentUser).child("name").setValue(getDesignerName());
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));


                    } else {

                        Toast.makeText(signInActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();

                    }

                }
            });

}


    private void signIn() {
        Toast.makeText(this, "SingIn()", Toast.LENGTH_SHORT).show();
        Intent signInIntent=mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser()!=null)
        {
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }
    }

    private void visibileGoogleSignInLayout() {
        googleSignInLayout.setVisibility(View.VISIBLE);
    }private void inVisibileGoogleSignInLayout() {
        googleSignInLayout.setVisibility(View.INVISIBLE);
    }

    public String getDesignerName() {

            return designerName = designerNameText.getText().toString().trim();

    }
}
