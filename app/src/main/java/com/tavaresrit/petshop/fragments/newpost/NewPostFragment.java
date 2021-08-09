package com.tavaresrit.petshop.fragments.newpost;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tavaresrit.petshop.R;
import com.tavaresrit.petshop.databinding.FragmentNewPostBinding;
 import com.tavaresrit.petshop.models.Post;
import com.tavaresrit.petshop.models.User;

import java.util.HashMap;
import java.util.Map;

import static com.tavaresrit.petshop.util.Firebase.getUid;

public class NewPostFragment extends Fragment {

    private FragmentNewPostBinding binding;
    private static final String TAG = "NewPostFragment";

    private DatabaseReference mDatabase;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentNewPostBinding.inflate(inflater, container, false);

        getActivity().setTitle("Postar publicação");


        binding.buttonSaveDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
                Toast.makeText(getContext(), "Postando....", Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(NewPostFragment.this)
                        .navigate(R.id.action_PostDetailsFragment_to_MainFragment);
            }
        });
    }

    private void submitPost() {
        final String title = binding.editTitle.getText().toString();
        final String body = binding.editBody.getText().toString();

        // Desative o botão para que não haja multi-posts
        Toast.makeText(getContext(), "Postando...", Toast.LENGTH_SHORT).show();

        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) {
                            // Se o usuário for NULO ou inexistente, NÂO escreve
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(getContext(),"Error: could not fetch user.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Escreve novo post
                            writeNewPost(userId, user.name, title, body);
                        }

                        NavHostFragment.findNavController(NewPostFragment.this)
                                .navigate(R.id.action_PostDetailsFragment_to_MainFragment);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());

                    }
                });
    }

    private void writeNewPost(String userId, String username, String title, String body) {
        // Criar uma nova postagem em /user-posts/$userid/$postid e em
        // /posts/$postid simultaneamente
        String key = mDatabase.child("posts").push().getKey(); // TODO: injetando chave key do usuario cadastrado
        Post post = new Post(userId, username, title, body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>(); //TODO: Obtendo a HASH MAP
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}