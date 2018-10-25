<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@page import="tstest.PhoneUserProcessor"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
 <script type="text/javascript"> 
//     alert("phoneShutDown");
    console.log("this is activedir.jsp");
 </script>

<%  
 		System.err.println("active"); 
//  		PhoneUserProcessor obj = (PhoneUserProcessor)request.getSession().getAttribute("object");
 		PhoneUserProcessor obj = new PhoneUserProcessor();
 		 
 		if (obj != null){
            obj.process(request, response, "getUserPhone") ;
 			
 		}
 		
 		%> 
</body>
</html>