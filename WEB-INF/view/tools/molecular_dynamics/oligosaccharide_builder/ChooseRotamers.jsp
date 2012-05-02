<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
 <title>Molecular Dynamics - Oligosaccharide Builder - Choose Rotamers</title>
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
  <div id="mdfiles_rotamers">
   <h2>Choose Rotamers</h2>
   <p><strong>Molecule:</strong></p>
   <img class="structure" src="/services/drawglycan/${sequence}?edgelabels=on" />
   <p class="breakword">${sequence}</p>
   <br />
   <p class="instruction">Choose the glycosidic rotamers you want to build.</p>
   <table>
    <tr>
     <th rowspan="2">Linkage</th>
     <c:if test="${showPhiColumn}">
      <th colspan="3">
       <a class="tooltip" href="#">
        &phi;
        <span>H1-C1-O-CX'<br />C1-C2-O-CX'<br />t: 180&deg;<br />g: 60&deg;<br />-g: -60&deg;</span>
       </a>
      </th>
     </c:if>
     <c:if test="${showOmegaColumn}">
      <th colspan="3">
       <a class="tooltip" href="#">
        &omega;
        <span>O-C6'-C5'-O5'<br />gg: 180&deg;<br />gt: -60&deg;<br />tg: 60&deg;</span>
       </a>
      </th>
     </c:if>
    </tr>
    <tr>
     <c:if test="${showPhiColumn}">
      <td>t</td><td>g</td><td>-g</td>
     </c:if>
     <c:if test="${showOmegaColumn}">
      <td>gg</td><td>gt</td><td>tg</td>
     </c:if>
    </tr>

    <c:forEach var="linkage" items="${linkages}" begin="3" varStatus="status">
     <c:if test="${linkage.flexiblePhi || linkage.flexibleOmega}">
      <tr>
       <td>(${status.count + 1}) ${linkage.name}</td>
       <c:if test="${showPhiColumn}">

        <c:choose>
         <c:when test="${linkage.flexiblePhi}">

          <c:set var="tChecked" value="no" scope="page" />
          <c:set var="gChecked" value="no" scope="page" />
          <c:set var="minusGChecked" value="no" scope="page" />
          <c:forEach var="value" items="${linkage.phiValues}">
           <c:choose>
            <c:when test="${value == 180.0}"><c:set var="tChecked" value="yes" /></c:when>
            <c:when test="${value == 60.0}"><c:set var="gChecked" value="yes" /></c:when>
            <c:when test="${value == -60.0}"><c:set var="minusGChecked" value="yes" /></c:when>
           </c:choose>
          </c:forEach>
          <td>
           <input type="checkbox" name="${status.count + 2}" value="t"
                  <c:if test="${tChecked == 'yes'}">checked</c:if>
                  <c:if test="${linkage.phiSet}">disabled</c:if> />
          </td>
          <td>
           <input type="checkbox" name="${status.count + 2}" value="g"
                  <c:if test="${gChecked == 'yes'}">checked</c:if>
                  <c:if test="${linkage.phiSet}">disabled</c:if> />
          </td>
          <td>
           <input type="checkbox" name="${status.count + 2}" value="-g"
                  <c:if test="${minusGChecked == 'yes'}">checked</c:if>
                  <c:if test="${linkage.phiSet}">disabled</c:if> />
          </td>
         </c:when>
         <c:otherwise>
          <td></td><td></td><td></td>
         </c:otherwise>
        </c:choose>

       </c:if> <%-- showPhiColumn --%>

       <c:if test="${showOmegaColumn}">

        <c:choose>
         <c:when test="${linkage.flexibleOmega}">

          <c:set var="ggChecked" value="no" scope="page" />
          <c:set var="gtChecked" value="no" scope="page" />
          <c:set var="tgChecked" value="no" scope="page" />
          <c:forEach var="value" items="${linkage.omegaValues}">
           <c:choose>
            <c:when test="${value == 180.0}"><c:set var="ggChecked" value="yes" /></c:when>
            <c:when test="${value == -60.0}"><c:set var="gtChecked" value="yes" /></c:when>
            <c:when test="${value == 60.0}"><c:set var="tgChecked" value="yes" /></c:when>
           </c:choose>
          </c:forEach>
          <td>
           <input type="checkbox" name="${status.count + 2}" value="gg"
                  <c:if test="${ggChecked == 'yes'}">checked</c:if>
                  <c:if test="${linkage.omegaSet}">disabled</c:if> />
          </td>
          <td>
           <input type="checkbox" name="${status.count + 2}" value="gt"
                  <c:if test="${gtChecked == 'yes'}">checked</c:if>
                  <c:if test="${linkage.omegaSet}">disabled</c:if> />
          </td>
          <td>
           <input type="checkbox" name="${status.count + 2}" value="tg"
                  <c:if test="${tgChecked == 'yes'}">checked</c:if>
                  <c:if test="${linkage.omegaSet}">disabled</c:if> />
          </td>
         </c:when>
         <c:otherwise>
          <td></td><td></td><td></td>
         </c:otherwise>
        </c:choose>

       </c:if> <%-- showOmegaColumn --%>

      </tr>
     </c:if>
    </c:forEach>

   </table>
  <br />
  <input class="button" type="submit" value="Save and continue" />
 </div> <!-- mdfiles_rotamers -->
 <br /> <br />
 <%@ include file="/inc/footer.inc" %>
 </div> <!-- wrapper -->
</form>
</body>
</html>
