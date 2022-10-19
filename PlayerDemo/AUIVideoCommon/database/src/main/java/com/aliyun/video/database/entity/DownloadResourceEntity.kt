package com.aliyun.video.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 *  {
"id": 6,
"name": "流行转场",
"nameEn": null,
"description": "描述",
"descriptionEn": "desc",
"resourceTypeCode": 2,
"resourceTypeDesc": "TRANSITION",
"previewTypeCode": null,
"previewTypeDesc": null,
"previewUrl": null,
"resourceId": "9ed2c2e1018f4059b7807f80fd59464c",
"resourceUrl": "http://alivc-demo-vod.aliyuncs.com/attached/material/195AA145E7454E5DBF82D11D6E9C43DE-7-4.mat",
"iconId": "1bd5ec854b93416c86f588f5d111ed6c",
"iconUrl": "http://alivc-demo-vod.aliyuncs.com/attached/material/780335C32B5543D68D6126463254FF9A-7-4.png",
"sort": 1,
"creationTime": "2019-02-11T03:39:04Z"
}
 */
@Entity
data class DownloadResourceEntity(
    @PrimaryKey()
    @SerializedName("id") var id: Int,
    @ColumnInfo(name = "name")
    @SerializedName("name") var name: String,
    @ColumnInfo(name = "nameEn")
    @SerializedName("nameEn") var nameEn: String,
    @ColumnInfo(name = "description")
    @SerializedName("description") var description: String,
    @ColumnInfo(name = "descriptionEn")
    @SerializedName("descriptionEn") var descriptionEn: String,
    @ColumnInfo(name = "resourceTypeCode")
    @SerializedName("resourceTypeCode") var resourceTypeCode: Int,
    @ColumnInfo(name = "resourceTypeDesc")
    @SerializedName("resourceTypeDesc") var resourceTypeDesc: String,
    @ColumnInfo(name = "previewTypeCode")
    @SerializedName("previewTypeCode") var previewTypeCode: String,
    @ColumnInfo(name = "previewTypeDesc")
    @SerializedName("previewTypeDesc") var previewTypeDesc: String,
    @ColumnInfo(name = "previewUrl")
    @SerializedName("previewUrl") var previewUrl: String,
    @ColumnInfo(name = "resourceId")
    @SerializedName("resourceId") var resourceId: String,
    @ColumnInfo(name = "resourceUrl")
    @SerializedName("resourceUrl") var resourceUrl: String,
    @ColumnInfo(name = "iconId")
    @SerializedName("iconId") var iconId: String,
    @ColumnInfo(name = "iconUrl")
    @SerializedName("iconUrl") var iconUrl: String,
    @ColumnInfo(name = "sort")
    @SerializedName("sort") var sort: Int,
    @ColumnInfo(name = "creationTime")
    @SerializedName("creationTime") var creationTime: String,
    /**
     * 本地保存路径
     */
    @ColumnInfo(name = "path")
    @SerializedName("path") var downLoadPath: String

)