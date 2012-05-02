<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
 <title>Molecular Dynamics - Oligosaccharide Builder - Options</title>
 <%@ include file="/inc/head.inc" %>
 <link rel="stylesheet" type="text/css" href="/css/subModal.css" />
 <script type="text/javascript" src="/js/subModal-common.js"></script>
 <script type="text/javascript" src="/js/subModal.js"></script>
</head>
<body>
<form method="post">
<div id="wrapper">
 <%@ include file="/inc/nav.inc" %>
 <h1>Molecular Dynamics Files</h1>
 <div id="steps">
  <span>Step 1:<br />Build Glycan</span>
  <span class="selected">Step 2:<br />Options</span>
  <span>Step 3:<br />Download Files</span>
 </div>
 <h2>Options</h2>
 <p><strong>Molecule:</strong></p>
 <img class="structure"
      src="/services/drawglycan/${glycanSession.sequence}" />
 <p class="breakword">${glycanSession.sequence}</p>
 <br />
 <div id="mdfiles_options">
  <p><strong>Total Structures Generated:</strong> ${totalStructures} </p>
  <br />
  <ul class="options">
   <c:if test="${showSetAngles}">
    <li>
     Optional: <input type="button" 
                onclick="window.location='/tools/molecular-dynamics/oligosaccharide-builder/set-glycosidic-angles'"
                value="Set Glycosidic Angles" />
     <img src="/img/external-link.jpg" />
    </li>
   </c:if>
   <c:if test="${showChooseRotamers}">
    <li>
     Optional: <input type="button"
                onclick="window.location = '/tools/molecular-dynamics/oligosaccharide-builder/choose-rotamers'"
                value="Choose Rotamers" />
     <img src="/img/external-link.jpg" />
    </li>
   </c:if>
    <li>
     Optional: <input type="button"
                      onclick="showPopWin('/tools/molecular-dynamics/solvation-options', 600, 350);"
                      value="Solvate Structures" />
     <img src="/img/external-link.jpg" />
    </li>
  </ul>
 </div> <!-- mdfiles_options -->
 <br />
 <a class="button" href="build-glycan">&lt; Previous</a>
 <a class="button" href="download-files">Next &gt;</a>
 <br /><br /><br />
 <%@ include file="/inc/footer.inc" %>
</div> <!-- wrapper -->
</form>
</body>
</html>
