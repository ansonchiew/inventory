<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%> 

<br/>
<ul id="navigation">
  <li id="createInventory"><a href="<c:url value='/inventory?new'/>">Create Inventory</a>
  <li id="editInventory"><a href="<c:url value='/inventory?action=edit'/>">Edit Inventory</a>  
  <li id="browseInventory"><a href="<c:url value='/inventory?action=browse'/>">Browse Inventory</a>
  <li id="deleteInventory"><a href="<c:url value='/inventory?action=delete'/>">Delete Inventory</a>
</ul>