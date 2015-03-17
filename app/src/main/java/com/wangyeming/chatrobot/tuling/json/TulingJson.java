package com.wangyeming.chatrobot.tuling.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * fastJSON 处理JSON调用的类
 * @author Wang
 * @data 2015/3/14
 */
public class TulingJson implements Serializable {
    public String code;
    public String text;
    public List<Lists> list = new ArrayList<>();
    public String url;

}
