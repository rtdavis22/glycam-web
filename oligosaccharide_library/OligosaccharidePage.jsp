<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
 <title></title>
 <link href="/css/styles.css" type="text/css" rel="Stylesheet" />
</head>
<body>
<div id="oligosaccharide_library">
 <h2>CFG Glycan Array v5.0</h2>
 <p id="section_description"></p>
 <hr style="height:1px;background-color:black;margin-top:3px;margin-bottom:3px;" />
 <p style="text-align:right">
  <c:if test="${page != 1}">
   <a href="/oligosaccharide-library/page?page=${page - 1}&category=${category}">&lt;</a>
  </c:if>
  <c:forEach var="num" items="${pages}">
   <a <c:if test="${num == page}">style="text-decoration:underline;font-weight:bold;"</c:if>
      href="/oligosaccharide-library/page?page=${num}&category=${category}">${num}</a>
  </c:forEach>
  <c:if test="${page != numPages}">
   <a href="/oligosaccharide-library/page?page=${page + 1}&category=${category}">&gt;</a>
  </c:if>
 </p>
 <table id="grid">
  <c:forEach var="item" items="${items}" varStatus="status">
   <c:if test="${status.count % 4 eq 1}">
    <tr>
   </c:if>
   <td valign="top" class="item" onmouseover="this.getElementsByTagName('div')[0].style.visibility = 'visible';"
                     onmouseout="this.getElementsByTagName('div')[0].style.visibility = 'hidden';" >
    <img src="/services/drawglycan/${item.sequence}?position_labels=off&config_labels=off&dpi=35" />
    <p>${item.name}</p>
    <div style="visibility:hidden;">
     <input type="button" value="Select" onclick="window.parent.returnVal='${item.sequence}';window.top.hidePopWin(true);"/>
     <input type="button" value="Enlarge" onclick="window.location='detail?id=${item.id}&category=${category}&page=${page}'" />
    </div>
   </td>
   <c:if test="${(status.count %4 eq 0) || status.last}">
    </tr>
   </c:if>
  </c:forEach>
 </table> <!-- grid -->
 <%-- This duplicate paginator is a clear violation of DRY. What's the best way to reuse the
      previous one? --%>
 <p style="text-align:right">
  <c:if test="${page != 1}">
   <a href="/oligosaccharide-library/page?page=${page - 1}&category=${category}">&lt;</a>
  </c:if>
  <c:forEach var="num" items="${pages}">
   <a <c:if test="${num == page}">style="text-decoration:underline;font-weight:bold;"</c:if>
      href="/oligosaccharide-library/page?page=${num}&category=${category}">${num}</a>
  </c:forEach>
  <c:if test="${page != numPages}">
   <a href="/oligosaccharide-library/page?page=${page + 1}&category=${category}">&gt;</a>
  </c:if>
 </p>

 <div class="clear"></div> 
</div>
</body>
</html>

