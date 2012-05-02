<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
 <title>Molecular Dynamics - Oligosaccharide Builder - Set Glycosidic Angles</title>
 <%@ include file="/inc/head.inc" %>
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
  <div id="mdfiles_setangles">
   <h2>Set Glycosidic Angles</h2>
   <p><strong>Molecule:</strong></p>
   <img class="structure" src="/services/drawglycan/${sequence}?edgelabels=on" />
   <p class="breakword">${sequence}</p>
   <br />
   <p class="instruction">Set glycosidic angles (in degrees) or leave blank to use the default value.</p>
   <table>
    <tr>
     <th>Linkage</th>
     <th><a class="tooltip" href="#">&phi;<span>H1-C1-O-CX'<br />C1-C2-O-CX'</span></a></th>
     <th><a class="tooltip" href="#">&psi;<span>C1-O-CX'-HX'<br />C1-O-C6'-C5'</span></a></th>
     <c:if test="${showOmegaColumn}">
      <th><a class="tooltip" href="#">&omega;<span>O-C6'-C5'-O5'</span></a></th>
     </c:if>
    </tr>

   <c:forEach var="linkage" items="${linkages}" begin="2" varStatus="status">
    <tr>
     <td>(${status.count}) ${linkage.name}</td>
     <td>
      <input type="text" name="${status.count + 1}-phi"
             value="${linkage.phiSet?linkage.phiValues[0]:''}" />
     </td>
     <td>
      <input type="text" name="${status.count + 1}-psi"
             value="${linkage.psiSet?linkage.psiValues[0]:''}" />
     </td>
      <c:if test="${linkage.flexibleOmega}">
       <td>
        <input type="text" name="${status.count + 1}-omega"
               value="${linkage.omegaSet?linkage.omegaValues[0]:''}" />
       </td>
      </c:if>
     </td>
    </c:forEach>

   </table>
  <br />
  <input class="button" type="submit" value="Save and continue" />
  </div> <!-- mdfiles_setangles -->
  <br /><br />
  <%@ include file="/inc/footer.inc" %>
 </div> <!-- wrapper -->
</form>
</body>
</html>
