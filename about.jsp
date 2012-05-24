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
  <div id="about" style="overflow:auto;">
    <h1>The Research Group of Robert J. Woods</h1>
    <br />
    <img src="/img/woodsgroup.jpg" style="width:350px;float:right;margin:10px;" />
    <p style="font-size:16px;text-align:left">Research in the Woods group examines the relationship between carbohydrate conformation and biological recognition. Areas of particular interest include carbohydrate antigenicity in immunological events, carbohydrate- processing enzymes, and the development of appropriate simulational methods for these systems. The flexibility of biomolecules is ideally suited to analysis by molecular dynamics simulations, whereas quantum mechanical methods are applied to examine enzyme mechanisms and develop the GLYCAM force field parameters. The relationship between carbohydrate sequence/structure and the affinity of carbohydrate- protein interactions is amenable to study by free energy perturbation and direct deltaG calculations. The computational methods are complimented by experimental techniques, such as NMR spectroscopy, X-ray crystallography, and mass spectrometry.</p>
   <br />
    <img src="/img/glycosylation.png" style="width:220px;float:left;margin:10px;" />
    <p style="font-size:16px;text-align:left">Predicted structure of glycosylated human Erythropoietin. Erythropoietin exists as a mixture of glycosylated variants (glycoforms), [1] and glycosylation is known to modulate its biological function. [2, 3] The three high-mannose N-linked oligosaccharides (Man_9 GlcNAc_2 ) are shown in purple, the single O-linked glycan (alpha-GalNAc) is shown in pink. The structure in the image represents a single glycoform that is the origin from which all others are generated. The protein structure was solved by NMR (pdbid: 1BUY) [4] and the glycans were added to the protein using the GLYCAM Web-tool (http://www.glycam.com) with energy minimization performed using the AMBER FF99 parameters [5] for the protein and the GLYCAM06 parameters [6] for the oligosaccharides. Figure made by the Woods group using Chimera. [7]</p>
   <div class="clear"></div>
  </div>
  <br />
  <%@ include file="/inc/footer.inc" %>
 </div> <!-- wrapper -->
</form>
</body>
</html>

