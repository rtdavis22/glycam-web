<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="gp" tagdir="/WEB-INF/tags/glycoprotein_builder" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
 <title>Glycoprotein Builder - Download Files</title>
 <%@ include file="/inc/head.inc" %>
 <script type="text/javascript" src="/deps/jmol/Jmol.js"></script>
 <script type="text/javascript">
  jmolInitialize("/deps/jmol/");
 </script>
</head>
<body>
<form method="post">
<input type="hidden" name="uuid" value="${uuid}" /> 

<div id="wrapper">
 <%@ include file="/inc/nav.inc" %>
 <gp:header step="5" />
 <div id="mdfiles_download">
  <p class="instruction">Download PDB, AMBER topology, and AMBER restart files below.</p>
  <br />
  <script type=text/javascript>
  jmolApplet([300, 450],
            "load pdb::/userdata/tools/mdfiles/${uuid}/structure.pdb");
  </script>

  <br />
  <p class="instruction">File Type: 
   <input name="filetype" type="radio" value="tar" checked /> tar.gz
   <input name="filetype" type="radio" value="zip" /> zip
  </p>
  <br />
  <input class="button" name="submit" type="submit" value="Download Files" />
  <br /><br />
 </div> <!-- download -->
 <br />
 <input type="button" class="button" value="< Previous"
        onclick="window.location='attach-glycans';" />
 <br /><br /><br />
 <%@ include file="/inc/footer.inc" %>
</div> <!-- wrapper -->
</form>
</body>
</html>
