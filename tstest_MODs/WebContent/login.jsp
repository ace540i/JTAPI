<%@page import="tstest.ui.PhoneServlet"%>
<%@page import="tstest.PhoneCallData"%>
<html> 
	<head>
<!-- 		<script -->
<!-- 		  src="https://code.jquery.com/jquery-3.1.1.min.js" -->
<!-- 		  integrity="sha256-hVVnYaiADRTO2PzUGmuLJr8BLUSjGIZsDYGmIJLv2b8=" -->
<!-- 		  crossorigin="anonymous"></script> -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min.js"></script>
		<%
		PhoneCallData data= (PhoneCallData)session.getAttribute(PhoneServlet.DATA);
			String user = "";
			String pwd = "";
			String server = "";
			if (data!=null){
				System.out.println("data!=null ");
				user = data.getLogin()!=null?data.getLogin():"";
				pwd = data.getPassword()!=null?data.getPassword():"";
				server = data.getServer()!=null?data.getServer():"";
			}
	 		System.out.println("user "+user);
	 		System.out.println("pwd "+pwd);
	 		System.out.println("server "+server);
 		
		%>
	</head>
	<body>
<!-- 		<table align="center" style="width:30%">  -->
<!-- 			<tr> -->
<!-- 				<td align="center"><h3>TSTest application</h3></td> -->
<!-- 			</tr> -->
<!-- 			<tr> -->
<!-- 				<td><hr/></td> -->
<!-- 			</tr> -->
<!-- 			<tr> -->
<!-- 				<td> -->
<!-- 					<div style="text-align:center;color:red"> -->
<%-- 					<%if (data!=null){%> --%>
<%-- 						<%if (data.error("login")!=null) out.print(data.error("login") + "<br>");%> --%>
<%-- 						<%if (data.error("password")!=null) out.print(data.error("password") + "<br>");%> --%>
<%-- 						<%if (data.error("server")!=null) out.print(data.error("server") + "<br>");%> --%>
<%-- 					<%}%> --%>
<!-- 					</div> -->
<!-- 				</td> -->
<!-- 			</tr> -->
<!-- 		</table> -->

		<form action="login.do" name="myform" method="post">
			<table align="center" style="width:30%"> 
				<tr>
 
					<td><input type="hidden" value="OAISYSTSAPI""/></td>
				</tr>
				<tr>
 
					<td><input type="hidden"   value="Avaya1234567!" /></td>
				</tr>
				<tr>
 
					<td><input type="hidden"   value="10.13.18.20" /></td>
				</tr>
<!-- 				<tr> -->
<!-- 					<td colspan="2" align="right"><input type="submit" name="login" value="Next" /></td> -->
<!-- 				</tr> -->
			</table>
		</form>
		<script type="text/javascript">
	
		$(document).ready(function() {
// 			alert("login.jsp");
	
//             $('#call').click(function ()
//             {
	
 
// // 								    type : "POST",
// //                   	            url : "phoneShutDown.jsp"							                            	    
// // 								 });
	
	
                $.ajax({
                	
                    type: "post",
                    url: "activedir.jsp", //this is my servlet
//                     data: "input=" +$('#ip').val()+"&output="+$('#op').val(),
				//	success:"activedir.jsp"
                     success: function(){    alert("login.jsp"); }  
//                             $('#output').append(msg);
//                      }
                });

        });
		
		
		
		
   			//	document.myform.submit();		 
		</script>
<!-- 		<table align="center" style="width:100%"> -->
<!-- 			<tr><td><iframe src="log.jsp" width="100%" height="300" scrolling="no"></iframe></td></tr> -->
<!-- 		</table> -->
	</body>
</html>