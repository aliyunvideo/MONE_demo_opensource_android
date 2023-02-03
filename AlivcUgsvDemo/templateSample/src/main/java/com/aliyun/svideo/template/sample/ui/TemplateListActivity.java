package com.aliyun.svideo.template.sample.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.aliyun.svideo.template.sample.R;
import com.aliyun.svideo.template.sample.util.Common;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TemplateListActivity extends AppCompatActivity {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private FragmentAdapter mFragmentAdapter;
    private List<FragmentInfo> mFragments = new ArrayList<>();
    private View mBack;
    private static final String[] CONFIG_FILES = new String[]{"templates_vlog.json", "templates_festival.json", "templates_food.json"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.SXVE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_list);
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.viewpager);
        mBack = findViewById(R.id.back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close(v);
            }
        });
        parseCategories();
        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mFragmentAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void parseCategories() {
        try {
            for(String config : CONFIG_FILES){
                String fileName = Common.TEMPLATE_FOLDER + File.separatorChar + config;
                InputStream is = this.getAssets().open(fileName);
                byte[] bytes = new byte[is.available()];
                is.read(bytes);
                is.close();
                String json = new String(bytes);
                JSONObject category = new JSONObject(json);
                String title = category.getString("name");
                JSONArray templates = category.getJSONArray("templates");
                TemplateListFragment frag = new TemplateListFragment();
                Bundle args = new Bundle();
                args.putString(Common.KEY_TEMPLATE_LIST, templates.toString());
                frag.setArguments(args);
                mFragments.add(new FragmentInfo(frag, title));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public class FragmentInfo{
        public FragmentInfo(Fragment fragment, String title){
            this.fragment = fragment;
            this.title = title;
        }
        public Fragment fragment;
        public String title;
    }
    public class FragmentAdapter extends FragmentPagerAdapter{
        private final List<FragmentInfo> mFragments;
        public FragmentAdapter(@NonNull FragmentManager fm, List<FragmentInfo> fragments) {
            super(fm);
            this.mFragments = fragments;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position).fragment;
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragments.get(position).title;
        }
    }

    public static void start(Context ctx){
        Intent intent = new Intent(ctx, TemplateListActivity.class);
        ctx.startActivity(intent);
    }

    public void close(View view){
        finish();
    }
}
