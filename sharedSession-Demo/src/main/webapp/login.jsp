<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import = "java.sql.*"%>
<%try{

	    String  uid = request.getParameter("uid");
	    String upwd = request.getParameter("upwd");
	    session.setAttribute("login","ok");
		session.setAttribute("user",uid);
		session.setAttribute("passwd",upwd);
	    session.setMaxInactiveInterval(-1);  
%>
<jsp:forward page="main.jsp"/>
<%
    }

catch (Exception e) {
			e.printStackTrace();
	   }
%>