<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="gp" tagdir="/WEB-INF/tags/glycoprotein_builder" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
 <title>Molecular Dynamics - Glycoprotein Builder - Input Glycan</title>
 <%@ include file="/inc/head.inc" %>
 <link rel="stylesheet" type="text/css" href="/css/subModal.css" />
 <script type="text/javascript" src="/js/subModal-common.js"></script>
 <script type="text/javascript" src="/js/subModal.js"></script>
 <script type="text/javascript">
 function return_string(str) {
  document.getElementById("structure").value = str;
 }

 function button_click() {
  showPopWin("/PointAndClickTool.html", 900, 525, return_string);
 }

 function library_click() {
  showPopWin("/oligosaccharide_library/OligosaccharideLibrary.html", 900, 525, return_string);
 }
 </script>
</head>
<body>
<form method="post">
 <div id="wrapper">
  <%@ include file="/inc/nav.inc" %>
  <gp:header step="3" />
  <div id="attach_glycans" class="input_glycan">
   <ul>
    <li>
     <input class="instruction" type="button" onclick="button_click();"
            value="Create a structure with the interactive carbohydrate builder." />
     <img onclick="button_click();" src="/img/external-link.jpg" />
    </li>
    <li>
     <input class="instruction" type="button" onclick="library_click();"
            value="Choose a structure from the oligosaccharide library." />
     <img onclick="library_click();" src="/img/external-link.jpg" />
    </li>
   </ul>
   <p class="instruction">or</p>
   <p class="instruction">Enter a structure in condensed GLYCAM notation in the box below.</p>
   <textarea id="structure" name="structure"></textarea><br />
   <c:if test="${not empty error}">
    <p class="error">${error}</p><br />
   </c:if>
   <input type="button" class="button" value="< Previous"
          onclick="window.location='attach-glycans';" />
   <input type="submit" class="button" value="Next >" />

  </div> <!-- attach_glycans -->
  <br /> <br >
  <%@ include file="/inc/footer.inc" %>
 </div> <!-- wrapper -->
</form>
</body>
</html>
