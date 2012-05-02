<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="gp" tagdir="/WEB-INF/tags/glycoprotein_builder" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
 <title>Molecular Dynamics - Glycoprotein Builder - Upload PDB</title>
 <%@ include file="/inc/head.inc" %>
</head>
<body>
<form method="post" enctype="multipart/form-data">
 <div id="wrapper">
  <%@ include file="/inc/nav.inc" %>
  <gp:header step="1" />
  <div id="upload_pdb">
   <p class="instruction">Upload a PDB file.</p>
   <br />
   <input type="file" name="pdb_file" /><br /><br />
   <c:if test="${not empty error}">
    <p class="error">${error}</p><br />
   </c:if>
   <input type="submit" class="button" value="Next >" />
  </div> <%-- upload_pdb --%>
  <br /> <br >
  <%@ include file="/inc/footer.inc" %>
 </div> <%-- wrapper --%>
</form>
</body>
</html>
