package com.Farm.e_agric.Adapter;

import android.app.Activity;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.Farm.e_agric.Model.Solutions;
import com.Farm.e_agric.Model.Users;
import com.Farm.e_agric.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {

    private Activity context;
    private List<Users> usersList;
    private List<Solutions> solutionsList;
    //Delete here
    private TextToSpeech tts;
    //Till here

    //delete here
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    //till here

    public CommentsAdapter(Activity context, List<Solutions> solutionsList, List<Users> usersList) {
        this.context = context;
        this.solutionsList = solutionsList;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_comment, parent, false);
        return new CommentsViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {
        Solutions solutions = solutionsList.get(position);
        holder.setmComment(solutions.getSolution());

        Users users = usersList.get(position);
        holder.setmUserName(users.getName());
        holder.setCircleImageView(users.getImage());

        //Delete here

        String bsc = users.getBsc();
        String msc = users.getMsc();
        if (bsc != null && msc != null) {
            if (bsc.equals("bsc") && msc.equals("msc")) {
                holder.rate.setImageResource(R.drawable.fivestarrr);
            } else {
                holder.rate.setImageResource(R.drawable.onestar);
            }
        } else if (bsc != null && msc == null) {
            if (bsc.equals("bsc")) {
                holder.rate.setImageResource(R.drawable.threestarrrr);
            } else {
                holder.rate.setImageResource(R.drawable.onestar);
            }
        } else if (bsc == null && msc != null) {
            if (msc.equals("msc")) {
                holder.rate.setImageResource(R.drawable.fivestarrr);
            }
            else {
                holder.rate.setImageResource(R.drawable.onestar);
            }
        } else {
            holder.rate.setImageResource(R.drawable.twostarr);
        }

        //till here

        //Delete here
        tts = new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(context.getApplicationContext(), "This language is not supported.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context.getApplicationContext(), "TTS Initialization failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.ibSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = holder.mComment.getText().toString().trim();
                int result = tts.speak(input, TextToSpeech.QUEUE_FLUSH, null);
                if (result == TextToSpeech.ERROR) {
                    Toast.makeText(context.getApplicationContext(),
                            "Error in converting Text to Speech", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //Till here
    }

    @Override
    public int getItemCount() {
        return solutionsList.size();
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder {
        TextView mComment, mUserName;
        CircleImageView circleImageView;
        //delete here
        ImageButton ibSpeak;
        //till here

        //delete here
        ImageView rate;
        //delete
        View mView;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            rate = mView.findViewById(R.id.imageView4);
            ibSpeak = mView.findViewById(R.id.ibSpeak);
        }

        public void setmComment(String comment) {
            mComment = mView.findViewById(R.id.comment_tv);
            mComment.setText(comment);
        }

        public void setmUserName(String userName) {
            mUserName = mView.findViewById(R.id.comment_user);
            mUserName.setText(userName);
        }

        public void setCircleImageView(String profilePic) {
            circleImageView = mView.findViewById(R.id.comment_Profile_pic);
            Glide.with(context).load(profilePic).into(circleImageView);
        }
    }
}