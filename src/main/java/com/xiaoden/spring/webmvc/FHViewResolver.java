package com.xiaoden.spring.webmvc;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dengfuhai
 * @description设计这个类的主要目的是：
 * 1.将一个静态文件变成一个动态文件
 * 2.根据用户传入的参数不同，产生不同的结果
 * 最终输出字符串，交给response输出
 * @date 2019/10/8 0008
 */
public class FHViewResolver {
    private String viewName;//视图文件名字
    private File templateFile;//视图文件

    public  FHViewResolver(String viewName,File templateFile){
        this.viewName=viewName;
        this.templateFile=templateFile;
    }
    /*
     * 功能描述: 这里把ModelAndView变成被渲染的View返回
     * @param mv,里面保存着渲染的数据和要渲染的视图名
     * @return java.lang.String
     */
    public String viewResolver(FHModelAndView mv)throws Exception{
        StringBuffer sb = new StringBuffer();
        //这个对象是对这个文件的任意位置进行读写操作
        RandomAccessFile ra = new RandomAccessFile(this.templateFile, "r");
        String line=null;
        while(null==(line=ra.readLine())){//一行一行读取
            Matcher matcher = matcher(line);//匹配一行
            while(matcher.find()){//一行中发现匹配上的
                for(int i=0;i<matcher.groupCount();i++){
                    //把￥{}中间的字符串取出来
                    String paramName=matcher.group(i);
                    Object paramValue = mv.getModel().get(paramName);
                    if(paramValue==null){
                        continue;
                    }
                    line.replaceAll("￥\\{"+paramName+"\\}",paramValue.toString());
                }
            }
            sb.append(line);
        }
        return sb.toString();
    }

    private Matcher matcher(String str){
        //正则表达式：\\{代表以{开头  \\}代表以}结尾  ()中间 .任意字符  +至少有一个 ？值
        //匹配多次
        Pattern pattern = Pattern.compile("￥\\{(.+?)\\}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        return matcher;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public File getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(File templateFile) {
        this.templateFile = templateFile;
    }
}
