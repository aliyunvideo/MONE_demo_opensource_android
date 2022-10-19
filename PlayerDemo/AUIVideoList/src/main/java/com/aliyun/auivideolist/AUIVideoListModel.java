package com.aliyun.auivideolist;

import android.content.Context;

import com.aliyun.auivideolist.bean.ListVideoBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class AUIVideoListModel {

    private static LinkedList<ListVideoBean> mListVideoBean;

    public LinkedList<ListVideoBean> loadData(Context context){
        Gson gson = new Gson();
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream assetsInputStream = context.getAssets().open("videolist.txt");
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assetsInputStream))){
            String line;
            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mListVideoBean = gson.fromJson(stringBuilder.toString(), new TypeToken<LinkedList<ListVideoBean>>() {}.getType());
        return mListVideoBean;
    }

    public LinkedList<ListVideoBean> getData(){
        return mListVideoBean;
    }
}
