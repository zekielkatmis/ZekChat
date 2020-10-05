package com.example.zekchat.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zekchat.Models.Users;
import com.example.zekchat.R;
import com.example.zekchat.Utils.ChangeFragment;
import com.example.zekchat.Utils.RandomName;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainUserProfileFragment extends Fragment {


    FirebaseAuth auth;
    FirebaseUser user;

    FirebaseDatabase database;
    DatabaseReference reference;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    String imageUrl;
    EditText input_userName, input_education, input_birthday, input_about;
    CircleImageView profile_image;
    Button profile_updateButton;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main_user_profile, container, false);
        define();
        getInformation();
        return view;
    }

    public void define() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Users").child(auth.getUid());

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        input_userName = view.findViewById(R.id.input_userName);
        input_education = view.findViewById(R.id.input_education);
        input_birthday = view.findViewById(R.id.input_birthday);
        input_about = view.findViewById(R.id.input_about);

        profile_image = view.findViewById(R.id.profile_image);
        profile_updateButton = view.findViewById(R.id.profile_updateButton);

        profile_updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void openGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 5);
    }

    //override onActivityResult
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 5 && resultCode == Activity.RESULT_OK) {
            Uri filepath = data.getData();

            StorageReference ref = storageReference.child("userProfilePhotos").child(RandomName.getSaltString() + ".jpg");

            ref.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                    firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final  String downloadUrl = uri.toString();

                            String username = input_userName.getText().toString();
                            String education = input_education.getText().toString();
                            String birthday = input_birthday.getText().toString();
                            String about = input_about.getText().toString();

                            reference = database.getReference().child("Users").child(auth.getUid());

                            Map map = new HashMap();

                            map.put("profileUsername", username);
                            map.put("profileEducation", education);
                            map.put("profileBirthday", birthday);
                            map.put("profileAbout", about);
                            map.put("profilePhoto", downloadUrl);

                            reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        ChangeFragment fragment = new ChangeFragment(getContext());
                                        fragment.change(new MainUserProfileFragment());
                                        Toast.makeText(getContext(), "Informations updated...", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "Failed informations not updated...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    public void getInformation() {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users users = snapshot.getValue(Users.class);

                input_userName.setText(users.getProfileUsername());
                input_education.setText(users.getProfileEducation());
                input_birthday.setText(users.getProfileBirthday());
                input_about.setText(users.getProfileAbout());
                imageUrl = users.getProfilePhoto();

                if (!users.getProfilePhoto().equals("null")) {
                    Picasso.get().load(users.getProfilePhoto()).into(profile_image);
                }

                /*String name = snapshot.child("profileUsername").getValue().toString();
                String education = snapshot.child("profileEducation").getValue().toString();
                ... We can get information also this way..*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateUser() {

        String username = input_userName.getText().toString();
        String education = input_education.getText().toString();
        String birthday = input_birthday.getText().toString();
        String about = input_about.getText().toString();

        reference = database.getReference().child("Users").child(auth.getUid());

        Map map = new HashMap();

        map.put("profileUsername", username);
        map.put("profileEducation", education);
        map.put("profileBirthday", birthday);
        map.put("profileAbout", about);

        if (imageUrl.equals("")) {
            map.put("profilePhoto", "null");
        } else {
            map.put("profilePhoto", imageUrl);
        }

        reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    ChangeFragment fragment = new ChangeFragment(getContext());
                    fragment.change(new MainUserProfileFragment());
                    Toast.makeText(getContext(), "Informations updated...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed informations not updated...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}