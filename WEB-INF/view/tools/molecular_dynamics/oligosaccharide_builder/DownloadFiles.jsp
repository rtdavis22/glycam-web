<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
 <title>Molecular Dynamics Files - Download Files</title>
 <%@ include file="/inc/head.inc" %>
 <link rel="stylesheet" type="text/css" href="/css/subModal.css" />
 <script type="text/javascript" src="/js/subModal-common.js">
 </script>
 <script type="text/javascript" src="/js/subModal.js"></script>
 <script type="text/javascript">
 function button_click(number) {
  var file = "/userdata/tools/mdfiles/${uid}/" + number + ".pdb";
  file += "&width=379&height=509";
  showPopWin("/jmol.jsp?pdbfile=" + file, 400, 525);
 }
 </script>
</head>
<body>
<form method="post">
<input id="download_type" type="hidden" name="download_type" value="all" />
<input type="hidden" name="uid" value="${uid}" /> 

<div id="wrapper">
 <%@ include file="/inc/nav.inc" %>
 <h1>Molecular Dynamics Files</h1>
 <div id="steps">
  <span>Step 1:<br />Build Glycan</span>
  <span>Step 2:<br />Options</span>
  <span class="selected">Step 3:<br />Download Files</span>
 </div>
 <p><strong>Molecule:</strong></p>
 <c:choose>
  <c:when test="${totalStructures > 1}">
   <img class="structure" src="/services/drawglycan/${glycanSession.sequence}?edgelabels=on" />
  </c:when>
  <c:otherwise>
   <img class="structure" src="/services/drawglycan/${glycanSession.sequence}" />
  </c:otherwise>
 </c:choose>
 <p class="breakword">${glycanSession.sequence}</p>
 <br />
 <div id="mdfiles_download">
  <p><strong>Total Structures Generated:</strong> ${totalStructures} </p>
  <br />
  <p class="instruction">Download PDB and AMBER topology and restart files.</p><br />
  <p class="instruction">File Type: 
   <input name="filetype" type="radio" value="tar" checked /> tar.gz
   <input name="filetype" type="radio" value="zip" /> zip
  </p>
  <br />
  <input class="button" name="submit" type="submit" value="Download All Structures"
         onclick="document.getElementById('download_type').value='all';this.form.submit();"/>
  <input class="button" name="submit" type="submit" value="Download Selected Structures"
         onclick="document.getElementById('download_type').value='selected';this.form.submit();"/>
  <br /><br />
  <h2>Structures Generated</h2>
  <table>
   <tr>
    <th>Structure</th>
    <c:if test="${numVaryingLinkages > 0}">
     <th>Linkage</th>
    </c:if>
    <c:if test="${showPhiColumn}">
     <th class="anglecolumn">
      <a class="tooltip" href="#">&phi;<span>H1-C1-O-CX'<br />C1-C2-O-CX'<br />
      t: 180&deg;<br />g: 60&deg;<br />-g: -60&deg;</span></a>
     </th>
    </c:if>
    <c:if test="${showOmegaColumn}">
     <th class="anglecolumn">
      <a class="tooltip" href="#">&omega;<span>O-C6'-C5'-O5'<br />
      gg: 180&deg;<br />gt: -60&deg;<br />tg: 60&deg;</span></a>
     </th>
    </c:if>
    <th>Minimized<br />Energy<br /><span style="font-size:12px">(kcal/mol)</span></th>
    <%--<th>Boltzmann<br />Probability</th>--%>
    <th></th>
    <th>PDB</th>
   </tr>
   <c:forEach var="structure" items="${resultStructures}" varStatus="status">
    <tr class="firstrow">
     <td rowspan="${numVaryingLinkages + 1}">
      ${status.count}<br />
      <input name="structure" value="${status.count}" type="checkbox" />
     </td>
     <c:if test="${numVaryingLinkages > 0}">
      <td class="noleftright"></td>
     </c:if>
     <c:if test="${showPhiColumn}">
      <td class="noleftright"></td>
     </c:if>
     <c:if test="${showOmegaColumn}">
      <td class="noleftright"></td>
     </c:if>
     <td rowspan="${numVaryingLinkages + 1}">
      ${structure.energy}
     </td><%--
     <td rowspan="${numVaryingLinkages + 1}"> 
      <fmt:formatNumber value="${structure.boltzmann}" pattern="0.000E0" />
     </td>--%>
     <td rowspan="${numVaryingLinkages + 1}">
      <input type="button" class="button" value="Visualize"
             onclick="button_click(${status.count})"; />
     </td>
     <td rowspan="${numVaryingLinkages + 1}">
      <input style="font-size:12px;" type="submit" class="button" value="Download"
             onclick="document.getElementById('download_type').value='pdb${status.count}';this.form.submit();"/>
     </td>
    </tr> 


    <c:forEach var="entry" items="${structure.angles}" varStatus="entryStatus">
     <tr>
        <td class="noborder"><c:out value="${entry.key - 1}" /></td>

        <c:if test="${showPhiColumn}">
         <td class="noborder">
          <c:if test="${entry.value.phiSet}">

           <c:choose>
            <c:when test="${entry.value.phi == 60.0}">
             <c:out value="g"/>
            </c:when>
            <c:when test="${entry.value.phi == 180.0}">
             <c:out value="t"/>
            </c:when>
            <c:when test="${entry.value.phi == -60.0}">
             <c:out value="-g"/>
            </c:when>
            <c:otherwise>
             <c:out value="${entry.value.phi}"/>
            </c:otherwise>
           </c:choose>

          </c:if>
         </td>
        </c:if>

        <c:if test="${showOmegaColumn}">
         <td class="noborder">
          <c:if test="${entry.value.omegaSet}">

           <c:choose>
            <c:when test="${entry.value.omega == 60.0}">
             <c:out value="tg"/>
            </c:when>
            <c:when test="${entry.value.omega == 180.0}">
             <c:out value="gg"/>
            </c:when>
            <c:when test="${entry.value.omega == -60.0}">
             <c:out value="gt"/>
            </c:when>
            <c:otherwise>
             <c:out value="${entry.value.omega}"/>
            </c:otherwise>
           </c:choose>

          </c:if>
         </td>
        </c:if>

      </tr>
     </c:forEach>
   </c:forEach>
  </table>
 </div> <!-- download -->
 <br />
 <a class="button" href="options">&lt; Previous<a>
 <br /><br /><br />
 <%@ include file="/inc/footer.inc" %>
</div> <!-- wrapper -->
</form>
</body>
</html>
