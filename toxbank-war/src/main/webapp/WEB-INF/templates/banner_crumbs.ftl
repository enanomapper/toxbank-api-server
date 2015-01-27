
<!-- banner -->
<div class="row half-bottom" id="header" style="padding-top:5px">
	<#if menu_profile??>
		<div class="two columns">
			<a href="${ambit_root}/ui"><img class='scale-with-grid' border='0' src='${ambit_root}/images/profile/${menu_profile}/logo.png' title='Home' alt='enanoMapper logo'></a>
		</div>
		<div class="fourteen columns remove-bottom">
			<#include "/menu/profile/${menu_profile}/smartmenu.ftl">
		</div>
	<#else>
		<div class="two columns">
			<a href="${ambit_root}/ui"><img class='scale-with-grid' border='0' src='${ambit_root}/images/profile/default/logo.png' title='Home' alt='enanoMapper logo'></a>
		</div>
		<div class="fourteen columns remove-bottom">
			<#include "/menu/profile/default/smartmenu.ftl">
		</div>
	</#if>

</div>
<div class="row remove-bottom" id="header">
	<#include "/toplinks.ftl">
</div>
<div class="row half-bottom">
<!--
		<div id='welcome' class="one column remove-bottom help">&nbsp;</div>
		-->
		<div class="ten columns remove-bottom" style="padding-left:10px;">
			<div id="breadCrumb" class="breadCrumb module remove-bottom h5">
                    <ul>
                        <li>
                            <a href="${ambit_root}" title="eNanoMapper protocol service">Home</a>
                        </li>
                    </ul>
			</div>
		</div>
</div>	


<script type='text/javascript'>
	$(document).ready(function() {    
		$( "#smartmenu" ).smartmenus();    
	});
 </script>