package com.example.wootlabwhatsapp.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.wootlabwhatsapp.R;
import com.example.wootlabwhatsapp.adapter.ChatsListAdapter;
import com.example.wootlabwhatsapp.databinding.FragmentChatsBinding;
import com.example.wootlabwhatsapp.model.Chats;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ChatsFragment extends Fragment {

    private static final String TAG = "ChatsFragment";

    public ChatsFragment() {
//        Required empty public constructor
    }

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private FirebaseFirestore firestore;

    private Handler handler = new Handler();

    private FragmentChatsBinding binding;
    private List<Chats> list;
    private ChatsListAdapter adapter;

    private ArrayList<String> allUserID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chats, container, false);

        list = new ArrayList<>();
        allUserID = new ArrayList<>();

        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatsListAdapter(list, getContext());
        binding.chatRecyclerView.setAdapter(adapter);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();

        if (firebaseUser != null) {
            getChatList();
        }

        return binding.getRoot();
    }

    private void getChatList() {
        binding.progressCircular.setVisibility(View.VISIBLE);
        list.clear();
        allUserID.clear();


        reference.child("ChatList").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String userID = Objects.requireNonNull(snapshot.child("chatid").getValue()).toString();
                    Log.d(TAG, "onDataChange: userId" + userID);

                    binding.progressCircular.setVisibility(View.GONE);
                    allUserID.add(userID);
                }
                getUserData();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e(TAG, "onCancelled: "+ error.getMessage());

            }
        });
    }

    private void getUserData() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for(String userID : allUserID){
                    firestore.collection("Users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            try{
                                Chats chat = new Chats(
                                        documentSnapshot.getString("userID"),
                                        documentSnapshot.getString("userName"),
                                        "This is a description",
                                        "",
                                        documentSnapshot.getString("imageProfile")
                                );

                                list.add(chat);
                            }catch (Exception e){
                                Log.e(TAG, "onSuccess: "+ e.getMessage());
                            }

                            if (adapter != null){
                                adapter.notifyItemInserted(0);
                                adapter.notifyDataSetChanged();
                                Log.e(TAG, "onSuccess: "+ adapter.getItemCount());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Log.e(TAG, "onFailure: "+ e.getMessage());
                        }
                    });
                }
            }
        });
    }


//
//    public static List<Chats> getChatsList() {
//
//        List<Chats> list = new ArrayList<>();
//        Chats chat = new Chats("01", "Damilola S", "Hi", "Yesterday", "");
//        list.add(chat);
//
//        chat = new Chats("02", "Ishaq Zakari", "Hello", "now", "");
//        list.add(chat);
//
//        chat = new Chats("03", "Yekeen Fatai", "Greetings", "Tuesday", "");
//        list.add(chat);
//
//        return list;
//    }
}