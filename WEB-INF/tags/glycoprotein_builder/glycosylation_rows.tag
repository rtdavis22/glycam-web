<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag body-content="empty" %>
<%@ attribute name="sites"
              type="org.glycam.molecular_dynamics.glycoprotein_builder.GlycosylationSiteList"
              required="true" %>
<%@ attribute name="prefix" required="true" %>
<%@ attribute name="showChainIdColumn" required="true" %>
<%@ attribute name="showICodeColumn" required="true" %>

<c:forEach var="site" items="${sites.iterator}" varStatus="status">
 <c:if test="${site.glycosylated}">
  <tr>
   <c:if test="${showChainIdColumn}"><td>${site.spot.info.chainId}</td></c:if>
   <td>${site.spot.info.resNum}</td>
   <c:if test="${showICodeColumn}"><td>${site.spot.info.ICode}</td></c:if>
   <td>
    <img src="/services/drawglycan/${site.glycanSession.sequence}&position_labels=off&config_labels=off&dpi=35" style="max-width:100px;margin:10px;" />
   </td>
   <td>
    <input class="button" style="padding:1px 5px;" type="button" value="Edit"
           onclick="window.location='glycan-options?edit=${prefix}${status.count - 1}';"/>
   </td>
   <td>
    <input class="button" style="padding:1px 5px;margin:0px 8px;" type="button" value="Remove"
           onclick="window.location='attach-glycans?remove=${prefix}${status.count - 1}';" />
   </td>
  </tr>
 </c:if>
</c:forEach>
