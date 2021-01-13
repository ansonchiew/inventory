<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<script type="text/javascript" src="<c:url value='../js/jquery-3.5.1.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='../js/urlparam.js'/>"></script>  

<script language="javascript">
  
  $(document).ready(function() {
    $("#selectRow").show();      
    $("#buttonRow").show();
    $("#browseInventory").addClass("selected");    
    
    $("#edit").click(function() {      
      var id = "${inventory.id}"
  	  window.location.href = "<c:url value='/inventory/'/>" + id + "?action=edit";
  	})
  	
  	$("#delete").click(function() {
      if (confirm("Are you sure you want to delete the inventory ${inventory.name}?") == true) {
        var id = "${inventory.id}"
  	    window.location.href = "<c:url value='/inventory/'/>" + id + "?action=delete";
      } 
  	})
  	
  	$("select#inventoryId").change(function() {
      var id = $("select#inventoryId").val();
      window.location.href = "<c:url value='/inventory/'/>" + id + "?action=browse";           
    })	
  	   
  });
</script>

<div>
  <p class="header_title">${title}</p>
 
  <table cellspacing="10">
    <tr id="selectRow" style="display: none">
      <td><b>Name:</b></th>
	  <td>      
	    <sf:select path="inventory.id" id="inventoryId">      	
	      <sf:option value="" label="--- Select ---"/>        
	      <sf:options items="${inventoryList}" />        
	    </sf:select>
	  </td>
	</tr>
	<tr>
      <td><b>ID:</b></td>
      <td>${inventory.id}</td>
    </tr>    
    <tr>
      <td><b>Category:</b></td>
      <td>
          ${inventory.category.name}
      </td>
    </tr>      
    <tr>
      <td><b>Sub-category:</b></td>
      <td>
          ${inventory.subCategory.name}
      </td>
    </tr>
    <tr>
      <td><b>Quantity:</b></td>
      <td>
          ${inventory.quantity}
      </td>
    </tr>    
    <tr id="buttonRow" style="display: none">
      <td></td>    
      <td>
		<a id="edit" href="#" class="buttons edit">Edit</a>
		<a id="delete" href="#" class="buttons delete">Delete</a>
      </td>
    </tr>  
  </table>

</<div>