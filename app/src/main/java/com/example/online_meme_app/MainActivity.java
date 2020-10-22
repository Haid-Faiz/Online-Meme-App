package com.example.online_meme_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.service.chooser.ChooserTarget;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Queue;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button nextButton, shareButton;
    ProgressBar progressBar;
    String api, currentImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);
        nextButton = findViewById(R.id.next_button);
        shareButton = findViewById(R.id.share_button);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        api = "https://meme-api.herokuapp.com/gimme";
        loadMeme();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMeme();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND).setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Hey, i get this meme from reddit..  " + currentImageUrl);
                // Creating chooser
                Intent shareIntent = Intent.createChooser(intent, "Share this using");
                startActivity(shareIntent);
            }
        });
    }

    private void loadMeme() {

        progressBar.setVisibility(View.VISIBLE);

//        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // Request a JSON Response from provided url
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, api, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            currentImageUrl = response.getString("url");
                            //
                            Glide.with(MainActivity.this).load(currentImageUrl).addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    progressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    progressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            }).into(imageView);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handling errors
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // add a request into request queue
//        requestQueue.add(jsonObjectRequest);
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}