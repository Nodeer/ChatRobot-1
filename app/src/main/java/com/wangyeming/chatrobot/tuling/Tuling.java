package com.wangyeming.chatrobot.tuling;

/**
 * 图灵接入API
 * @author Wang
 * @data 2015/3/13
 */
public class Tuling {

    private final static String API_URI = "http://www.tuling123.com/openapi/api";
    private final static String TULING_KEY = "fa3747334ed3f1a1520238ffa32ea415";

    private String info;    //消息内容
    private String userId;  //针对每一个用户的id
    private String loc;     //位置信息，编码方式为UTF-8
    private String lon;     //经度信息
    private String lat;     //纬度信息

    private String Uri;

    /**
     * 设置消息内容
     * @param info
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * 获取消息内容
     * @return
     */
    public String getInfo() {
        return info;
    }

    /**
     * 设置userId
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获取userId
     * @return
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置位置信息
     * @param loc
     */
    public void setLoc(String loc) {
        this.loc = loc;
    }

    /**
     * 获取位置信息
     * @return
     */
    public String getLoc() {
        return loc;
    }

    /**
     * 设置经度信息
     * @param lon
     */
    public void setLon(String lon) {
        this.lon = lon;
    }

    /**
     * 获取经度信息
     * @return
     */
    public String getLon() {
        return lon;
    }

    /**
     * 设置纬度信息
     * @param lat
     */
    public void setLat(String lat) {
        this.lat = lat;
    }

    /**
     * 获取纬度信息
     * @return
     */
    public String getLat() {
        return lat;
    }

    /**
     * 返回生成的uri
     * @return
     */
    public String generateUri() {
        String uri = API_URI + "?" + "key=" + TULING_KEY + "&" + "info=" + this.getInfo()
                + "&" + "userid=" + this.getUserId();
        if(this.getLoc() != null) {
            uri = uri + "&" + "loc=" + this.getLoc();
        }
        if(this.getLon() != null) {
            uri = uri + "&" + "lon=" + this.getLon();
        }
        if(this.getLat() != null) {
            uri = uri + "&" + "lat=" + this.getLat();
        }
        return uri.replaceAll(" ", "&nbsp");
    }
}
