package com.radiuslabs.locus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import com.radiuslabs.locus.adapters.CommentsAdapter;
import com.radiuslabs.locus.models.Comment;
import com.radiuslabs.locus.models.User;
import com.radiuslabs.locus.restservices.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsDialog extends DialogFragment {

    private String storyId;
    private List<Comment> comments;
    private HashMap<String, User> users;

    private CommentsAdapter adapter;
    private EditText etComment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (comments == null)
            comments = new ArrayList<>();

        adapter = new CommentsAdapter(comments, getActivity());

        if (users == null)
            users = new HashMap<>();

        adapter.setUsers(users);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_comments, container);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.rvComments);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        etComment = (EditText) v.findViewById(R.id.etComment);

        v.findViewById(R.id.bPost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment();
            }
        });

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return v;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        if (adapter != null)
            adapter.setComments(comments);
    }

    public void setUsers(HashMap<String, User> users) {
        this.users = users;
        if (adapter != null) {
            adapter.setUsers(users);
        }
    }

    private void addComment() {
        final Comment comment = new Comment();
        comment.setText(etComment.getText().toString());
        RestClient.getInstance().getStoryService().addComment(storyId, comment)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            comment.setUserId(Util.user.get_id());
                            adapter.addComment(comment);
                            etComment.setText("");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                    }
                });

    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }
}
