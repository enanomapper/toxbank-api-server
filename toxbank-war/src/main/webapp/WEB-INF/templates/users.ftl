<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl">
<#include "/users_head.ftl" >


<script type='text/javascript'>
$(document).ready(function() {
	var oTable = defineUsersTable("${ambit_root}","${ambit_request_json}","#users");	
});
</script>

<script type="text/javascript">
$(document)
		.ready(
				function() {
					jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/admin" title="admin">Admin</a></li>');
					jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/user" title="users">Users</a></li>');
					jQuery("#breadCrumb").jBreadCrumb();
					jQuery("#welcome").html("Users");		
					loadHelp("${ambit_root}","task");
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
		<div class="thirteen columns ui-widget-content ui-corner-all add-bottom" style="padding:0;" >
		
		<table id='users' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
		<thead>
		<tr>
		<th>Username</th>	
		<th>Name</th>
		<th>e-mail</th>
		<th>Affiliation</th>
		</tr>
		</thead>
		<tbody></tbody>
		</table>
			
		</div> 	

<div class="two columns remove-bottom" style="padding:0;" >

&nbsp;

	
</div>

<div class='row add-bottom' style="height:140px;">&nbsp;</div>


<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
