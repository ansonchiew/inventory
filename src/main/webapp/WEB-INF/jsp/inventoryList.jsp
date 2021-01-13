<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>                           
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>                            
<script type="text/javascript" src="<c:url value='../js/jquery-3.5.1.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='../js/urlparam.js'/>"></script>  

<link rel="stylesheet" type="text/css" href="<c:url value='/resources/style/style.css'/>" />

<script language="javascript">
  
  $(document).ready(function() {        
    if (window.location.getParameter("action")) {          
      var action = window.location.getParameter("action");
      if (action == "browse") {              
      	$("#browseInventory").addClass("selected");
      } else if (action == "edit") {
        $("#editInventory").addClass("selected");
      } else if (action == "delete") {
        $("#deleteInventory").addClass("selected");
      } 
      
	  $("select#inventoryId").change(function() {
      	var id = $("select#inventoryId").val();
      	var newAction = action;
      	if (action == "delete") {
      	  newAction = "browse"
      	}      	
      	window.location.href = "<c:url value='/inventory/'/>" + id + "?action=" + newAction;           
      })      
    }    
  })

</script>

<div>
<p class="header_title">${title}</p>

<table cellpadding="10">
  <tr>
    <th><label for="inventory"><b>Inventory name:</b></label></th>
    <td>      
      <sf:select path="inventory.id" id="inventoryId">      	
        <sf:option value="" label="--- Select ---"/>        
        <sf:options items="${inventoryList}" />        
      </sf:select>
    </td>
  </tr>
</table>
</div>