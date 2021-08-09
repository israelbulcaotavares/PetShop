package com.tavaresrit.petshop.fragments.sigin;

import android.content.Intent;
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
import com.tavaresrit.petshop.MainActivity;
import com.tavaresrit.petshop.R;
import com.tavaresrit.petshop.databinding.FragmentSignInBinding;

public class SignInFragment extends Fragment {
    private static final String TAG = "SignInFragment";
    private FragmentSignInBinding binding;
     private FirebaseAuth mAuth;
     private FirebaseUser firebaseUser;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSignInBinding.inflate(inflater, container, false);
        binding.buttonSigninUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });



        mAuth = FirebaseAuth.getInstance();

        return binding.getRoot();

    }

    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess();
        }
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SignInFragment.this)
                        .navigate(R.id.action_SignInFragment_to_SignUpFragment);
            }
        });
    }

    private void signIn() {
        Log.d(TAG, "signIn");
        if (!validateForm()) {
            return;
        }

         String email = binding.editTextEmail.getText().toString();
        String password = binding.editTextPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());


                        if (task.isSuccessful()) {
                            onAuthSuccess();
                        } else {
                            Toast.makeText(getContext(), "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess() {

        startActivity(new Intent(getContext(), MainActivity.class));

    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(binding.editTextEmail.getText().toString())) {
            binding.editTextEmail.setError("Requerido");
            result = false;
        } else {
            binding.editTextEmail.setError(null);
        }

        if (TextUtils.isEmpty(binding.editTextPassword.getText().toString())) {
            binding.editTextPassword.setError("Requerido");
            result = false;
        } else {
            binding.editTextPassword.setError(null);
        }

        return result;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}