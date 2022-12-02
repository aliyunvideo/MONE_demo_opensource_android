#!/bin/sh

aui_root_path=$1
target_root_path=$2
foundation_dir=$aui_root_path/AUIFoundation
ugsv_dir=$aui_root_path/AlivcUgsvDemo

target_aui_dir=$target_root_path/AUI
target_ugc_dir=$target_aui_dir/AUIShortVideo

echo "import start..."

mkdir -p $target_ugc_dir

echo "copy AUIFoundation..."
cp -rf $aui_root_path/AUIFoundation $target_aui_dir/AUIFoundation

echo "copy AndroidThirdParty..."
cp -rf $aui_root_path/AndroidThirdParty $target_aui_dir/AndroidThirdParty

echo "copy AliyunVideoCommon..."
cp -rf $ugsv_dir/AliyunVideoCommon $target_ugc_dir/AliyunVideoCommon

echo "copy AUIBeauty..."
cp -rf $ugsv_dir/AUIBeauty $target_ugc_dir/AUIBeauty

echo "copy AUIMedia..."
cp -rf $ugsv_dir/AUIMedia $target_ugc_dir/AUIMedia

echo "copy AUIUgsvBase..."
cp -rf $ugsv_dir/AUIUgsvBase $target_ugc_dir/AUIUgsvBase

echo "copy AUICrop..."
cp -rf $ugsv_dir/AUICrop $target_ugc_dir/AUICrop

echo "copy AUIMusic..."
cp -rf $ugsv_dir/AUIMusic $target_ugc_dir/AUIMusic

echo "copy AUIVideoEditor..."
cp -rf $ugsv_dir/AUIVideoEditor $target_ugc_dir/AUIVideoEditor

echo "copy AUIFileDownloader..."
cp -rf $ugsv_dir/AUIFileDownloader $target_ugc_dir/AUIFileDownloader

echo "copy AUIResample..."
cp -rf $ugsv_dir/AUIResample $target_ugc_dir/AUIResample

echo "copy AUIVideoRecorder..."
cp -rf $ugsv_dir/AUIVideoRecorder $target_ugc_dir/AUIVideoRecorder

echo "copy UGSVApp..."
cp -rf $ugsv_dir/UGSVApp $target_ugc_dir/UGSVApp


echo "copy register all project..."
sed -i '' "/.*buildscript.*/a\\
    apply from: \"AUI\/AndroidThirdParty\/config.gradle\"
" $target_root_path/build.gradle

sed -i '' "/.*repositories.*/a\\
    maven { \\
        allowInsecureProtocol = true \\
        url \"http:\/\/maven.aliyun.com\/repository\/releases\" \\
    }
" $target_root_path/build.gradle

sed -i '' "s/if (\"true\".equalsIgnoreCase(allInOne).*/if(true) { /g" $target_aui_dir/AndroidThirdParty/config.gradle

sed -i '' "s/if (!hasQueen()).*/if(false) { /g" $target_ugc_dir/AUIBeauty/QueenBeauty/build.gradle

sed -i '' "s/buildConfigField(\"int\", \"APK_TYPE\".*/buildConfigField(\"int\", \"APK_TYPE\", \"1\") /g" $target_ugc_dir/UGSVApp/build.gradle

echo "include ':AUI'" >> $target_root_path/settings.gradle
echo "project(':AUI').projectDir = new File(\"AUI\")" >> $target_root_path/settings.gradle

echo "include ':AUIFoundation:AVTheme'" >> $target_root_path/settings.gradle
echo "project(':AUIFoundation:AVTheme').projectDir = new File(\"AUI/AUIFoundation/AVTheme\")" >> $target_root_path/settings.gradle

echo "include ':AUIFoundation:AVBaseUI'" >> $target_root_path/settings.gradle
echo "project(':AUIFoundation:AVBaseUI').projectDir = new File(\"AUI/AUIFoundation/AVBaseUI\")" >> $target_root_path/settings.gradle

echo "include ':AUIFoundation:AVMatisse'" >> $target_root_path/settings.gradle
echo "project(':AUIFoundation:AVMatisse').projectDir = new File(\"AUI/AUIFoundation/AVMatisse\")" >> $target_root_path/settings.gradle

echo "include ':AUIFoundation:AVUtils'" >> $target_root_path/settings.gradle
echo "project(':AUIFoundation:AVUtils').projectDir = new File(\"AUI/AUIFoundation/AVUtils\")" >> $target_root_path/settings.gradle

echo "include ':AUIMedia'" >> $target_root_path/settings.gradle
echo "project(':AUIMedia').projectDir = new File(\"AUI/AUIShortVideo/AUIMedia\")" >> $target_root_path/settings.gradle

echo "include ':AUICrop'" >> $target_root_path/settings.gradle
echo "project(':AUICrop').projectDir = new File(\"AUI/AUIShortVideo/AUICrop\")" >> $target_root_path/settings.gradle

echo "include ':AUICrop:crop'" >> $target_root_path/settings.gradle
echo "include ':AUICrop:crop:snap_crop'" >> $target_root_path/settings.gradle

echo "include ':AUIVideoEditor'" >> $target_root_path/settings.gradle
echo "project(':AUIVideoEditor').projectDir = new File(\"AUI/AUIShortVideo/AUIVideoEditor\")" >> $target_root_path/settings.gradle
echo "include ':AUIVideoEditor:AUIEditorSticker'" >> $target_root_path/settings.gradle
echo "include ':AUIVideoEditor:AUIEditorCaption'" >> $target_root_path/settings.gradle
echo "include ':AUIVideoEditor:AUIEditorCommon'" >> $target_root_path/settings.gradle
echo "include ':AUIVideoEditor:AUIVideoTrack'" >> $target_root_path/settings.gradle
echo "include ':AUIVideoEditor:AUIEditorClip'" >> $target_root_path/settings.gradle
echo "include ':AUIVideoEditor:AUIEditorEffect'" >> $target_root_path/settings.gradle

echo "include ':AUIVideoRecorder'" >> $target_root_path/settings.gradle
echo "project(':AUIVideoRecorder').projectDir = new File(\"AUI/AUIShortVideo/AUIVideoRecorder\")" >> $target_root_path/settings.gradle

echo "include ':AUIFileDownloader'" >> $target_root_path/settings.gradle
echo "project(':AUIFileDownloader').projectDir = new File(\"AUI/AUIShortVideo/AUIFileDownloader\")" >> $target_root_path/settings.gradle

echo "include ':AUIResample'" >> $target_root_path/settings.gradle
echo "project(':AUIResample').projectDir = new File(\"AUI/AUIShortVideo/AUIResample\")" >> $target_root_path/settings.gradle

echo "include ':AUIUgsvBase'" >> $target_root_path/settings.gradle
echo "project(':AUIUgsvBase').projectDir = new File(\"AUI/AUIShortVideo/AUIUgsvBase\")" >> $target_root_path/settings.gradle

echo "include ':AUIMusic'" >> $target_root_path/settings.gradle
echo "project(':AUIMusic').projectDir = new File(\"AUI/AUIShortVideo/AUIMusic\")" >> $target_root_path/settings.gradle

echo "include ':AliyunVideoCommon'" >> $target_root_path/settings.gradle
echo "project(':AliyunVideoCommon').projectDir = new File(\"AUI/AUIShortVideo/AliyunVideoCommon\")" >> $target_root_path/settings.gradle

echo "include ':AUIBeauty'" >> $target_root_path/settings.gradle
echo "project(':AUIBeauty').projectDir = new File(\"AUI/AUIShortVideo/AUIBeauty\")" >> $target_root_path/settings.gradle

echo "include ':QueenBeauty'" >> $target_root_path/settings.gradle
echo "project(':QueenBeauty').projectDir = new File(\"AUI/AUIShortVideo/AUIBeauty/QueenBeauty\")" >> $target_root_path/settings.gradle

echo "include ':FaceunityBeauty'" >> $target_root_path/settings.gradle
echo "project(':FaceunityBeauty').projectDir = new File(\"AUI/AUIShortVideo/AUIBeauty/FaceunityBeauty\")" >> $target_root_path/settings.gradle

echo "include ':UGSVApp'" >> $target_root_path/settings.gradle
echo "project(':UGSVApp').projectDir = new File(\"AUI/AUIShortVideo/UGSVApp\")" >> $target_root_path/settings.gradle
echo "//project(':UGSVApp').setBuildFileName('build_lib.gradle')" >> $target_root_path/settings.gradle


echo "...done!!! finish"