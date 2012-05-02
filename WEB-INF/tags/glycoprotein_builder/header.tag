<%@ tag body-content="empty" %>
<%@ attribute name="step" required="true" %>
<h1>Glycoprotein Builder</h1>
<div id="steps">
 <span>Step 1:<br />Upload PDB File</span>
 <span>Step 2:<br />Preprocess PDB</span>
 <span>Step 3:<br />Attach Glycans</span>
 <span>Step 4:<br />Options</span>
 <span>Step 5:<br />Download Files</span>
</div>
<script type="text/javascript">
 document.getElementById("steps").getElementsByTagName("span")[${step - 1}].className="selected";
</script>
