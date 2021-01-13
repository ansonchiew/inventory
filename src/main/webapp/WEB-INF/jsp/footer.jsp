<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 

<jsp:useBean id="date" class="java.util.Date" /> 
<fmt:formatDate value="${date}" pattern="yyyy" var="currentYear" /> 

<div style="border-top: 3px solid #dcdcdc; width: 100%; padding-top: 3px;">	
	<span style="float: left; font-size: 10px; color: #777; padding-right: 1em;">&copy <c:out value="${currentYear}" /> Contineo</span>
</div>	