<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="gp" tagdir="/WEB-INF/tags/glycoprotein_builder" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
 <title>Molecular Dynamics - Glycoprotein Builder - Choose Glycosylation Locations</title>
 <%@ include file="/inc/head.inc" %>
 <script type="text/javascript">
  function show_or_hide_rows(button, table_id) {
    var rows = document.getElementById(table_id).getElementsByTagName('tr'),
        display, i;

    if (button.value !== 'Show All Locations') {
        button.value = 'Show All Locations';
        display = 'none';
    } else {
        button.value = 'Show Only Biologically Likely Locations';
        display = 'table-row';
    }

    for (i = 0; i < rows.length; i++) {
      if (rows[i].className.search('not_likely') !== -1)
        rows[i].style.display = display;
    }
  }
 </script>
</head>
<body>
<form method="post">
 <div id="wrapper">
  <%@ include file="/inc/nav.inc" %>
  <gp:header step="3" />
  <div id="glycoprotein_builder">
  <div id="choose_locations">
    <p class="instruction">Choose glycosylation sites below. Biologically likely sites (in bold) are shown by default.</p>
    <br />
    <c:if test="${not empty nSites}">
     <h3>N-linking</h3>
     <gp:glycosylation_locations prefix="n" sites="${nSites}" tableId="n_linking" />
     <input type="button" class="button" value="Show All Locations"
            onclick="show_or_hide_rows(this, 'n_linking');" />
     <br />
    </c:if>
    <br />
    <c:if test="${not empty oSites}">
     <h3>O-linking</h3>
     <gp:glycosylation_locations prefix="o" sites="${oSites}" tableId="o_linking" />
     <input type="button" class="button" value="Show All Locations"
            onclick="show_or_hide_rows(this, 'o_linking');" />
     <br />
    </c:if>
   <br />
   <input type="button" class="button" value="< Back" onclick="window.location='glycan-options';" />
   <input type="submit" class="button" value="Next >" />
  </div> <%-- choose_locations --%>
 </div> <%-- glycoprotein_builder --%>
 <br /> <br />
 <%@ include file="/inc/footer.inc" %>
</div> <%-- wrapper --%>
</form>
</body>
</html>
