<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
 <title>GLYCAM - Molecular Dynamics</title>
 <%@ include file="/inc/head.inc" %>
 <link rel="stylesheet" type="text/css" href="/css/subModal.css" />
 </script>
</head>
<body>
<form method="post">
 <div id="wrapper">
  <%@ include file="/inc/nav.inc" %>
  <h1>Molecular Dynamics</h1>
  <br />
  <div id="tools">
   <table>
    <tr>
     <td>
      <img href="molecular-dynamics/oligosaccharide-builder/build-glycan" src="#"/>
      <h3><a href="molecular-dynamics/oligosaccharide-builder/build-glycan">Build an oligosaccharide</a></h3>
      <p>Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged.</p>
      <div class="clear"></div>
     </td>
     <td>
      <img src="#"/>
      <h3><a href="pdb-preprocessor/upload-pdb">Build a glycoprotein</a></h3>
      <p>Nunc imperdiet, eros nec convallis pellentesque, augue arcu mattis arcu, quis convallis eros dui id justo. Donec malesuada enim et dui interdum feugiat. Sed faucibus posuere congue. Vivamus luctus eros eget felis interdum lobortis. Vestibulum elit orci, cursus id fermentum.</p>
      <div class="clear"></div>
     </td>
    </tr>
   </table>
  </div> <!-- tools -->
  <br />
  <%@ include file="/inc/footer.inc" %>
 </div> <!-- wrapper -->
</form>
</body>
</html>
