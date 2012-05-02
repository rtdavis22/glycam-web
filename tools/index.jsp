<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
 <title>GLYCAM - Tools</title>
 <%@ include file="/inc/head.inc" %>
 <link rel="stylesheet" type="text/css" href="/css/subModal.css" />
 </script>
</head>
<body>
<form method="post">
 <div id="wrapper">
  <%@ include file="/inc/nav.inc" %>
  <h1>Tools</h1>
  <br />
  <div id="tools">
   <table>
    <tr>
     <td>
      <img src="/img/md.png" />
      <h3><a href="molecular-dynamics">Molecular Dynamics</a></h3>
      <p>Build and solvate glycans and oligosaccharides.  Use our intuitive point and click
interface, choose from a library of compounds, or paste oligosaccharide sequence text.
Download input files for use with AMBER.</p>
      <div class="clear"></div>
     </td>
     <td>
      <img src="/img/docking.png" />
      <h3><a href="#">Docking and Virtual Screening</a></h3>
      <p>Here, you can choose to dock a single oligosaccharide to a protein or you can screen an
entire library of ligands.  Docking is performed using our own enhanced version of Autodock Vina
that we call Vina-Carb.</p>
      <div class="clear"></div>
     </td>
    </tr>
    <tr>
     <td>
      <img src="/img/xtal.png" />
      <h3><a href="#">Crystallography</a></h3>
      <p>Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged.</p>
      <div class="clear"></div>
     </td>
     <td>
      <img src="/img/visualize.png" />
      <h3><a href="#">Visualization Only</a></h3>
      <p>If you just want to get a quick look at a molecule, this is the tool for you.</p>
      <div class="clear"></div>
     </td>
    </tr>
    <tr>
     <td>
      <img src="/img/analysis.png" />
      <h3><a href="#">Analysis</a></h3>
      <p>Given existing data, calculate J-couplings, autocorrelations, NOE estimates and more.</p>
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
