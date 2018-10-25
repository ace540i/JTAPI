<%@page import="tstest.ui.TSTestServlet"%>
<%TSTestServlet.Listener listener = (TSTestServlet.Listener)session.getAttribute(TSTestServlet.LISTENER);%>
<html>
	<head>
		<meta http-equiv="refresh" content="5;URL=log.jsp"> <!-- refresh every 5 seconds -->
		<script language="javascript">
			function doLoad(){
				<%if (listener!=null){%>
					if (parent.document.getElementById("callAction")){
						parent.document.getElementById("callAction").value = "<%=listener.isConnected()?"Hangup":"Dial"%>";
					}
				<%}%>
			}
		</script>
	</head>
	<body onLoad="javascript:doLoad();">
		<table align="center" style="width:100%"> 
			<tr>
				<td>
				<textarea id="log" disabled="true"  style="width:100%" rows="17">
				<%if (listener!=null){%>
					<%=listener.getLog().getText()%>
				<%}%>
				</textarea>
				</td>
			</tr>
		</table>
	</body>
</html>