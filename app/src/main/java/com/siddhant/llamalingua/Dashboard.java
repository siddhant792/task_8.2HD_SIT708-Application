package com.siddhant.llamalingua;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends AppCompatActivity {

    TextView tvDescription, tvWelcome;
    String[] user;
    private List<String[]> blogList;
    private RequestQueue requestQueue;
    ViewPager2 viewPager;
    BlogAdapter blogAdapter;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        handler = new Handler();
        viewPager = findViewById(R.id.viewPager);
        tvDescription = findViewById(R.id.tvDescription);
        tvWelcome = findViewById(R.id.tvWelcome);
        user = getIntent().getStringArrayExtra("user");
        tvWelcome.setText("Welcome " + user[1]);
        tvDescription.setText("Improve your command over " + user[4] + " with Llama Lingua!");

        findViewById(R.id.btnStartConversation).setOnClickListener(v -> {
            Intent i = new Intent(Dashboard.this, ChatWindow.class);
            i.putExtra("name", user[1]);
            i.putExtra("language", user[4]);
            i.putExtra("difficulty", user[5]);
            startActivity(i);
        });
        blogList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        fetchBlogs();
    }

    private void fetchBlogs() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "http://10.0.2.2:5000/blogs",
                null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject blogObject = response.getJSONObject(i);
                            String title = blogObject.getString("title");
                            String content = blogObject.getString("content");
                            String img = blogObject.getString("img");
                            blogList.add(new String[] {title, content, img});
                        }
                        blogAdapter = new BlogAdapter(Dashboard.this, blogList, viewPager);
                        viewPager.setAdapter(blogAdapter);
                        viewPager.setOffscreenPageLimit(3);
                        viewPager.setClipChildren(false);
                        viewPager.setClipToPadding(false);
                        viewPager.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

                        CompositePageTransformer transformer = new CompositePageTransformer();
                        transformer.addTransformer(new MarginPageTransformer(40));
                        transformer.addTransformer(new ViewPager2.PageTransformer() {
                            @Override
                            public void transformPage(@NonNull View page, float position) {
                                float r = 1 - Math.abs(position);
                                page.setScaleY(0.85f + r * 0.14f);
                            }
                        });
                        viewPager.setPageTransformer(transformer);
                        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                            @Override
                            public void onPageSelected(int position) {
                                super.onPageSelected(position);
                                handler.removeCallbacks(runnable);
                                handler.postDelayed(runnable, 2000);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(Dashboard.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(Dashboard.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    };
}