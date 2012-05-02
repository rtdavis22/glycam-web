<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
 <title>PDB File Preprocessor - Unrecognized Residues</title>
 <%@ include file="/inc/head.inc" %>
</head>
<body>
<form method="post">
 <div id="wrapper">
  <%@ include file="/inc/nav.inc" %>
  <h1>PDB File Preprocessor</h1>
  <div id="pdb_options">
    <p class="instruction">The following residues were not recognized.</p>
    <table style="margin:0 auto;">
     <tr>
      <c:if test="${showChainIdCol}">
       <th>Chain Id</th>
      </c:if>
      <th>Index</th>
      <c:if test="${showICodeCol}">
       <th>Insertion Code</th>
      </c:if>
      <th>Name</th>
      <th>Remove</th>
     </tr>
     <c:forEach var="residue" items="${residues}" varStatus="status">
      <tr>
       <c:if test="${showChainIdCol}">
        <td>${residue.residueInfo.chainId}</td>
       </c:if>
       <td>${residue.residueInfo.resNum}</td>
       <c:if test="${showICodeCol}">
        <td>${residue.residueInfo.ICode}</td>
       </c:if>
       <td>${residue.residueInfo.name}</td>
       <td>
        <input type="checkbox" name="residue_${status.count}" disabled="disabled"
                <c:if test="${residue.toBeRemoved}">checked="checked"</c:if> />
       </td>
      </tr>
     </c:forEach>
    </table>
   <br />
   <input type="button" class="button" value="< Back" onclick="window.location='menu';" />
   <input type="submit" class="button" value="Done" />
  </div> <!-- pdb_options -->
  <br /> <br >
  <%@ include file="/inc/footer.inc" %>
 </div> <!-- wrapper -->
</form>
</body>
</html>
