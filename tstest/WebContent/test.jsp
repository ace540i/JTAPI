<%@page import="tstest.ui.TSTestServlet"%>
<%@page import="tstest.TSTestData"%>
<html>
	<head>
		<%
			TSTestData data= (TSTestData)session.getAttribute(TSTestServlet.DATA);
			String caller = "";
			String callee = "";
			String service = "";
			if (data!=null){
				caller = data.getCaller()!=null?data.getCaller():"";
				callee = data.getCallee()!=null?data.getCallee():"";
				service = data.getService()!=null?data.getService():"";
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
						<%if (data.error("service")!=null) out.print(data.error("service") + "<br>");%>
						<%if (data.error("caller")!=null) out.print(data.error("caller") + "<br>");%>
						<%if (data.error("callee")!=null) out.print(data.error("callee") + "<br>");%>
					<%}%>
					</div>
				</td>
			</tr>
		</table>
		<form action="call.do" method="post">
			<table align="center" style="width:30%"> 
				<tr>
					<td>Service</td>
					<td>
						<%String[] services = (String[])session.getAttribute(TSTestServlet.SERVICES);%>
						<select name="service"  style="width:100%">
							<%if (services!=null){%>
								<%for (int i=0;i<services.length;i++){%>
									<%="<option name='" + services[i] + "'>" + services[i] + "</option>" + (services[i].equals(service)?"selected":"")%>
								<%}%>
							<%} else {System.err.println("WARNING:services are null.");}%>
						</select>
					</td>
				</tr>
				<tr>
					<td>Caller</td>
					<td><input type="text" name="caller" style="width:100%" value="<%=caller%>" /></td>
				</tr>
				<tr>
					<td>Callee</td>
					<td><input type="text" name="callee" style="width:100%"value="<%=callee%>" /></td>
				</tr>
				<tr>
					<td colspan="2" align="right"><input type="submit" id="callAction" name="callAction" value="Dial" /></td>
				</tr>
			</table>
		</form>
		<table align="center" style="width:100%">
			<tr><td><iframe src="log.jsp" width="100%" height="300" scrolling="no"></iframe></td></tr>
		</table>
	</body>
</html>