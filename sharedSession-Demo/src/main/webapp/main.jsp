<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <title>~WELCOME TO MY HOMEPAGE~</title>
    </head>
    <body>
    <center>
   
    ~WELCOME TO MY HOMEPAGE~
	
	<%
    String strTmp="";
try{
strTmp =java.net.InetAddress.getLocalHost().getHostAddress();
}
catch(Exception e){
	System.out.println(e);
}

String port = 8080 +"";
out.println(strTmp + " "+port + " "+ new java.util.Date());
%>

<%
String user="null";
String passwd = "null";
try{
user = ""+session.getAttribute("user");
passwd = ""+session.getAttribute("passwd");

}
catch(Exception e){
	System.out.println(e);
}

out.println("user : " + user +" passwd: "+ passwd + " "+ new java.util.Date());
%>
    </center>
    </body>
</html>