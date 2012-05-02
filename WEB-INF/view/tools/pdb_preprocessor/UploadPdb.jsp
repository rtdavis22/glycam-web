<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
 <title>PDB File Preprocessor - Upload PDB</title>
 <%@ include file="/inc/head.inc" %>
</head>
<body>
<form method="post" enctype="multipart/form-data">
 <div id="wrapper">
  <%@ include file="/inc/nav.inc" %>
  <h1>PDB File Preprocessor</h1>
  <div id="steps">
   <span class="selected">Step 1:<br />Upload PDB File</span>
   <span>Step 2:<br />Options</span>
   <span>Step 3:<br />Download Files</span>
  </div>
  <div id="upload_pdb">
   <p class="instruction">Upload a PDB file.</p>
   <br />
   <input type="file" name="pdb_file" /><br /><br />
   <c:if test="${not empty error}">
    <p class="error">${error}</p><br />
   </c:if>
   <input type="submit" class="button" value="Next >" />
  </div> <!-- upload_pdb -->
  <br /> <br >
  <%@ include file="/inc/footer.inc" %>
 </div> <!-- wrapper -->
</form>
</body>
</html>
