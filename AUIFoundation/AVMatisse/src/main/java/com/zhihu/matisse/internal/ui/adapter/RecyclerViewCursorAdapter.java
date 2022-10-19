/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhihu.matisse.internal.ui.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import androidx.recyclerview.widget.RecyclerView;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.utils.PathUtils;
import com.zhihu.matisse.internal.utils.ThreadUtils;

public abstract class RecyclerViewCursorAdapter<VH extends RecyclerView.ViewHolder> extends
        RecyclerView.Adapter<VH> {

    private Cursor mCursor;
    private int mRowIDColumn;
    private List<Item> mItemList = new ArrayList<>();
    private Context mContext;

    RecyclerViewCursorAdapter(Context context, Cursor c) {
        mContext = context;
        setHasStableIds(true);
        swapCursor(c);
    }

    protected abstract void onBindViewHolder(VH holder, Item item);

    @Override
    public void onBindViewHolder(VH holder, int position) {
        //if (!isDataValid(mCursor)) {
        //    throw new IllegalStateException("Cannot bind view holder when cursor is in invalid state.");
        //}
        //if (!mCursor.moveToPosition(position)) {
        //    throw new IllegalStateException("Could not move cursor to position " + position
        //            + " when trying to bind view holder");
        //}
        Item item = mItemList.get(position);
        onBindViewHolder(holder, item);
    }

    @Override
    public int getItemViewType(int position) {
        //if (!mCursor.moveToPosition(position)) {
        //    throw new IllegalStateException("Could not move cursor to position " + position
        //            + " when trying to get item view type.");
        //}
        Item item = mItemList.get(position);
        return getItemViewType(position, item);
    }

    protected abstract int getItemViewType(int position, Item item);

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @Override
    public long getItemId(int position) {
        //if (!isDataValid(mCursor)) {
        //    throw new IllegalStateException("Cannot lookup item id when cursor is in invalid state.");
        //}
        //if (!mCursor.moveToPosition(position)) {
        //    throw new IllegalStateException("Could not move cursor to position " + position
        //            + " when trying to get an item id");
        //}
        return mItemList.get(position).id;
        //return mCursor.getLong(mRowIDColumn);
    }

    public synchronized void swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return;
        }

        if (newCursor != null) {
            mCursor = newCursor;
            mRowIDColumn = mCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
            parserData();
            // notify the observers about the new cursor
        } else {
            notifyItemRangeRemoved(0, getItemCount());
            mItemList.clear();
            mCursor = null;
            mRowIDColumn = -1;
        }
    }

    private void parserData() {
        ThreadUtils.runOnSubThread(() -> {
            if (!isDataValid(mCursor)) {
                return;
            }
            mItemList.clear();
            int count = 0;
            mCursor.moveToFirst();
            do {
                Item item = Item.valueOf(mCursor);
                if (item == null) {
                    return;
                }
                String path = PathUtils.getPath(mContext, item.getContentUri());
                if (path != null && new File(path).exists()) {
                    mItemList.add(item);
                    int finalCount = count;
                    ThreadUtils.runOnUiThread(()->{
                        notifyItemInserted(finalCount);
                    });
                    count++;
                }
                if (!isDataValid(mCursor)) {
                    return;
                }
            } while (mCursor.moveToNext());
        });

    }

    public Cursor getCursor() {
        return mCursor;
    }

    private boolean isDataValid(Cursor cursor) {
        return cursor != null && !cursor.isClosed();
    }
}
