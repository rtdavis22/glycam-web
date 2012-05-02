
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<%@ include file="/inc/head.inc" %>
<script type="text/javascript">
 function getXMLHTTP() {
  var XMLHTTP = null;
  if (window.ActiveXObject) {
   try {
    XMLHTTP = new ActiveXObject("Msxml2.XMLHTTP");
   } catch(e) {
    try {
     XMLHTTP = new ActiveXObject("Microsoft.XMLHTTP");
    } catch(e) {
    }
   }
  } else if (window.XMLHttpRequest) {
   try {
    XMLHTTP = new XMLHttpRequest();
   } catch(e) {
   }
  }
  return XMLHTTP;
 }

 function update_progress() {
  var XMLHTTP = getXMLHTTP();
   if (XMLHTTP != null) {
    XMLHTTP.open("GET", "/tools/molecular-dynamics/oligosaccharide-builder/build-progress");
    XMLHTTP.onreadystatechange = function() {
     if(XMLHTTP.readyState == 4 && XMLHTTP.status == 200) {
      if (XMLHTTP.responseText.search("done") != -1) {       
        window.location = "download-files"; 
      }
      else {
        document.getElementById("structuresBuilt").innerHTML = XMLHTTP.responseText;
      }
     }
    }
    XMLHTTP.send(null);
  }
 }

 self.setInterval("update_progress()", 1000);
</script>

</head>
<body>
<div id="waitpage">
 <img src="/img/logo.jpg" alt="logo" /><br /><br />
 <p>Minimizing ${numStructures} structures</p><br /><br />
 <img src="/img/loading.gif" alt="loading" />
 <br /><br />
 <p><span id="structuresBuilt">0</span> / ${numStructures}</p>
</div>
</body>
</html>
