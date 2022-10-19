package com.aliyun.svideo.editor.publish

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.aliyun.aio.avtheme.AVBaseThemeActivity
import com.aliyun.svideo.editor.AUIVideoEditor
import com.aliyun.svideo.editor.R
import com.aliyun.svideo.editor.common.panel.OnItemClickListener
import com.aliyun.svideo.editor.common.panel.viewmodel.PanelItemId
import com.aliyun.svideo.editor.databinding.UgsvEditorPublishBinding
import com.vidshop.base.image.GlideApp
import com.zhihu.matisse.AVMatisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import java.util.*

class AUIPublishActivity : AVBaseThemeActivity(), OnItemClickListener{

    companion object{
        const val COVER_CHOOSE_CODE = 1003
        const val COMPOSE_UPDATE_CODE = 1004
        const val KEY_COVER_PATH = "cover_path"
        const val KEY_PARAM_CONFIG = "project_json_path"
    }

    private lateinit var mDataBinding: UgsvEditorPublishBinding
    var mSelectPath:String? = null
    var mProjectConfig:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.ugsv_editor_publish)
        initArgs()
        init()
    }

    private fun initArgs() {
        mSelectPath = intent.getStringExtra(KEY_COVER_PATH)
        mProjectConfig = intent.getStringExtra(KEY_PARAM_CONFIG)
    }

    private fun init() {
        var viewModelProvider = ViewModelProvider(this)
        var model = viewModelProvider.get(PublishViewModel::class.java)
        model.onBind(this)
        model.setOnItemClickListener(this)
        mDataBinding.viewModel = model
        mDataBinding.lifecycleOwner = this
        mDataBinding.avBaseTitle.leftImageView.setOnClickListener { v ->
            onBackPressed()
        }
        mSelectPath?.let {
            GlideApp.with(this).load(mSelectPath).override(512)
                .into(mDataBinding.ugsvEditorPic)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == COVER_CHOOSE_CODE) {
            if (data == null) {
                return
            }
            var list = AVMatisse.obtainPathResult(data)
            if (list.size > 0) {
                mSelectPath = list.get(0)
                GlideApp.with(this).load(mSelectPath).override(512)
                    .into(mDataBinding.ugsvEditorPic)
            }
        } else if (requestCode == COMPOSE_UPDATE_CODE) {
            if (resultCode == RESULT_OK) {
                var intent = Intent()
                intent.putExtra(KEY_COVER_PATH, mSelectPath)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onItemClick(view: View?, id: Long, obj: Any?) {
        when(id){
            PanelItemId.ITEM_ID_PUBLISH->{
                onPublish()
            }
            PanelItemId.ITEM_ID_SELECT_COVER->{
                onSelectCover()
            }
        }
    }

    fun onSelectCover() {
        AVMatisse.from(this).choose(MimeType.ofImage(), true)
            .showSingleMediaType(true)
            .maxSelectable(1).countable(false).restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .thumbnailScale(0.85f)
            .imageEngine(GlideEngine())
            .showPreview(true) // Default is `true`
            .forResult(COVER_CHOOSE_CODE)
    }

    fun onPublish() {
        AUIVideoEditor.startEditorCompose(this, mProjectConfig, mSelectPath,mDataBinding.viewModel?.mDesText?.value, COMPOSE_UPDATE_CODE)
    }


}