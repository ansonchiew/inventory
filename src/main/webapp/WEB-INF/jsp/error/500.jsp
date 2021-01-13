<%@ page isErrorPage="true" %>
<%@ page import="java.io.*" %>
<% 
  StringWriter errors = new StringWriter();
  exception.printStackTrace(new PrintWriter(errors));  
%>
<p class="header_title">
Error 500: Server Error
</p>

<pre>
<%=errors.toString()%>
</pre>

 