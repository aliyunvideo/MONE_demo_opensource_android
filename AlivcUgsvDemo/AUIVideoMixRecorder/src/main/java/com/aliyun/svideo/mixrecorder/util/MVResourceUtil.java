package com.aliyun.svideo.mixrecorder.util;

import com.aliyun.svideo.downloader.DownloaderManager;
import com.aliyun.svideo.downloader.FileDownloaderModel;
import com.aliyun.svideosdk.common.struct.form.AspectForm;
import com.aliyun.svideosdk.common.struct.form.IMVForm;

import java.io.File;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MVResourceUtil {

    private final static String MV1_1 = "folder1.1";
    private final static String MV3_4 = "folder3.4";
    private final static String MV4_3 = "folder4.3";
    private final static String MV9_16 = "folder9.16";
    private final static String MV16_9 = "folder16.9";

    public static String[] mv_dirs = {
        MV1_1,
        MV3_4,
        MV4_3,
        MV9_16,
        MV16_9
    };

    public static List<IMVForm> fetchMvLocalResource() {
        IMVForm imvForm = new IMVForm();
        List<IMVForm> mvList = new ArrayList<>();
        mvList.add(imvForm);
        List<FileDownloaderModel> modelsTemp = DownloaderManager.getInstance().getDbController().getResourceByType(3);
        ArrayList<IMVForm> resourceForms = new ArrayList<>();
        ArrayList<Integer> ids = new ArrayList<>();
        List<FileDownloaderModel> models = new ArrayList<>();
        if (modelsTemp != null && modelsTemp.size() > 0) {
            for (FileDownloaderModel model : modelsTemp) {
                if (new File(model.getPath()).exists()) {
                    models.add(model);
                }
            }
            IMVForm form = null;
            ArrayList<AspectForm> pasterForms = null;
            for (FileDownloaderModel model : models) {
                if (!ids.contains(model.getId())) {
                    if (form != null) {
                        form.setAspectList(pasterForms);
                        resourceForms.add(form);
                    }
                    ids.add(model.getId());
                    form = new IMVForm();
                    pasterForms = new ArrayList<>();
                    form.setId(model.getId());
                    form.setName(model.getName());
                    form.setKey(model.getKey());
                    form.setLevel(model.getLevel());
                    form.setTag(model.getTag());
                    form.setCat(model.getCat());
                    form.setIcon(model.getIcon());
                    form.setPreviewPic(model.getPreviewpic());
                    form.setPreviewMp4(model.getPreviewmp4());
                    form.setDuration(model.getDuration());
                    form.setType(model.getSubtype());
                }
                AspectForm pasterForm = addAspectForm(model);
                pasterForms.add(pasterForm);
            }
            if (form != null) {
                form.setAspectList(pasterForms);
                resourceForms.add(form);
            }
        }
        mvList.addAll(resourceForms);
        return mvList;
    }

    private static AspectForm addAspectForm(FileDownloaderModel model) {
        AspectForm aspectForm = new AspectForm();
        aspectForm.setAspect(model.getAspect());
        aspectForm.setDownload(model.getDownload());
        aspectForm.setMd5(model.getMd5());
        aspectForm.setPath(model.getPath());
        return aspectForm;
    }

    public static String getMVPath(List<AspectForm> list, int w, int h) {
        String path = null;
        if (list == null || list.size() == 0) {
            return path;
        }
        path = calculatePercent(list, w, h);
        return path;
    }

    public static String calculatePercent(List<AspectForm> list, int w, int h) {
        int result = 0;
        String path = null;
        if (list == null || list.size() == 0 || h <= 0 || w <= 0) {
            return path;
        }
        float percent = (float)w / h;
        int aspect = 0;
        Map map = new IdentityHashMap();
        for (int i = 0; i < list.size(); i++) {
            aspect = list.get(i).getAspect();
            path = list.get(i).getPath();
            if (aspect == 1 && exits(path + File.separator + MV1_1)) {
                map.put(new Integer(1), (float)1);
            } else if (aspect == 2) {
                if (exits(path + File.separator + MV3_4)) {
                    map.put(new Integer(2), (float)3 / 4);
                }
                if (exits(path + File.separator + MV4_3)) {
                    map.put(new Integer(3), (float)4 / 3);
                }
            } else if (aspect == 3) {
                if (exits(path + File.separator + MV9_16)) {
                    map.put(new Integer(4), (float)9 / 16);
                }
                if (exits(path + File.separator + MV16_9)) {
                    map.put(new Integer(5), (float)16 / 9);
                }
            }
        }

        float diffNum = -1;
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (diffNum == -1) {
                diffNum = Math.abs(percent - (float)entry.getValue());
                result = (Integer) entry.getKey();
                continue;
            }

            float diffNumTemp = Math.abs(percent - (float)entry.getValue());
            if (diffNum >= diffNumTemp) {
                diffNum = diffNumTemp;
                result = (Integer) entry.getKey();

            }
        }
        if (result != 0) {
            for (AspectForm form : list) {
                if (result == 1 && form.getAspect() == 1) {
                    path = form.getPath();
                    break;
                } else if ((result == 2 || result == 3) && form.getAspect() == 2) {
                    path = form.getPath();
                    break;
                } else if ((result == 4 || result == 5) && form.getAspect() == 3) {
                    path = form.getPath();
                    break;
                }
            }
            path = path + File.separator + mv_dirs[result - 1];
        }
        return path;
    }

    public static boolean exits(String path) {
        boolean isExits = false;
        if (path == null || "".equals(path)) {
            return isExits;
        }
        File file = new File(path);
        if (file.exists()) {
            isExits = true;
        }
        return isExits;
    }


}
