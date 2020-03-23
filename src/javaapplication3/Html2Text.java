/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication3;

import java.io.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;
 
public class Html2Text extends HTMLEditorKit.ParserCallback {
	 StringBuffer s;
 
	 public Html2Text() {}
 
	 public void parse(Reader in) throws IOException {
	   s = new StringBuffer();
	   ParserDelegator delegator = new ParserDelegator();
	   // the third parameter is TRUE to ignore charset directive
	   delegator.parse(in, this, Boolean.TRUE);
	 }
 
	 public void handleText(char[] text, int pos) {
	   s.append(text);
	 }
 
	 public String getText() {
	   return s.toString();
	 }
 
	 public static void main (String[] args) {
	   try {
	     // the HTML to convert
		 //Reader in=new StringReader("string");	
	     FileReader in = new FileReader("java-new.html");
	     Html2Text parser = new Html2Text();
	     parser.parse(in);
	     in.close();
	     System.out.println(parser.getText());
	   }
	   catch (Exception e) {
	     e.printStackTrace();
	   }
	 }
}

