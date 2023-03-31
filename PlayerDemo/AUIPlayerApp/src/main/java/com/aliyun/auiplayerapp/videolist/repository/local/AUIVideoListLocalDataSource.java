package com.aliyun.auiplayerapp.videolist.repository.local;

import android.content.Context;

import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;
import com.aliyun.auiplayerapp.videolist.repository.AUIVideoListDataSource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class AUIVideoListLocalDataSource implements AUIVideoListDataSource {

   private final List<VideoInfo> mVideoInfo;

   /**
    * load assets/videolist.txt
    */
   public AUIVideoListLocalDataSource(Context context){
      Gson gson = new Gson();
      StringBuilder stringBuilder = new StringBuilder();
      try (InputStream assetsInputStream = context.getAssets().open("videolist.txt");
           BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assetsInputStream))) {
         String line;
         while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
      mVideoInfo = gson.fromJson(stringBuilder.toString(), new TypeToken<LinkedList<VideoInfo>>() {
      }.getType());
   }

   @Override
   public List<VideoInfo> getVideoList() {
      return mVideoInfo;
   }
}
