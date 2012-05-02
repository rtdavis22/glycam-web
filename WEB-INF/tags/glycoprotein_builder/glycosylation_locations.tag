<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag body-content="empty" %>
<%@ attribute name="sites"
              type="org.glycam.molecular_dynamics.glycoprotein_builder.GlycosylationSiteList"
              required="true" %>
<%@ attribute name="prefix" required="true" %>
<%@ attribute name="tableId" required="true" %>

<c:set var="showChainIdColumn" value="${sites.anyWithChainId}" />
<c:set var="showICodeColumn" value="${sites.anyWithICode}" />
<c:set var="showGlycanColumn" value="${sites.anyGlycosylated}" />

<table id="${tableId}" class="glycosylation_sites">
 <tr>
  <th>Context</th>
  <c:if test="${showChainIdColumn}"><th>Chain Id</th></c:if>
  <th>Residue Number</th>
  <c:if test="${showICodeColumn}"><th>Insertion Code</th></c:if>
  <th>
   <a class="tooltip" href="#">
    SASA<span>Solvent accessible surface area, calculated by NAccess</span>
   </a>
  </th>
  <th>Select</th>
  <c:if test="${showGlycanColumn}"><th>Glycan</th></c:if>
 </tr>

 <c:forEach var="site" items="${sites.iterator}" varStatus="status">
  <tr class="${site.spot.likely?'likely':'not_likely'}">
   <td class="${site.spot.likely?'likely':''}">${site.spot.context}</td>
   <c:if test="${showChainIdColumn}"><td>${site.spot.info.chainId}</td></c:if>
   <td>${site.spot.info.resNum}</td>
   <c:if test="${showICodeColumn}"><td>${site.spot.info.ICode}</td></c:if>
   <td class="${site.withHighSasa?'high_sasa':site.withLowSasa?'low_sasa':''}">
    ${site.spot.sasa}<c:if test="${site.withHighSasa}"> +</c:if>
                     <c:if test="${site.withLowSasa}"> -</c:if>
   </td>
   <td>
    <input type="checkbox" name="${prefix}${status.count}"
           <c:if test="${!site.potentialGlycosylationSite}">disabled="disabled"</c:if> />
   </td>
   <c:if test="${showGlycanColumn}">
   <td>
    <c:if test="${site.glycosylated}">
     <img src='/services/drawglycan/${site.glycanSession.sequence}&position_labels=off&config_labels=off&dpi=35' />
    </c:if>
   </td>
   </c:if>
  </tr>
 </c:forEach>
</table>
<br />
