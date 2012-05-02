<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
 <title>PDB File Preprocessor - HIS Mappings</title>
 <%@ include file="/inc/head.inc" %>
 <script type="text/javascript">
  function set_all_his_mappings(value) {
    var inputElements = document.getElementsByTagName('input'),
        i;
    for (i = 0; i < inputElements.length; i++) {
      if (inputElements[i].value === value) {
        inputElements[i].checked = true;
      }
    }
  }

  var configureSetAllButtons = function() {
    var checkbox = document.getElementById('select_all_hid');
    if (checkbox) {
      checkbox.onclick = function() {
        set_all_his_mappings('HID');
      }

      document.getElementById('select_all_hie').onclick = function() {
        set_all_his_mappings('HIE');
      }

      document.getElementById('select_all_hip').onclick = function() {
        set_all_his_mappings('HIP');
      }
    }
  }

  var disableAllMappings = function() {
    var els = document.getElementsByTagName("input"),
        i, name;
    for (i = 0; i < els.length; i++) {
      name = els[i].name;
      if (name && (name.indexOf("his_") == 0)) {
        els[i].disabled = true;
      }
    }
  }

  var enablePossibleTypes = function() {
   <c:forEach var="hisResidue" items="${hisResidues}" varStatus="status">
    <c:forEach var="possibleType" items="${hisResidue.possibleMappingTypes}">
     enableType(${status.count}, '${possibleType}');
    </c:forEach>
   </c:forEach>
  }

  var enableType = function(index, type) {
    var els = document.getElementsByName('his_' + index),
        i;
    for (i = 0; i < els.length; i++) {
      if (els[i].value == type)
        els[i].disabled = false;
    }
  }

  var disableInapplicableSetAllButtons = function() {
    var els = document.getElementsByTagName("input"),
        i, name;
    for (i = 0; i < els.length; i++) {
      name = els[i].name;
      if (name && name.indexOf("his_") == 0) {
        if (els[i].disabled) {
          disableSetAllButton(els[i].value);
        }
      } 
    }
  }

  var disableSetAllButton = function(hisMapping) {
    var id = "select_all_" + hisMapping.toLowerCase();
    document.getElementById(id).disabled = true;
  }

  var onLoad = function() {
    configureSetAllButtons();
    disableAllMappings();
    enablePossibleTypes();
    disableInapplicableSetAllButtons();
  }
 </script>
</head>
<body onload="onLoad();">
<form method="post">
 <div id="wrapper">
  <%@ include file="/inc/nav.inc" %>
  <h1>PDB File Preprocessor</h1>
  <div id="pdb_options">
   <c:if test="${not empty hisResidues}">
    <br />
    <p class="instruction">Choose HIS mappings.</p>
    <table style="margin:0 auto;">
     <tr>
      <c:if test="${showHisChainIdColumn}">
       <th>Chain Id</th>
      </c:if>
      <th>Residue Number</th>
      <c:if test="${showHisICodeColumn}">
       <th>Insertion Code</th>
      </c:if>
      <th>
       HIS-&delta;<br />
       <span style="font-size:12px;font-weight:normal;">Select All:</span>
       <input id="select_all_hid" name="select_all_his" type="radio" />
      </th>
      <th>
       HIS-&epsilon;<br />
       <span style="font-size:12px;font-weight:normal;">Select All:</span>
       <input id="select_all_hie" name="select_all_his" type="radio" />
      </th>
      <th>
       HIS<sup>+</sup><br />
       <span style="font-size:12px;font-weight:normal;">Select All:</span>
       <input id="select_all_hip" name="select_all_his" type="radio" />
      </th>
     </tr>

     <c:forEach var="hisResidue" items="${hisResidues}" varStatus="status">
      <tr>
       <c:if test="${showHisChainIdColumn}">
        <td>${hisResidue.residueInfo.chainId}</td>
       </c:if>
       <td>${hisResidue.residueInfo.resNum}</td>
       <c:if test="${showHisICodeColumn}">
        <td>${hisResidue.residueInfo.ICode}</td>
       </c:if>

       <td>
        <input type="radio" name="his_${status.count}" value="HID"
               <c:if test="${hisResidue.mappedType == 'HID'}">checked="checked"</c:if> />
       </td>
       <td>
        <input type="radio" name="his_${status.count}" value="HIE"
               <c:if test="${hisResidue.mappedType == 'HIE'}">checked="checked"</c:if> />
       </td>
       <td>
        <input type="radio" name="his_${status.count}" value="HIP"
               <c:if test="${hisResidue.mappedType == 'HIP'}">checked="checked"</c:if> />
       </td>
      </tr>
     </c:forEach>
    </table>
   </c:if> <%-- not empty hisResidues --%>
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
