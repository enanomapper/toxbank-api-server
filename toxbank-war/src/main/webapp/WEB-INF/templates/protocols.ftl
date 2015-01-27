<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl">

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>

<script type='text/javascript'>
$(document).ready(function() {
	var oTable = defineProtocolsTable("${ambit_root}","${ambit_request_json}","#protocols");	
});
</script>

<script type="text/javascript">
$(document)
		.ready(
				function() {
					jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/protocol" title="users">Protocols</a></li>');
					jQuery("#breadCrumb").jBreadCrumb();
					jQuery("#welcome").html("protocols");		
					loadHelp("${ambit_root}","protocols");
					
				});
</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">


<#include "/banner_crumbs.ftl">

<div class="one column remove-bottom" style="padding:0;" >&nbsp;
</div>

		<!-- Page Content
		================================================== -->
		<div class="fourteen columns ui-widget-content ui-corner-all add-bottom" style="padding:0;" >
		
		<table id='protocols' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
		<thead>
		<tr>
		<th>Identifier</th>
		<th>Title</th>
		<th>Status / Owner</th>		
		<th>Abstract</th>
		<th>Project/ Organisation </th>		
		<th>Published</th>
		<th>Updated</th>
		
		<!--
		<th>Submission date</th>
		<th>Date updated</th>
		<th>Authors</th>
		<th>Keywords</th>
		<th>Is summary searchable</th>

		<th>Data template</th>
		-->
		</tr>
		</thead>
		<tbody></tbody>
		</table>
			
		</div> 	

<div class="one column remove-bottom" style="padding:0;" >

&nbsp;

	
</div>

<div class='row add-bottom' style="height:140px;">&nbsp;</div>


<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
