package com.alivc.player.videolist.auivideofunctionlist.player;

import android.content.Context;
import android.util.Log;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.nativeclass.PlayerConfig;

import java.util.LinkedHashMap;

/**
 * @author baorunchen
 * @date 2024/4/10
 * @brief Manages a pool of AliPlayer instances to be reused across the application.
 * @note Call logic and timing:
 * @note {@link AliPlayerPool#init(Context)} -> {@link AliPlayerPool#getPlayer(String)} -> ... -> {@link AliPlayerPool#release()}
 */
public class AliPlayerPool {
    private static final String TAG = "[AUI]AliPlayerPool";

    private static final int INITIAL_CAPACITY = 3;
    private final LinkedHashMap<String, AliPlayer> mPlayerPool = new LinkedHashMap<>(INITIAL_CAPACITY, 0.75f, true);

    // Through interface settings, a full screen effect can be achieved, with the default being IPlayer ScaleMode SCALE_ASPECT_FIT
    // The current SDK defaults to IPlayer ScaleMode SCALE-ASPECT_FIT, which means that the image is filled based on its own aspect ratio, and the shorter side is not fully covered on the entire screen, but it will cause the remaining space to be transparent, similar to the top and bottom black edges
    // If you want a full screen effect, please set it to IPlayer ScaleMode SCALE_ASPECT-FILL, which means that the image is filled based on its own aspect ratio, and the excess is cropped, but it can cause the image to be displayed incompletely, similar to zooming in and displaying a portion
    private static final IPlayer.ScaleMode DEFAULT_VIDEO_SCALE_MODE = IPlayer.ScaleMode.SCALE_ASPECT_FIT;

    private Context mContext;

    /**
     * Initializes the player pool with predetermined number of AliPlayer instances.
     *
     * @param context The application context.
     * @note Considering the instant performance, avoid using lazy loading to initialize three player instances,
     * @note please call the init method at a more appropriate location to initialize the player.
     * @attention Corresponding interface: {@link AliPlayerPool#release()}
     */
    public void init(Context context) {
        release();
        mContext = context;
    }

    /**
     * Releases all AliPlayer instances and clears the pool.
     *
     * @note Considering memory issues, please destroy 3 player instances in a more suitable location to avoid memory leakage
     */
    public void release() {
        synchronized (mPlayerPool) {
            for (AliPlayer aliPlayer : mPlayerPool.values()) {
                destroyAliPlayerInstance(aliPlayer);
            }
            mPlayerPool.clear();
        }
        mContext = null;
    }

    /**
     * Retrieves an AliPlayer instance from the pool based on a key. If the key is
     * not associated with an instance, the least recently used player is recycled.
     *
     * @param key The key to identify the needed AliPlayer instance.
     * @return The AliPlayer instance associated with the given key.
     * @attention If there is a crash caused by a failure to retrieve the player instance after calling this interface,
     * @attention please ensure that you have completed the call to the interface {@link AliPlayerPool#init(Context)}.
     */
    public AliPlayer getPlayer(String key) {
        synchronized (mPlayerPool) {
            AliPlayer aliPlayer = mPlayerPool.get(key);
            if (aliPlayer != null) {
                Log.i(TAG, "[REUSE][" + key + "]");
                return aliPlayer;
            }

            // Originally implemented using linked lists, the purpose was to achieve reusability of player instances and reduce performance overhead.
            // However, due to various invocation issues in the application layer, exceptions are caused.
            // Therefore, we adopt a method where one view corresponds to one unique player to avoid the reuse of player instances.
            if (mPlayerPool.size() == INITIAL_CAPACITY) {
                String oldestKey = mPlayerPool.keySet().iterator().next();
                AliPlayer oldAliPlayer = mPlayerPool.remove(oldestKey);
                destroyAliPlayerInstance(oldAliPlayer);
            }

            aliPlayer = initNewAliPlayerInstance(mContext);
            Log.i(TAG, "[CREATE][" + key + "]");
            // Insert to end of LinkedHashMap, marking it as most recently used.
            mPlayerPool.put(key, aliPlayer);
            return aliPlayer;
        }
    }

    /// TODO keria
    /// If further design is needed, it can be considered to expose the init/destroy method in the form of a callback,
    /// with the internal logic only used for the creation, destruction, retrieval, reuse, and recycling of player instances

    /**
     * Creates and initializes a new AliPlayer instance.
     *
     * @param context The application context.
     * @return A new instance of AliPlayer.
     */
    private static AliPlayer initNewAliPlayerInstance(Context context) {
        AliPlayer aliPlayer = AliPlayerFactory.createAliPlayer(context);
        PlayerConfig config = aliPlayer.getConfig();
        config.mClearFrameWhenStop = true;
        aliPlayer.setConfig(config);
        aliPlayer.setLoop(true);
        aliPlayer.setScaleMode(DEFAULT_VIDEO_SCALE_MODE);

        Log.i(TAG, "[INIT]: " + aliPlayer);
        return aliPlayer;
    }

    /**
     * Destroys the AliPlayer instance and releases its resources.
     *
     * @param aliPlayer The AliPlayer instance to destroy.
     */
    private static void destroyAliPlayerInstance(AliPlayer aliPlayer) {
        if (aliPlayer != null) {
            Log.i(TAG, "[DESTROY]: " + aliPlayer);
            aliPlayer.setSurface(null);
            aliPlayer.stop();
            aliPlayer.release();
        }
    }
}
