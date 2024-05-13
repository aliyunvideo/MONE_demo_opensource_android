package com.aliyun.auiplayerserver.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class AliyunVideoListBean {
    /**
     * 视频审核状态，审核中
     */
    public static final String STATUS_CENSOR_ON = "onCensor";
    /**
     * 视频审核状态，待审核
     */
    public static final String STATUS_CENSOR_WAIT = "check";
    /**
     * 视频审核状态，审核通过
     */
    public static final String STATUS_CENSOR_SUCCESS = "success";
    /**
     * 视频审核状态，审核不通过
     */
    public static final String STATUS_CENSOR_FAIL = "fail";

    private String message;
    private int code;
    private VideoDataBean data;

    public static class VideoDataBean {
        private int total;
        private List<VideoListBean> videoList;

        public static class VideoListBean implements Parcelable {
            /**
             * id
             */
            protected String id = "";
            private String videoId;
            private String title;
            private String status;
            private String coverUrl;
            private String censorStatus;
            private String firstFrameUrl;
            private String randomUUID = null;
            private User user;
            private float duration;


            public VideoSourceType getSourceType() {
                if (STATUS_CENSOR_SUCCESS.equals(censorStatus)) {
                    return VideoSourceType.TYPE_STS;
                } else {
                    return VideoSourceType.TYPE_ERROR_NOT_SHOW;
                }
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getCensorStatus() {
                return censorStatus;
            }

            public void setCensorStatus(String censorStatus) {
                this.censorStatus = censorStatus;
            }

            public String getFirstFrameUrl() {
                return firstFrameUrl;
            }

            public void setFirstFrameUrl(String firstFrameUrl) {
                this.firstFrameUrl = firstFrameUrl;
            }

            public String getVideoId() {
                return videoId;
            }

            public void setVideoId(String videoId) {
                this.videoId = videoId;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getCoverUrl() {
                return coverUrl;
            }

            public void setCoverUrl(String coverUrl) {
                this.coverUrl = coverUrl;
            }

            public int getId() {
                int i = 0;
                try {
                    i = Integer.parseInt(id);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                return i;

            }

            public void setId(int id) {
                this.id = id + "";
            }

            public String getRandomUUID() {
                return randomUUID;
            }

            public void setRandomUUID(String randomUUID) {
                this.randomUUID = randomUUID;
            }

            public User getUser() {
                return user;
            }

            public void setUser(User user) {
                this.user = user;
            }

            public float getDuration() {
                return duration;
            }

            public void setDuration(float duration) {
                this.duration = duration;
            }

            @Override
            public String toString() {
                return "VideoListBean{" +
                        "id='" + id + '\'' +
                        ", videoId='" + videoId + '\'' +
                        ", title='" + title + '\'' +
                        ", status='" + status + '\'' +
                        ", coverUrl='" + coverUrl + '\'' +
                        ", censorStatus='" + censorStatus + '\'' +
                        ", firstFrameUrl='" + firstFrameUrl + '\'' +
                        ", randomUUID='" + randomUUID + '\'' +
                        ", user=" + user +
                        ", duration=" + duration +
                        '}';
            }


            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.id);
                dest.writeString(this.videoId);
                dest.writeString(this.title);
                dest.writeString(this.status);
                dest.writeString(this.coverUrl);
                dest.writeString(this.censorStatus);
                dest.writeString(this.firstFrameUrl);
                dest.writeString(this.randomUUID);
                dest.writeParcelable(this.user, flags);
                dest.writeFloat(this.duration);
            }

            public void readFromParcel(Parcel source) {
                this.id = source.readString();
                this.videoId = source.readString();
                this.title = source.readString();
                this.status = source.readString();
                this.coverUrl = source.readString();
                this.censorStatus = source.readString();
                this.firstFrameUrl = source.readString();
                this.randomUUID = source.readString();
                this.user = source.readParcelable(User.class.getClassLoader());
                this.duration = source.readFloat();
            }

            public VideoListBean() {
            }

            protected VideoListBean(Parcel in) {
                this.id = in.readString();
                this.videoId = in.readString();
                this.title = in.readString();
                this.status = in.readString();
                this.coverUrl = in.readString();
                this.censorStatus = in.readString();
                this.firstFrameUrl = in.readString();
                this.randomUUID = in.readString();
                this.user = in.readParcelable(User.class.getClassLoader());
                this.duration = in.readFloat();
            }

            public static final Parcelable.Creator<VideoListBean> CREATOR = new Parcelable.Creator<VideoListBean>() {
                @Override
                public VideoListBean createFromParcel(Parcel source) {
                    return new VideoListBean(source);
                }

                @Override
                public VideoListBean[] newArray(int size) {
                    return new VideoListBean[size];
                }
            };
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<VideoListBean> getVideoList() {
            return videoList;
        }

        public void setVideoList(List<VideoListBean> videoList) {
            this.videoList = videoList;
        }

        @Override
        public String toString() {
            return "VideoDataBean{" +
                    "total=" + total +
                    ", videoList=" + videoList +
                    '}';
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public VideoDataBean getData() {
        return data;
    }

    public void setData(VideoDataBean data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AliyunVideoListBean{" +
                "message='" + message + '\'' +
                ", code=" + code +
                ", data=" + data +
                '}';
    }

    /**
     * "user": {
     * "userId": "8895749",
     * "userName": "Martha",
     * "avatarUrl": "http://live-appserver-sh.alivecdn.com/heads/03.png"
     * }
     */
    public static class User implements Parcelable {
        public String userId = "";
        public String userName = "";
        public String avatarUrl = "";

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        @Override
        public String toString() {
            return "User{" +
                    "userId='" + userId + '\'' +
                    ", userName='" + userName + '\'' +
                    ", avatarUrl='" + avatarUrl + '\'' +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.userId);
            dest.writeString(this.userName);
            dest.writeString(this.avatarUrl);
        }

        public void readFromParcel(Parcel source) {
            this.userId = source.readString();
            this.userName = source.readString();
            this.avatarUrl = source.readString();
        }

        public User() {
        }

        protected User(Parcel in) {
            this.userId = in.readString();
            this.userName = in.readString();
            this.avatarUrl = in.readString();
        }

        public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
            @Override
            public User createFromParcel(Parcel source) {
                return new User(source);
            }

            @Override
            public User[] newArray(int size) {
                return new User[size];
            }
        };
    }
}
