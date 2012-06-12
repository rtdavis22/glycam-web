<%
 int width = 200;
 int height = 400;
 if (request.getParameter("width") != null)
     width = Integer.parseInt(request.getParameter("width"));
 if (request.getParameter("height") != null)
     height = Integer.parseInt(request.getParameter("height"));
%>

<head>
 <script type="text/javascript" src="/deps/jmol/Jmol.js"></script>
 <script type="text/javascript">
  jmolInitialize("/deps/jmol/"); 
 </script>
</head>
<body>
<script type=text/javascript>
 jmolApplet([<%=width%>, <%=height%>], 
            "load pdb::<%=request.getParameter("pdbfile")%>");
</script> 
</body>
