/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication3;

/**
 *
 * @author chu
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.*;

class Frequency {
    Date date;
    int freq;
    public Frequency(){
        date = Calendar.getInstance().getTime();
        this.freq =0;
    }
    public Date getDate(){
        return date;
    }
    
    public void setDate(Date d){
        date =d;
    }
    
    public int getFrequency(){
        return freq;
    }
    
    public void setFrequency(int f){
        freq =f;
    }
}









public class HttpClient {
    
    private LinkedList<String> hrefs;
    private LinkedList<String> processedHrefs;
    private String strURL;
    private HashMap<Character,Integer> words; 
    private String defaultEncoding;
    

    
    
    public HttpClient(String url,String encoding){
        
        hrefs = new LinkedList<>();
        processedHrefs =new LinkedList<>();
        hrefs.push(url);
        processedHrefs.push(url);
        strURL = url;
        words = new HashMap<>();
        defaultEncoding=encoding;
    }
    
    public LinkedList<String>  getHrefs(){
        return hrefs;
    }
    
    public String getURL(){
        return strURL;
    }
    
    public void setURL(String url){
        strURL = url;
    }
    
    private String getHTMLBody(String strHTML){
        /*
        strHTML="d\n" +
            "tytyjgfsz hs\n" +
            "\n" +
            "<body>\n" +
            "<div class=\"feedback_window\" id=\"feedback_window\">\n" +
            "<form id=\"form_2553\" name=\"form_2553\" class=\"common_form\" action=\"//active.163.com/service/form/v1/2553/submit\" method=\"POST\" enctype=\"multipart/form-data\">\n" +
            "\n" +
            "<label <body> for=\"form_2553_ip\">IP地址</label>\n" +
            "</body>\n" +
            "dafhdafhg fhdfhgbczcxvreasyb\n" +
            "ygukhmyukhmcgersdg</boDY>aadf;khsdl";
        */
        
        Pattern p = Pattern.compile("<body>[\\s\\S]*</body>",Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(strHTML);
        if(m.find())
            strHTML =  m.group(); 
        return strHTML;
    }
        private void testReg(){
        
        String str = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<meta charset=\"utf-8\">\n" +
                    "<meta charset=\"gb2312\">\n" +
                    "<meta charset=\"iso-8859-1\">\n"
                ;
        
        
        String reg_charset = "(?<=charset=\")[\\w\\-\\d]+";  
        String encoding = "";
        Pattern p = Pattern.compile(reg_charset);  
        Matcher m = p.matcher(str);  
        
        while (m.find()) {
            encoding = m.group();
            //encoding=encoding.replaceAll(" ", "");
            //encoding = encoding.substring(8, encoding.length()-1);
            System.err.println("encoding string:" + encoding);                
        } 
    }
    
        private void Sumsup(String txt){
            
        for(int i=0;i<txt.length();i++){
            Character c =txt.charAt(i);
            if(words.containsKey(c)){
                words.put(c,1+words.get(c));
            }else{
                words.put(c, 1);
            }
        }
         Set<Character> keys=words.keySet();
         keys.forEach((key) -> {
             int value=words.get(key);
             System.out.println("key:"+key+"  and  value:"+value);
        });
        
    }
    
    public  String doGet(String encoding) {
        
        System.err.println("\n********\n***********\n************\n********");
        System.err.println("begin to process addr:" + strURL + " with encoding:" + encoding);
        
        if(hrefs.isEmpty())
            return "";
        
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        String result = null;// 返回结果字符串
        try {
            // 创建远程url连接对象
            URL url = new URL(strURL);
            // 通过远程url连接对象打开一个连接，强转成httpURLConnection类
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接方式：get
            connection.setRequestMethod("GET");
            // 设置连接主机服务器的超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：60000毫秒
            connection.setReadTimeout(60000);
            // 发送请求
            connection.connect();
            
            // 通过connection连接，获取输入流
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                // 封装输入流is，并指定字符集
                br = new BufferedReader(new InputStreamReader(is, encoding));
                
                // 存放数据
                StringBuilder sbf = new StringBuilder();
                String temp = null;                
                while (null != (temp = br.readLine())) {
                    sbf.append(temp);
                    sbf.append("\r\n");
                }
                result = sbf.toString();                
                String extactedEncoding = getEncoding(result);
                if(!extactedEncoding.equalsIgnoreCase(encoding)){   ////////
                    if(extactedEncoding.isEmpty())
                        {
                            extactedEncoding = this.defaultEncoding;
                            System.err.println("in doGet() :empty encoding for addr:" + strURL + " set encoding to" + extactedEncoding);
                        }
                    result  = doGet2(extactedEncoding);    
                }
                result = Html2Text(result,extactedEncoding);
                Sumsup(result);    
            }
            
        } catch (MalformedURLException e) {
            System.err.println("Exception in file: " +
                Thread.currentThread().getStackTrace()[1].getFileName()+ 
                " in line: " + Thread.currentThread().getStackTrace()[1].getLineNumber());
            //e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IOException e");
            //e.printStackTrace();
        } finally {
            if(hrefs.contains(this.strURL))
                hrefs.remove(this.strURL);
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.err.println("IOException e");
                    //e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    System.err.println("IOException e");
                }
            }
            connection.disconnect();// 关闭远程连接
        }
                
        while(!hrefs.isEmpty()){
            System.err.println("int Html2Text():the working addrs is:" + hrefs.size() + 
                                "\nthe total encountered addrs is:" + processedHrefs.size());
            setURL(hrefs.getFirst());
            doGet(defaultEncoding);
        }
        System.err.println("in doget() ending the maighty loop ");
               
        return result;
    }
    
    public String Html2Text(String inputString,String encoding) {
                String strHref;                        
                
		String htmlStr = inputString; // 含html标签的字符串
                if(htmlStr.isEmpty()){
                    hrefs.remove(strURL);
                    return "";
                }
		String textStr = "";               
                htmlStr = getHTMLBody(htmlStr);
                java.util.regex.Pattern p_href;
                java.util.regex.Matcher m_href;
                
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;
		try {
                    String regEx_comments="(<!--)(.*)-->";
                    Pattern pComments = Pattern.compile(regEx_comments);
                    Matcher mComments = pComments.matcher(htmlStr);                    
                    htmlStr = mComments.replaceAll("");  
                    
                    String regEx_href = "https{0,}://(.*?)[\'\"]";                    
                    String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
                    String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
                    String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
                    p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
                    m_script = p_script.matcher(htmlStr);
                    htmlStr = m_script.replaceAll(""); // 过滤script标签
                    p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
                    m_style = p_style.matcher(htmlStr); 
                    htmlStr = m_style.replaceAll(""); // 过滤style标签  
                    p_href = Pattern.compile(regEx_href,Pattern.CASE_INSENSITIVE);
                    m_href = p_href.matcher(htmlStr);
                    
                    while(m_href.find()){
                        strHref = m_href.group();
                        strHref = strHref.substring(0, strHref.length()-1);
                        if(strHref.endsWith(".html") || strHref.endsWith(".htm")){
                            if(!processedHrefs.contains(strHref)){
                                hrefs.push(strHref); 
                                processedHrefs.push(strHref);
                            } 
                        }
                    }
                    p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE); 
                    m_html = p_html.matcher(htmlStr);
                    htmlStr = m_html.replaceAll(""); // 过滤html标签
                    textStr = htmlStr;
                } catch (Exception e) 
                {
                    System.err.println("error in Html2Text(): " + e.toString()); 
                }
                    //剔除空格行
                textStr=textStr.replaceAll("[ |\\t]", ""); 
                //textStr=textStr.replaceAll("[a-zA-Z]", ""); 
                textStr=textStr.replaceAll("(?m)^\\s*$(\\n|\\r\\n)", "");               
                
            System.err.println("in Htmel2Text():current addr:" +getURL() + "with encoding:" + encoding);         
            
            //System.err.println(textStr);  
            
            hrefs.remove(strURL);

            return textStr;// 返回文本字符串
            }
     
    public static String StripHT(String strHtml) {
	String txtcontent = strHtml.replaceAll("</?[^>]+>", ""); //剔出<html>的标签  
        txtcontent = txtcontent.replaceAll("<a>\\s*|\t|\r|\n</a>", "");//去除字符串中的空格,回车,换行符,制表符  
        return txtcontent;
    }
    
    
    private String getEncoding(String str){
        if(str.isBlank() || str.isEmpty()){            
            System.err.println("in getEncoding() :Empty contents");
            return str;
        }
        String reg_charset = "(?<=charset=\"?)[\\w\\-\\d]+";
        String encoding = "";
        Pattern p = Pattern.compile(reg_charset);  
        Matcher m = p.matcher(str);  
        
        if (m.find()) {
            encoding = m.group();                            
        } 
        
        System.err.println("in getEncoding():text is not empty and ENCODING :" + encoding);
        return encoding;
        
    }

    private String doGet2(String encoding) {
        System.err.println("entering doGet2 with encoding:" + encoding);
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        String result = null;// 返回结果字符串
        try {
            // 创建远程url连接对象
            URL url = new URL(strURL);
            // 通过远程url连接对象打开一个连接，强转成httpURLConnection类
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接方式：get
            connection.setRequestMethod("GET");
            // 设置连接主机服务器的超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：60000毫秒
            connection.setReadTimeout(60000);
            // 发送请求
            connection.connect();
            
            // 通过connection连接，获取输入流
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                // 封装输入流is，并指定字符集
                br = new BufferedReader(new InputStreamReader(is, encoding));
                
                // 存放数据
                StringBuilder sbf = new StringBuilder();
                String temp = null;
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append("\r\n");
                }
                result = sbf.toString();              
            }
        } catch (MalformedURLException e) {
            System.err.println("Exception in file: " +
            Thread.currentThread().getStackTrace()[1].getFileName()+ 
            " in line: " + Thread.currentThread().getStackTrace()[1].getLineNumber());
            //e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Exception in file: " +
            Thread.currentThread().getStackTrace()[1].getFileName()+ 
            " in line: " + Thread.currentThread().getStackTrace()[1].getLineNumber());
            //e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.err.println("Exception in file: " +
                     Thread.currentThread().getStackTrace()[1].getFileName()+ 
                     " in line: " + Thread.currentThread().getStackTrace()[1].getLineNumber());
                    //e.printStackTrace();
                }
            }

            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                        System.err.println("Exception in file: " +
                        Thread.currentThread().getStackTrace()[1].getFileName()+ 
                        " in line: " + Thread.currentThread().getStackTrace()[1].getLineNumber()); 
                //e.printStackTrace();
                }
            }

            connection.disconnect();// 关闭远程连接
        }
        
        result = Html2Text(result,encoding);
        return result;
    }
}




    
