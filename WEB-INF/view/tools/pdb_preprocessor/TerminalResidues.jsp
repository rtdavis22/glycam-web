<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
 <title>PDB File Preprocessor - Terminal Residues</title>
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
   <c:forEach var="chain" items="${chains}" varStatus="status">
    <c:forEach var="option" items="${chain.possibleNterminalList}">
      enableNTerminal(${status.count}, '${option}');
    </c:forEach>
    <c:forEach var="option" items="${chain.possibleCterminalList}">
      enableCTerminal(${status.count}, '${option}');
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
  <div id="pdb_options">
    <p class="instruction">Select terminal residues.</p>
    <table style="margin:0 auto;">
     <tr>
      <c:if test="${showChainIdCol1}">
       <th></th>
      </c:if>
      <th></th>
      <c:if test="${showICodeCol1}">
       <th></th>
      </c:if>

      <c:if test="${showChainIdCol2}">
       <th></th>
      </c:if>
      <th></th>
      <c:if test="${showICodeCol2}">
       <th></th>
      </c:if>

      <th colspan="2">N-Termination</th>
      <th></th>
      <th colspan="3">C-Termination</th>

     </tr>
     <tr>
      <c:if test="${showChainIdCol1}">
       <th>Chain Id</th>
      </c:if>
      <th>Start Index</th>
      <c:if test="${showICodeCol1}">
       <th>Insertion Code</th>
      </c:if>

      <c:if test="${showChainIdCol2}">
       <th>Chain Id</th>
      </c:if>
      <th>End Index</th>
      <c:if test="${showICodeCol2}">
       <th>Insertion Code</th>
      </c:if>
      <th>-COCH<sub>3</sub></th>
      <th>NH<sub>3</sub><sup>+</sup></th>
      <th></th>
      <th>-NH<sub>2</sub></th>
      <th>-NHCH<sub>3</sub></th>
      <th>CO<sub>2</sub><sup>-</sup></th>
     </tr>
     <c:forEach var="chain" items="${chains}" varStatus="status">
      <tr>
       <c:if test="${showChainIdCol1}">
        <td>${chain.start.chainId}</td>
       </c:if>
       <td>${chain.start.resNum}</td>
       <c:if test="${showICodeCol1}">
        <td>${chain.start.iCode}</td>
       </c:if>
       <c:if test="${showChainIdCol2}">
        <td>${chain.end.chainId}</td>
       </c:if>
       <td>${chain.end.resNum}</td>
       <c:if test="${showICodeCol2}">
        <td>${chain.end.iCode}</td>
       </c:if>
       <td>
        <input type="radio" name="nterminal_${status.count}" value="COCH3"
               <c:if test="${chain.nterminalType == 'COCH3'}">checked="checked"</c:if> />
       </td>
       <td>
        <input type="radio" name="nterminal_${status.count}" value="NH3"
               <c:if test="${chain.nterminalType == 'NH3'}">checked="checked"</c:if> />
       </td>
       <td class="gap"></td>
       <td>
        <input type="radio" name="cterminal_${status.count}" value="NH2"
               <c:if test="${chain.cterminalType == 'NH2'}">checked="checked"</c:if> />
       </td>
       <td>
        <input type="radio" name="cterminal_${status.count}" value="NHCH3"
               <c:if test="${chain.cterminalType == 'NHCH3'}">checked="checked"</c:if> />
       </td>
       <td>
        <input type="radio" name="cterminal_${status.count}" value="CO2"
               <c:if test="${chain.cterminalType == 'CO2'}">checked="checked"</c:if> />
       </td>
      </tr>
     </c:forEach>
    </table>
   <br />
   <input type="button" class="button" value="< Back" onclick="window.location='menu';" />
   <input type="submit" class="button" value="Done" />
  </div> <!-- pdb_options -->
  <br /> <br >
  <%@ include file="/inc/footer.inc" %>
 </div> <!-- wrapper -->
</form>
</body>
</html>
