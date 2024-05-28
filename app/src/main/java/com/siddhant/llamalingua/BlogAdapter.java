package com.siddhant.llamalingua;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;

import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {

    private List<String[]> blogList;
    private LayoutInflater inflater;
    private ViewPager2 viewPager2;
    private Context context;

    public BlogAdapter(Context context, List<String[]> blogList, ViewPager2 viewPager2) {
        this.blogList = blogList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_blog, parent, false);
        return new BlogViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        String[] blog = blogList.get(position);
        holder.bind(blog);
        if(position == blogList.size() - 2) {
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    class BlogViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView img;

        BlogViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.blogTitle);
            img = itemView.findViewById(R.id.img);

            itemView.setOnClickListener(v -> {
                Intent i = new Intent(context, Blog.class);
                i.putExtra("data", blogList.get(getAdapterPosition()));
                context.startActivity(i);
            });
        }

        public void bind(String[] blog) {
            titleTextView.setText(blog[0]);
            Glide.with(itemView.getContext())
                    .load(blog[2])
                    .into(img);
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            blogList.addAll(blogList);
        }
    };
}
