<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
 <title>PDB File Preprocessor - Unrecognized Atoms</title>
 <%@ include file="/inc/head.inc" %>
</head>
<body>
<form method="post">
 <div id="wrapper">
  <%@ include file="/inc/nav.inc" %>
  <h1>PDB File Preprocessor</h1>
  <div id="pdb_options">
    <p class="instruction">The following atoms were not recognized.</p>
    <table style="margin:0 auto;">
     <tr>
      <th>Index</th>
      <th>Atom Name</th>
      <th>Residue Name</th>
      <c:if test="${showChainIdCol}">
       <th>Chain Id</th>
      </c:if>
      <th>Residue Number</th>
      <c:if test="${showICodeCol}">
       <th>Insertion Code</th>
      </c:if>
     </tr>
     <c:forEach var="atom" items="${atoms}" varStatus="status">
      <tr>
       <td>${atom.serial}</td>
       <td>${atom.name}</td>
       <td>${atom.residueInfo.name}</td>
       <c:if test="${showChainIdCol}">
        <td>${atom.residueInfo.chainId}</td>
       </c:if>
       <td>${atom.residueInfo.resNum}</td>
       <c:if test="${showICodeCol}">
        <td>${atom.residueInfo.iCode}</td>
       </c:if>
      </tr>
     </c:forEach>
    </table>
   <br />
   <input type="button" class="button" value="< Back" onclick="window.location='menu';" />
  </div> <!-- pdb_options -->
  <br /> <br >
  <%@ include file="/inc/footer.inc" %>
 </div> <!-- wrapper -->
</form>
</body>
</html>
