package com.vidshop.base.image

import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

/*
 * Copyright (C) 2004 - 2019 UCWeb Inc. All Rights Reserved.
 * Description : AppGlid配置
 *
 */
@GlideModule
class AUIGlideAppModule : AppGlideModule() {
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}