<%@page import="tstest.ui.TSTestServlet"%>
<%@page import="tstest.TSTestData"%>
<html>
	<head>
		<%
			TSTestData data= (TSTestData)session.getAttribute(TSTestServlet.DATA);
			String user = "";
			String pwd = "";
			String server = "";
			if (data!=null){
				System.out.println("data!=null ");
				user = data.getLogin()!=null?data.getLogin():"";
				pwd = data.getPassword()!=null?data.getPassword():"";
				server = data.getServer()!=null?data.getServer():"";
			}
		%>
	</head>
	<body>
		<table align="center" style="width:30%"> 
			<tr>
				<td align="center"><h3>TSTest application</h3></td>
			</tr>
			<tr>
				<td><hr/></td>
			</tr>
			<tr>
				<td>
					<div style="text-align:center;color:red">
					<%if (data!=null){%>
						<%if (data.error("login")!=null) out.print(data.error("login") + "<br>");%>
						<%if (data.error("password")!=null) out.print(data.error("password") + "<br>");%>
						<%if (data.error("server")!=null) out.print(data.error("server") + "<br>");%>
					<%}%>
					</div>
				</td>
			</tr>
		</table>

		<form action="login.do" method="post">
			<table align="center" style="width:30%"> 
				<tr>
					<td>User</td>
					<td><input type="text" name="user" style="width:100%" value="<%=user%>"/></td>
				</tr>
				<tr>
					<td>Password</td>
					<td><input type="password" name="password" style="width:100%" value="<%=pwd%>" /></td>
				</tr>
				<tr>
					<td>Server</td>
					<td><input type="text" name="server" style="width:100%" value="<%=server%>" /></td>
				</tr>
				<tr>
					<td colspan="2" align="right"><input type="submit" name="login" value="Next" /></td>
				</tr>
			</table>
		</form>
		<table align="center" style="width:100%">
			<tr><td><iframe src="log.jsp" width="100%" height="300" scrolling="no"></iframe></td></tr>
		</table>
	</body>
</html>