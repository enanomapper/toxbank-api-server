				"authorizations": {
				<#if menu_profile??>
					<#include "/apidocs/profile/${menu_profile}/authz.ftl" >
				<#else>
					<#include "/apidocs/profile/default/authz.ftl" >
				</#if>				
				},