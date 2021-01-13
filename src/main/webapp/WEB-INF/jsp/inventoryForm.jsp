<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>                           
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>                            
<script type="text/javascript" src="<c:url value='../js/jquery-3.5.1.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='../js/urlparam.js'/>"></script>  

<script language="javascript">
  
  $(document).ready(function() {
    var selectCount = 0        
    if (window.location.getParameter("action")) {
      var action = window.location.getParameter("action");
      if (action == "edit") {
       	$("#idRow").show();      
        $("nameInputRow").hide();   
      	$("#nameRow").show();      
        $("categoryInputRow").hide();   
      	$("#categoryRow").show();      
        $("subCategoryInputRow").hide();   
      	$("#subCategoryRow").show();      
        $("#editInventory").addClass("selected");
        
        $('#quantity').focus();
      }
    } else if (window.location.search.substr(1) == "new") {
      $("#nameInputRow").show();   
      $("#nameRow").hide();     
      $("#categoryInputRow").show();   
      $("#categorRow").hide();    
      $("#subCategoryInputRow").show();   
      $("#subCategoryRow").hide();    
      $("#createInventory").addClass("selected");

      $("select#category").change(selectCount++) 
      $("select#subCategory").change(selectCount++) 
    }
     
    $("#inventory input").change(function() {    	
   		$("#inventory").data("changed", true);
	});
        
    $("#save").click(function() {
      if ($("#name").val() == "" || $("#name").val() == null) {        
        alert("Please enter the inventory name.");
        return;
      }
 
      if ($("#quantity").val() == "" || $("#quantity").val() == null) {        
        alert("Please enter the quantity.");
        return;
      }
       
      if ($("#inventory").data("changed") || selectCount>0) {
        if (confirm("Are you sure you want to save the inventory " + $("#name").val() + "?") == true) {	  
	      if (${edit}) {
	        $("#category").val("${inventory.category.name}") 
	        $("#subCategory").val("${inventory.subCategory.name}") 	         
	      }
	      $("#inventory").submit();
	    }  
      } else {
      	alert("There is no changes made.")        
      }
    })   
         
  }) 
  
</script>
 
<div>
<p class="header_title">${title}</p>

<sf:form method="${method}" modelAttribute="inventory">
  <fieldset>  
  <table cellspacing="10">
    <tr id="idRow" style="display: none">    
      <td><label><b>ID:</b></label></td>
      <td>${inventory.id}</td>
    </tr>
    <tr id="nameInputRow" style="display: none">
      <td><label for="name"><b>Name:</b></label></td>
      <td>
 	    <sf:input path="name" type="text" maxlength="128" size="70"/> 	    
 	    <br/>
        <sf:errors path="name" cssClass="error"/>      
 	  </td>      
    </tr>
    <tr id="nameRow" style="display: none">
      <td><label><b>Name:</b></label></td>
      <td>
         ${inventory.name}
      </td>
    </tr>         
    <tr id="categoryInputRow" style="display: none">
 	  <td><label for="category"><b>Category:</b></label></td>
 	  <td>
        <sf:select path="category.name" id="category">
   	    	<sf:option value="" label="--- Select ---"/>
        	<sf:options items="${categoryList}"/>
      	</sf:select>   	
      	<br/>
        <sf:errors path="category.name" cssClass="error"/>
	  </td>
    </tr>
    <tr id="categoryRow" style="display: none">
      <td><label><b>Category:</b></label></td>
      <td>
         ${inventory.category.name}
      </td>
    </tr>
    <tr id="subCategoryInputRow" style="display: none">
 	  <td><label for="subCategory"><b>Sub-category:</b></label></td>
 	  <td>
        <sf:select path="subCategory.name" id="subCategory">
   	    	<sf:option value="" label="--- Select ---"/>
        	<sf:options items="${subCategoryList}"/>
      	</sf:select>   	
      	<br/>
        <sf:errors path="subCategory.name" cssClass="error"/>
	  </td>
    </tr>
    <tr id="subCategoryRow" style="display: none">
      <td><label><b>Sub-category:</b></label></td>
      <td>
         ${inventory.subCategory.name}
      </td>
    </tr>      
    <tr>
      <td valign="top"><label for="quantity"><b>Quantity:</b></label></td>
      <td>
 	    <sf:input path="quantity" type="text" maxlength="128" size="70"/> 	    
 	    <br/>
        <sf:errors path="quantity" cssClass="error"/>      
 	  </td>      
    </tr>
    <tr>
      <td colspan="2">
        <p class="error">${error}</p> 
      </td>
    </tr>       
    <tr>
      <td></td>    
      <td>
      	<a id="save" href="#" class="buttons save">Save</a>       
      </td>
    </tr>
  </table>
  </fieldset>  
</sf:form> 
</div>