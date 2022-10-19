package com.aliyun.svideo.editor.bean;

/**
 * @author cross_ly DATE 2019/09/25
 * <p>描述:
 */
public class ImageUploadCallbackBean {

    /**
     * result : true
     * requestId : a89139b7-5db2-42a8-8d08-279116062242
     * message :
     * code : 200
     * data : {"uploadAddress
     * ":"==","uploadAuth":"\==","imageId":"","imageURL":"https:/c-demo-vod.png"}
     */

    private String result;
    private String requestId;
    private String message;
    private String code;
    private DataBean data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * uploadAddress :
         * ==
         * uploadAuth :
         * ==
         * imageId :
         * imageURL : ://alivc-demo-vod
         */

        private String uploadAddress;
        private String uploadAuth;
        private String imageId;
        private String imageURL;

        public String getUploadAddress() {
            return uploadAddress;
        }

        public void setUploadAddress(String uploadAddress) {
            this.uploadAddress = uploadAddress;
        }

        public String getUploadAuth() {
            return uploadAuth;
        }

        public void setUploadAuth(String uploadAuth) {
            this.uploadAuth = uploadAuth;
        }

        public String getImageId() {
            return imageId;
        }

        public void setImageId(String imageId) {
            this.imageId = imageId;
        }

        public String getImageURL() {
            return imageURL;
        }

        public void setImageURL(String imageURL) {
            this.imageURL = imageURL;
        }
    }
}
