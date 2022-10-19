package com.aliyun.svideo.editor.caption.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.aliyun.svideo.editor.caption.ICaptionTabPage
import com.aliyun.svideo.editor.caption.viewmodel.CaptionEditViewModel

class CaptionEditPanelAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mDataList: List<ICaptionTabPage> = arrayListOf()
    private lateinit var captionEditViewModel: CaptionEditViewModel

    fun setData(dataList : List<ICaptionTabPage>) {
        mDataList = dataList;
    }

    fun setViewModel(captionEditVM: CaptionEditViewModel) {
        this.captionEditViewModel = captionEditVM

    }
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val tabPage = mDataList[viewType]
        tabPage.onTabInit()
        val layoutParam = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        tabPage.getView().layoutParams = layoutParam
        return RecycleHolder(tabPage.getView())
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val tabPage = mDataList[position]
        tabPage.onBind(this.captionEditViewModel)
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    inner class RecycleHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}