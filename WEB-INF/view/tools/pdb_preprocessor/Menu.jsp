<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
 <title>PDB File Preprocessor - Upload PDB</title>
 <%@ include file="/inc/head.inc" %>
 <script type="text/javascript">
  function init() {
   <c:if test="${!enableNext}">
    var button = document.getElementById("next");
    button.disabled = "disabled";
   </c:if>
  }
 </script>
</head>
<body onload="init()">
<form method="post" enctype="multipart/form-data">
 <div id="wrapper">
 <div id="pdb_preprocessor">
  <%@ include file="/inc/nav.inc" %>
  <h1>PDB File Preprocessor</h1>
  <div id="menu">
   <p class="instruction">.....</p>
   <br />
   <table style="margin:0 auto;">
   <c:forEach var="section" items="${menu}" varStatus="status">
    <tr style="line-height:21px">
     <td style="min-width:300px;text-align:left;">
       ${section.name}
     </td>
     <td style="text-align:right">${section.summary}</td>
     <td>
      <img
       <c:choose>
        <c:when test="${section.status == 'GOOD'}">
         src="/img/check.png"
        </c:when>
        <c:when test="${section.status == 'NEEDS_ATTENTION'}">
         src="/img/exclam.png"
        </c:when>
        <c:when test="${section.status == 'BAD'}">
         src="/img/ex.png"
        </c:when>
       </c:choose>
       />
      </img>
     </td>
     <td>
      <c:if test="${section.enabled}">
      <input class="button" style="padding:2px;font-size:10px;height:20px;" type="button" value="View/Change" onclick="window.location='${section.url}';" />
      </c:if>
     </td>
    </tr>
   </c:forEach>
   </table>

   <br />
   <input type="button" class="button" value="< Back"
          onclick="window.location='${previousUrl}'" />
   <input id="next" type="button" class="button" value="${submitValue}"
          onclick="window.location='${submitUrl}'" />
          
  </div> <!-- menu -->
  <br /> <br />
  <%@ include file="/inc/footer.inc" %>
 </div> <!-- wrapper -->
</form>
</body>
</html>
