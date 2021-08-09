package com.tavaresrit.petshop.fragments.post_details;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tavaresrit.petshop.adapter.CommentAdapter;
import com.tavaresrit.petshop.databinding.FragmentPostDetailsBinding;
import com.tavaresrit.petshop.models.Comment;
import com.tavaresrit.petshop.models.Post;
import com.tavaresrit.petshop.models.User;

import static com.tavaresrit.petshop.util.Firebase.getUid;


public class PostDetailsFragment extends Fragment {

    private FragmentPostDetailsBinding binding;
    private static final String TAG = "PostDetailFragment";

    //recebe passagem de parametros
    public static final String EXTRA_POST_KEY = "post_key";
    private String mPostKey;

    //Database referencias
    private DatabaseReference mPostReference; //todo: database POST
    private DatabaseReference mCommentsReference; //todo: database Comment

    private ValueEventListener mPostListener;

    //Adapter Comment
    private CommentAdapter mCommentAdapter;



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        getActivity().setTitle("Comentar");

        binding = FragmentPostDetailsBinding.inflate(inflater, container, false);
        binding.buttonPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obter a chave de postagem dos argumentos
        mPostKey = requireArguments().getString(EXTRA_POST_KEY);
        if (mPostKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Recebe lista "posts" Database para poder ler
        mPostReference = FirebaseDatabase.getInstance().getReference().child("posts").child(mPostKey);
        mCommentsReference = FirebaseDatabase.getInstance().getReference().child("post-comments").child(mPostKey);



        binding.recyclerComment.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    @Override
    public void onStart() {
        super.onStart();

        listPost();



    }

    @Override
    public void onStop() {
        super.onStop();

        // Remover ouvinte de evento de pós-valor
        if (mPostListener != null) {
            mPostReference.removeEventListener(mPostListener);
        }

        // Limpe o ouvinte de comentários (Direto da Comment Adapter)
        mCommentAdapter.cleanupListener();

    }


    private void postComment(){

        final String uid = getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Obtenha informações do usuário
                        User user = dataSnapshot.getValue(User.class);
                        String authorName = user.name;

                        // Criar novo objeto de comentário
                        String commentText = binding.editComment.getText().toString();
                        Comment comment = new Comment(uid, authorName, commentText);

                        // Push (Empurre) o comentário, ele aparecerá na lista
                        mCommentsReference.push().setValue(comment);

                        // Limpandoo o campo
                        binding.editComment.setText(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



    }


    private void listPost(){
        // Adicionar ouvinte de evento de valor à postagem
        //todo: INSERÇÃO INSTANTÂNEA DE DADOS
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Post post = dataSnapshot.getValue(Post.class);
                binding.postAuthor.setText(post.author);
                binding.postTitle.setText(post.title);
                binding.postBody.setText(post.body);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(getContext(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        mPostReference.addValueEventListener(postListener);

        //
        //Mantenha uma cópia do post listener para que possamos removê-lo quando o aplicativo parar
        mPostListener = postListener;

        //Recebe informações do Adapter Comment
        mCommentAdapter = new CommentAdapter(getContext(), mCommentsReference); // Recebendo Adaptador com Referencia de Comment
        binding.recyclerComment.setAdapter(mCommentAdapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}