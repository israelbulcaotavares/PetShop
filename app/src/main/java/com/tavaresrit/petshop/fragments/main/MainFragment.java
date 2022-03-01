package com.tavaresrit.petshop.fragments.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.tavaresrit.petshop.MainActivity;
import com.tavaresrit.petshop.R;
import com.tavaresrit.petshop.databinding.FragmentMainBinding;
import com.tavaresrit.petshop.fragments.post_details.PostDetailsFragment;
import com.tavaresrit.petshop.fragments.viewholder.PostViewHolder;
import com.tavaresrit.petshop.models.Post;
import com.tavaresrit.petshop.util.Firebase;

import java.util.Objects;


public class MainFragment extends Fragment  {

    private FragmentMainBinding binding;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Post, PostViewHolder> mAdapter;

    private LinearLayoutManager mManager;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentMainBinding.inflate(inflater, container, false);

         getActivity().setTitle("Feed");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        setAdapters();


        return binding.getRoot();

    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MainFragment.this).navigate(R.id.action_MainFragment_to_NewPostFragment);
            }
        });
    }

    private void setAdapters(){
        // Configurar Gerenciador de Layout, layout reverso
        binding.recyclerPostList.setHasFixedSize(true);
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        binding.recyclerPostList.setLayoutManager(mManager);

        // Configura FirebaseRecyclerAdapter com a consulta
        Query postsQuery = Firebase.getQueryPosts(mDatabase);

        //Adapter do Firebase
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(postsQuery, Post.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {

            @Override
            public PostViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new PostViewHolder(inflater.inflate(R.layout.item_post, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(PostViewHolder viewHolder, int position, final Post post) {


                final DatabaseReference postRef = getRef(position);
                final String postKey = postRef.getKey();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                        Bundle args = new Bundle();
                        args.putString(PostDetailsFragment.EXTRA_POST_KEY, postKey);
                        navController.navigate(R.id.action_MainFragment_to_PostDetailsFragment, args);

                        Toast.makeText(getContext(), "Resultado: " +postKey, Toast.LENGTH_SHORT).show();

                    }
                });


                // Conecta da PostViewHolder
                viewHolder.bindToPost(post);

            }
        };

        binding.recyclerPostList.setAdapter(mAdapter);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //todo:Inicia as informações do Database na tela - parte 1
    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }
    //todo:Inicia as informações do Database na tela - parte 2
    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }


}