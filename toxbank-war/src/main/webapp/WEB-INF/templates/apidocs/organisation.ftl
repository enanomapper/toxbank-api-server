{
	<#include "/apidocs/version.ftl" >
    "produces": [
        "application/json",
        "text/n3",
        "application/rdf+xml",
        "text/uri-list"
    ],
    "resourcePath": "/organisation",
    "apis": [
        {
            "path": "/organisation",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Retrieve list of organisations",
                    "notes": "Retrieve list of organisations <a href='http://api.toxbank.net/index.php/Organisation' target='opentox'>Organisation API</a>",
                    "type": "Organisation",
                    "nickname": "getAllOrganisations",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
                        {
                            "name": "search",
                            "description": "prefix_name",
                            "required": false,
                            "type": "string",
                            "paramType": "query",
                            "allowMultiple": false
                        },		    
						<#include "/apidocs/parameters_page.ftl" >	                        
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "organisation not found"
                        },
						<#include "/apidocs/error_500.ftl" >	                        
                    ]
                }
            ]
        },
 		{
            "path": "/organisation/{id}",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Retrieves a single organisation",
                    "notes": "Retrieve organisation <a href='http://api.toxbank.net/index.php/Organisation' target='opentox'>Organisation API</a>",
                    "type": "Organisation",
                    "nickname": "getOrganisationID",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
                        {
                            "name": "id",
                            "description": "organisation identifier",
                            "required": false,
                            "type": "string",
                            "paramType": "path",
                            "allowMultiple": false
                        }                       
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "organisation not found"
                        },
                        <#include "/apidocs/error_task.ftl" >,                                   
						<#include "/apidocs/error_500.ftl" >	     
                    ]
                },
                {
                    "method": "POST",
                    "summary": "Creates an organisation",
                    "notes": "Creates a organisation <a href='http://api.toxbank.net/index.php/Organisation' target='opentox'>Organisation API</a>",
                    "type": "Task",
                    "nickname": "createOrganisation",
					"consumes": [
		                       "application/x-www-form-urlencoded"
		                ],			                    
     				 <#include "/apidocs/authz.ftl" >
                    "parameters": [
							{
							    "name": "title",
							    "description": "title",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false
							},	
							{
							    "name": "ldapgroup",
							    "description": "ldapgroup",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false
							}
																				
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "organisation not found"
                        },
                        <#include "/apidocs/error_task.ftl" >,                                   
						<#include "/apidocs/error_500.ftl" >	     
                    ]
                }                
            ]
        }        
    ],
    "models" : {
      "Organisation" : <#include "/apidocs/json_schema_organisation.ftl" >  ,
      "Task" : <#include "/apidocs/json_schema_task.ftl" >  	  
    },    
    <#include "/apidocs/profile/${menu_profile}/info.ftl" >  
}