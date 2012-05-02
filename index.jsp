<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
 <title>GLYCAM</title>
 <%@ include file="/inc/head.inc" %>
 <link rel="stylesheet" type="text/css" href="/css/subModal.css" />
 </script>
</head>
<body>
<form method="post">
 <div id="wrapper">
  <%@ include file="/inc/nav.inc" %>
  <br />
  <div id="home">
   <div id="tools">
     <h3>Tools</h3>
     <a href="/tools/molecular-dynamics/oligosaccharide-builder/build-glycan">
      <img src="/img/md.png" />
     </a>
     <a href="/tools/molecular-dynamics/glycoprotein-builder/upload-pdb">
      <img src="#" />
     </a>
     <img src="#" />
     <img src="#" />
     <img src="#" />
     <br />
    <a href="/tools"><span>More info</span></a>
   </div>
  </div> <!-- home -->
  <br />
  <%@ include file="/inc/footer.inc" %>
 </div> <!-- wrapper -->
</form>
</body>
</html>

<script type="text/javascript">

function register_coordinate(position) {
  var lat = position.coords.latitude,
      lon = position.coords.longitude,
      resource, xhr;
  resource = '/register-coordinate?latitude=' + lat + '&longitude=' + lon;
  xhr = new XMLHttpRequest();
  xhr.open('GET', resource, false);
  xhr.send(null);
}

if (navigator.geolocation) {
  navigator.geolocation.getCurrentPosition(register_coordinate);
}

</script>
