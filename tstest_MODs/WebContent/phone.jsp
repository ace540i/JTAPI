<%@page import="tstest.ui.PhoneServlet"%>
<%@page import="tstest.PhoneCallData"%>
<%--  <jsp:include page="/login.jsp" /> --%>
<html>
	<head>
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
<!-- 	<form action="login.do" name="login" method="post"> -->
<!-- 	<input type="hidden" name="user" value="OAISYSTSAPI""/> -->
<!-- 	<input type="hidden" name="password"  value="Avaya1234567!" /> -->
<!-- 	<input type="hidden" name="server"  value="10.13.18.20" /> -->
<!-- 		</form> -->
<!--  				<script type="text/javascript">   -->
<!--       				document.login.submit(); -->
		 
<!--   		</script>    -->
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
		<form action="call.do"  name="login" method="post">
			<input type="hidden" name="user" value="OAISYSTSAPI"/>
			<input type="hidden" name="password"  value="Avaya1234567!" />
			<input type="hidden" name="server"  value="10.13.18.20" />
			<table align="center" style="width:30%"> 
				<tr>
					<td>Service</td>
					<td>
					
						<%String[] services = (String[])session.getAttribute(PhoneServlet.SERVICES);
// 						   services[1] ="AVAYA#MILSWLINK#CSTA#CAAESSVR01"; 
						   %>
						<select name="service"     style="width:100%">
<%-- 							<%if (services!=null){%> --%>
<%-- 								<%for (int i=0;i<services.length;i++){%> --%>
<%-- 									<%="<option name='" + services[i] + "'>" + services[i] + "</option>" + (services[i].equals(service)?"selected":"")%> --%>
<%-- 								<%}%> --%>
<%-- 							<%} else --%>
<!-- // 							    { -->
		   		        <option name='AVAYA#MILSWLINK#CSTA#CAAESSVR01'>AVAYA#MILSWLINK#CSTA#CAAESSVR01</option>
							<% service = "AVAYA#MILSWLINK#CSTA#CAAESSVR01"; %>
<!-- 								System.err.println("WARNING:services are null.");}%> -->
						</select>
					</td>
				</tr>
				<tr>
					<td>Caller</td>
					
					<td><input type="text" name="caller" value="5246"  style="width:100%" value="<%=caller%>" /></td>
				</tr>
				<tr>
					<td>Callee</td>
					<td><input type="text" name="callee"  value="5611" style="width:100%"value="<%=callee%>" /></td>
				</tr>
				<tr>
					<td colspan="2" align="right"><input type="submit" id="callAction" name="callAction" value="Dial" /></td>
				</tr>
			</table>
			<div onClick="makeCall();" >call 5611</div>
		</form>
		<script type="text/javascript">    
		function makeCall() {
 			document.login.submit();
		}
 	 	</script> 
<!-- 		<table align="center" style="width:100%"> -->
<!-- 			<tr><td><iframe src="log.jsp" width="100%" height="800" scrolling="yes"></iframe></td></tr> -->
<!-- 		</table> -->
	</body>
</html>