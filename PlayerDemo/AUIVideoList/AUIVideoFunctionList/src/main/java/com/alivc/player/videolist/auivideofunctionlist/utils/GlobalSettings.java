package com.alivc.player.videolist.auivideofunctionlist.utils;

public class GlobalSettings {

    /**
     * enable local cache
     */
    public static boolean CACHE_IS_ENABLE = true;
    /**
     * 最大内存缓存大小 M
     */
    public static int CACHE_MEMORY_SIZE = 10;
    /**
     * 最大缓存容量 M
     */
    public static int CACHE_SIZE = 20 * 1024;
    /**
     * 缓存过期时间 天
     */
    public static int CACHE_EXPIRED_TIME = 30;
    /**
     * 磁盘最小空余容量 M
     */
    public static int CACHE_FREE_STORAGE_SIZE = 0;
    /**
     * 缓存文件路径
     */
    public static String CACHE_DIR = "";

    /**
     * 预加载时长
     */
    public static int DURATION = 5 * 1000;

}
