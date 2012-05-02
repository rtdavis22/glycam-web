<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="gp" tagdir="/WEB-INF/tags/glycoprotein_builder" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
 <title>Molecular Dynamics - Glycoprotein Builder - Glycan Options</title>
 <%@ include file="/inc/head.inc" %>
 <link rel="stylesheet" type="text/css" href="/css/subModal.css" />
 <script type="text/javascript" src="/js/subModal-common.js"></script>
 <script type="text/javascript" src="/js/subModal.js"></script>
</head>
<body>
<form method="post">
<div id="wrapper">
 <%@ include file="/inc/nav.inc" %>
 <gp:header step="3" />
 <h2>Options</h2>
 <p><strong>Molecule:</strong></p>
 <img class="structure" src="/services/drawglycan/${glycanSession.sequence}" />
 <p class="breakword">${glycanSession.sequence}</p>
 <br />
 <div id="mdfiles_options">
  <br />
  <ul class="options">
   <c:if test="${showSetAngles}">
    <li>
     Optional: <input type="button" 
                onclick="window.location='/tools/molecular-dynamics/glycoprotein-builder/set-angles'"
                value="Set Glycosidic Angles" />
     <img src="/img/external-link.jpg" />
    </li>
   </c:if>
   <c:if test="${showChooseRotamers}">
    <li>
     Optional: <input type="button"
                onclick="window.location='/tools/molecular-dynamics/glycoprotein-builder/choose-rotamers'"
                value="Choose Rotamers" />
     <img src="/img/external-link.jpg" />
    </li>
   </c:if>
  </ul>
 </div> <!-- mdfiles_options -->
 <br />
 <a class="button" href="input-glycan">&lt; Previous</a>
 <a class="button" href="choose-locations">Next &gt;</a>
 <br /><br /><br />
 <%@ include file="/inc/footer.inc" %>
</div> <!-- wrapper -->
</form>
</body>
</html>
