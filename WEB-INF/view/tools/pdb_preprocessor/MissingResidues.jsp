<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
 <title>PDB File Preprocessor - Missing Residues</title>
 <%@ include file="/inc/head.inc" %>
 <script type="text/javascript">
  var onLoad = function() {
    disableAllTerminalOptions();
    enablePossibleOptions();
  }

  var disableAllTerminalOptions = function() {
    var els = document.getElementsByTagName("input"),
        i, name;
    for (i = 0; i < els.length; i++) {
      name = els[i].name;
      if (name && (name.indexOf("nterminal_") == 0 || name.indexOf("cterminal_") == 0)) {
        els[i].disabled = true;
      }
    }
  }

  var enableTerminal = function(name, value) {
    var els = document.getElementsByName(name),
        i;
    for (i = 0; i < els.length; i++) {
      if (els[i].value == value)
        els[i].disabled = false;
    }
  }

  var enableNTerminal = function(index, value) {
    enableTerminal("nterminal_" + index, value);
  }

  var enableCTerminal = function(index, value) {
    enableTerminal("cterminal_" + index, value);
  }

  var enablePossibleOptions = function() {
   <c:forEach var="gap" items="${gaps}" varStatus="status">
    <c:forEach var="possibleNTerminal" items="${gap.possibleNterminalList}">
      enableNTerminal(${status.count}, '${possibleNTerminal}');
    </c:forEach>
    <c:forEach var="possibleCTerminal" items="${gap.possibleCterminalList}">
      enableCTerminal(${status.count}, '${possibleCTerminal}');
    </c:forEach>
   </c:forEach>
  }
 </script>

</head>
<body onload="onLoad()">
<form method="post">
 <div id="wrapper">
  <%@ include file="/inc/nav.inc" %>
  <h1>PDB File Preprocessor</h1>
  <div id="pdb_preprocessor">
   <div id="missing_residues">
    <p class="instruction">...</p>
    <table style="margin:0 auto;">
     <tr>
      <th></th>
      <th></th>
      <th colspan="3">C-Termination</th>
      <th colspan="2" class="gap"></th>
      <th colspan="2">N-Termination</th>
      <th></th>
     </tr>
     <tr>
      <th>Chain</th>
      <th>Residue Before Gap</th>
      <th>-NH<sub>2</sub></th>
      <th>-NHCH<sub>3</sub><br /><span style="font-size:12px;">(NME)</span></th>
      <th>CO<sub>2</sub><sup>-</sup></th>
      <th class="gap"></th>
      <th>Residue After Gap</th>
      <th>-COCH<sub>3</sub><br /><span style="font-size:12px;">(ACE)</span></th>
      <th>NH<sub>3</sub><sup>+</sup></th>
     </tr>
     <c:forEach var="gap" items="${gaps}" varStatus="status">
      <tr>
       <td>
        ${gap.chainInfo.start.resNum} - ${gap.chainInfo.end.resNum}
        <c:if test="${gap.chainInfo.start.chainId != ' '}">
         (${gap.chainInfo.start.chainId})
        </c:if>
       </td>
       <td>${gap.start.resNum}</td>
       <td>
        <input type="radio" name="cterminal_${status.count}" value="NH2"
               <c:if test="${gap.cterminalType == 'NH2'}">checked="checked"</c:if> />
       </td>
       <td>
        <input type="radio" name="cterminal_${status.count}" value="NHCH3"
               <c:if test="${gap.cterminalType == 'NHCH3'}">checked="checked"</c:if> />
       </td>
       <td>
        <input type="radio" name="cterminal_${status.count}" value="CO2"
               <c:if test="${gap.cterminalType == 'CO2'}">checked="checked"</c:if> />
       </td>
       <td class="gap"></td>
       <td>${gap.end.resNum}</td>
       <td>
        <input type="radio" name="nterminal_${status.count}" value="COCH3"
               <c:if test="${gap.nterminalType == 'COCH3'}">checked="checked"</c:if> />
       </td>
       <td>
        <input type="radio" name="nterminal_${status.count}" value="NH3"
               <c:if test="${gap.nterminalType == 'NH3'}">checked="checked"</c:if> />
       </td>
      </tr>
     </c:forEach>
    </table>
    <br />
    <input type="button" class="button" value="< Back" onclick="window.location='menu';" />
    <input type="submit" class="button" value="Done" />
   </div> <!-- missing_residues -->
  </div> <!-- pdb_preprocessor -->
  <br /> <br >
  <%@ include file="/inc/footer.inc" %>
 </div> <!-- wrapper -->
</form>
</body>
</html>
