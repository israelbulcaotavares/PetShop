package com.tavaresrit.petshop.fragments.sigin;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tavaresrit.petshop.R;
import com.tavaresrit.petshop.models.User;
import com.tavaresrit.petshop.databinding.FragmentSignUpBinding;

public class SignUpFragment extends Fragment {
    private static final String TAG = "SignInFragment";
    private FragmentSignUpBinding binding;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String name;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSignUpBinding.inflate(inflater, container, false);

        binding.buttonSaveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        return binding.getRoot();

    }
    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(binding.editEmailSignup.getText().toString())) {
            binding.editEmailSignup.setError("Requerido");
            result = false;
        } else {
            binding.editEmailSignup.setError(null);
        }

        if (TextUtils.isEmpty(binding.editPasswordSignup.getText().toString())) {
            binding.editPasswordSignup.setError("Requerido");
            result = false;
        } else {
            binding.editPasswordSignup.setError(null);
        }

        return result;
    }
    private void signUp() {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }

         name = binding.editNomeSignup.getText().toString();
        String email = binding.editEmailSignup.getText().toString();
        String password = binding.editPasswordSignup.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
 
                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(getContext(), "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {

        // Write new user
        writeNewUser(user.getUid(), name, user.getEmail());

        // Go to MainFragment
        NavHostFragment.findNavController(this).navigate(R.id.action_SignUpFragment_to_SigInFragment);
    }

    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);
        mDatabase.child("users").child(userId).setValue(user);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SignUpFragment.this)
                        .navigate(R.id.action_SignUpFragment_to_SigInFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}