<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
 <title>Molecular Dynamics Files - Solvation Options</title>
 <%@ include file="/inc/head.inc" %>
 <script type="text/javascript">
  function disable_form() {
    var elements = document.getElementsByName("shape");
    for (var i = 0; i < elements.length; i++) {
        elements[i].disabled = true;
    }
    document.getElementById("buffer").disabled = true;
    document.getElementById("closeness").disabled = true;
  }
  function enable_form() {
    var elements = document.getElementsByName("shape");
    for (var i = 0; i < elements.length; i++) {
        elements[i].disabled = false;
    }
    document.getElementById("buffer").disabled = false;
    document.getElementById("closeness").disabled = false;
  }
 </script>
</head>
<body>
<c:if test="${timeToClose}">
 <script type="text/javascript"> window.top.hidePopWin(true); </script>
</c:if>
<form method="post">
 <div id="solvation_options">
  <h2>Solvation Options</h2><br />
  <p>
   Solvate Structures:<br />
   <input type="radio" name="solvate" onclick="enable_form();" value="Yes" ${solvate?"checked":""} /> Yes
   <input type="radio" name="solvate" onclick="disable_form();" value="No" ${solvate?"":"checked"} /> No
  </p>
  <br />
  <p>
   Choose the shape of the solvent box:<br />
   <input type="radio" name="shape" value="Rectangular" ${(shape == "Rectangular")?"checked":""} /> Rectangular
   <input type="radio" name="shape" value="Cubic" ${(shape == "Cubic")?"checked":""}  /> Cubic
  </p>
  <br />
  <p>
   Enter the size of the solvent buffer in Angstroms:<br />
   <input id="buffer" type="text" name="buffer" value="${buffer}" />
  </p>
  <br />
  <p>
   Enter the minimum distance between the center of a solute atom and a solvent atom:<br />
   <input id="closeness" type="text" name="closeness" value="${closeness}" />
  </p>
  <br />
  <p class="error">${error}</p>
  <input class="button" type="submit" value="Save and continue" />
 </div> <!-- wrapper -->
 <c:if test="${solvate == false}">
  <script type="text/javascript">disable_form();</script>
 </c:if>
</form>
</body>
</html>
