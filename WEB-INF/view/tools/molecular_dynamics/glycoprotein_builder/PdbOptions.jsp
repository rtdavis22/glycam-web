<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
 <title>Molecular Dynamics - Glycoprotein Builder - PDB Options</title>
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

  function on_load() {
    var checkbox = document.getElementById('select_all_bonded');
    if (checkbox) {
      checkbox.onclick = function() {
        var checked = this.checked,
            inputElements = document.getElementsByTagName('input'),
            i;
        for (i = 0; i < inputElements.length; i++) {
          if (inputElements[i].type === "checkbox" && inputElements[i].name.search("cys_") != -1) {
            inputElements[i].checked = checked;
          }
        }
      }
    }

    checkbox = document.getElementById('select_all_hid');
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
 </script>
</head>
<body onload="on_load();">
<form method="post">
 <div id="wrapper">
  <%@ include file="/inc/nav.inc" %>
  <h1>Glycoprotein Builder</h1>
  <div id="steps">
   <span>Step 1:<br />Upload PDB File</span>
   <span class="selected">Step 2:<br />PDB Options</span>
   <span>Step 3:<br />Download Files</span>
  </div>
  <div id="pdb_options">
   <c:if test="${not empty cysPairs}">
    <p class="instruction">Select disulfide bonds.</p>

    <table style="margin:0 auto;">
     <tr>
      <c:set var="cysColSpan"
             value="${1 + (showCYSChainIdColumn?1:0) + (showCYSICodeColumn?1:0)}"
             scope="page" />
      <th colspan="${cysColSpan}">CYS 1</th>
      <th colspan="${cysColSpan}">CYS 2</th>
     </tr>
     <tr>
      <c:if test="${showCYSChainIdColumn}">
       <th>Chain Id</th>
      </c:if>
      <th>Residue Number</th>
      <c:if test="${showCYSICodeColumn}">
       <th>Insertion Code</th>
      </c:if>
      <c:if test="${showCYSChainIdColumn}">
       <th>Chain Id</th>
      </c:if>
      <th>Residue Number</th>
      <c:if test="${showCYSICodeColumn}">
       <th>Insertion Code</th>
      </c:if>
      <th>Distance</th>
      <th>
       Bonded<br />
       <span style="font-size:12px;font-weight:normal">Select All:</span>
       <input id="select_all_bonded" type="checkbox" />
      </th>
     </tr>

     <c:forEach var="cysPair" items="${cysPairs}" varStatus="status">
      <tr>
       <c:if test="${showCYSChainIdColumn}">
        <td>${cysPair.cys1.chainId}</td>
       </c:if>
       <td>${cysPair.cys1.resNum}</td>
       <c:if test="${showCYSICodeColumn}">
        <td>${cysPair.cys1.iCode}</td>
       </c:if>
       <c:if test="${showCYSChainIdColumn}">
        <td>${cysPair.cys2.chainId}</td>
       </c:if>
       <td>${cysPair.cys2.resNum}</td>
       <c:if test="${showCYSICodeColumn}">
        <td>${cysPair.cys2.iCode}</td>
       </c:if>
       <td><fmt:formatNumber value="${cysPair.distance}" pattern="0.000" /></td>
       <td>
        <input type="checkbox" name="cys_${status.count}"
               <c:if test="${cysPair.bonded}">checked="checked"</c:if> />
       </td>
      </tr>
     </c:forEach>
    </table>
   </c:if> <%-- not empty cysPairs --%>
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
       HID<br />
       <span style="font-size:12px;font-weight:normal;">Select All:</span>
       <input id="select_all_hid" name="select_all_his" type="radio" />
      </th>
      <th>
       HIE<br />
       <span style="font-size:12px;font-weight:normal;">Select All:</span>
       <input id="select_all_hie" name="select_all_his" type="radio" />
      </th>
      <th>
       HIP<br />
       <span style="font-size:12px;font-weight:normal;">Select All:</span>
       <input id="select_all_hip" name="select_all_his" type="radio" />
      </th>
     </tr>

     <c:forEach var="hisResidue" items="${hisResidues}" varStatus="status">
      <tr>
       <c:if test="${showHisChainIdColumn}">
        <td>${hisResidue.residue.chainId}</td>
       </c:if>
       <td>${hisResidue.residue.resNum}</td>
       <c:if test="${showHisICodeColumn}">
        <td>${hisResidue.residue.iCode}</td>
       </c:if>

       <td>
        <input type="radio" name="his_${status.count}" value="HID"
               <c:if test="${hisResidue.mappedName == 'HID'}">checked="checked"</c:if> />
       </td>
       <td>
        <input type="radio" name="his_${status.count}" value="HIE"
               <c:if test="${hisResidue.mappedName == 'HIE'}">checked="checked"</c:if> />
       </td>
       <td>
        <input type="radio" name="his_${status.count}" value="HIP"
               <c:if test="${hisResidue.mappedName == 'HIP'}">checked="checked"</c:if> />
       </td>
      </tr>
     </c:forEach>
    </table>
   </c:if> <%-- not empty hisResidues --%>
   <br />
   <input type="button" class="button" value="< Previous" onclick="window.location='upload-pdb';" />
   <input type="submit" class="button" value="Next >" />
  </div> <!-- pdb_options -->
  <br /> <br >
  <%@ include file="/inc/footer.inc" %>
 </div> <!-- wrapper -->
</form>
</body>
</html>
