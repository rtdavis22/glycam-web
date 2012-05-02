<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="gp" tagdir="/WEB-INF/tags/glycoprotein_builder" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
 <title>Molecular Dynamics - Glycoprotein Builder - Attach Glycans</title>
 <%@ include file="/inc/head.inc" %>
 <script type="text/javascript">
  var toInputGlycanPage = function() {
    sendToPage('input-glycan');
  }

  var toDownloadPage = function() {
    sendToPage('download-files');
  }

  var toPdbPreprocessor = function() {
    sendToPage('/tools/pdb-preprocessor/menu');
  }

  var sendToPage = function(location) {
    window.location = location;
  }
 </script>
</head>
<body>
<form method="post">
 <div id="wrapper">
  <%@ include file="/inc/nav.inc" %>
  <gp:header step="3" />
  <div id="attach_glycans" class="input_glycan">
   <br />
   <input type="button" class="button" value="Attach a Glycan" onclick="toInputGlycanPage();" />
   <span class="instruction">or</span>
   <input type="button" class="button" value="Download Files" onclick="toDownloadPage();" />
   <br /><br />
   <input type="button" class="button" value="< Previous" onclick="toPdbPreprocessor();" />

   <c:if test="${nSites.anyGlycosylated || oSites.anyGlycosylated}">
    <br /><br />
    <h2>Current Glycans</h2>
    <table style="margin:0 auto;">
     <tr>
      <c:if test="${showChainIdColumn}"><th>Chain ID</th></c:if>
      <th>Residue Number</th>
      <c:if test="${showICodeColumn}"><th>Insertion Code</th></c:if>
     </tr>

     <gp:glycosylation_rows sites="${nSites}" prefix="n"
                            showChainIdColumn="${showChainIdColumn}"
                            showICodeColumn="${showICodeColumn}" />
     <gp:glycosylation_rows sites="${oSites}" prefix="o"
                            showChainIdColumn="${showChainIdColumn}"
                            showICodeColumn="${showICodeColumn}" />
    </table>
   </c:if>

  </div> <%-- attach_glycans --%>
  <br /> <br >
  <%@ include file="/inc/footer.inc" %>
 </div> <%-- wrapper --%>
</form>
</body>
</html>
