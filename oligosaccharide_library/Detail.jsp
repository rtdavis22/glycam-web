<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
 <title></title>
 <link href="/css/styles.css" type="text/css" rel="Stylesheet" />
</head>
<body>
<div id="oligosaccharide_library">
<br />
 <div style="text-align:left">
  <a href="/oligosaccharide-library/page?page=${page}&category=${category}">&lt; back</a>
 </div>
 <br />
 <div style="text-align:center;">
  <img src="/services/drawglycan/${item.sequence}" />
 </div>
 <br /><br />
 <div>
  Sequence:<br />${item.sequence}
  <br /><br />
  Name: ${item.name}
  <br /><br />
  <input type="button" class="button" value="Select"
         onclick="window.parent.returnVal='${item.sequence}';window.top.hidePopWin(true);" />

 </div>

</div>
</body>
</html>

