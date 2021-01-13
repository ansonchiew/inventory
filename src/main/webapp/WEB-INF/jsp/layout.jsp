<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title><tiles:insertAttribute name="title" ignore="true" /></title>
	</head>
	
	<body>
		<table border="0" cellpadding="0" cellspacing="0" align="center" width="100%">
			<tr>
				<td colspan="2" width="100%">
					<tiles:insertAttribute name="header" />
				</td>
			</tr>
			<tr>
				<td width="200" height="800" valign="top">
					<tiles:insertAttribute name="menu" />
				</td>
				<td width="1500" align="left" valign="top" style="padding:15px;">
					<tiles:insertAttribute name="body" />
				</td>
			</tr>
			<tr>
				<td height="10" colspan="2">
					<tiles:insertAttribute name="footer" />
				</td>
			</tr>
		</table>
	</body>
</html>