{
	<#include "/apidocs/version.ftl" >
    "produces": [
        "application/json",
        "text/n3",
        "application/rdf+xml",
        "text/uri-list"
    ],
    "resourcePath": "/user",
    "apis": [
        {
            "path": "/user",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Retrieve list of users",
                    "notes": "Retrieve list of users <a href='http://api.toxbank.net/index.php/User' target='opentox'>User API</a>",
                    "type": "User",
                    "nickname": "getAllUsers",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
                        {
                            "name": "search",
                            "description": "prefix_of_firstname_or_lastname",
                            "required": false,
                            "type": "string",
                            "paramType": "query",
                            "allowMultiple": false
                        },		
                        	                                    {
                            "name": "username",
                            "description": "user name",
                            "required": false,
                            "type": "long",
                            "paramType": "query",
                            "allowMultiple": false
                        },
						<#include "/apidocs/parameters_page.ftl" >	                        
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "User not found"
                        },
						<#include "/apidocs/error_500.ftl" >	                        
                    ]
                }
            ]
        },
 		{
            "path": "/user/{id}",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Retrieve user details",
                    "notes": "Retrieve user details <a href='http://api.toxbank.net/index.php/User' target='opentox'>User API</a>",
                    "type": "User",
                    "nickname": "getUserID",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
                        {
                            "name": "id",
                            "description": "User identifier",
                            "required": false,
                            "type": "string",
                            "paramType": "path",
                            "allowMultiple": false
                        }                       
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Task not found"
                        },
                        <#include "/apidocs/error_task.ftl" >,                                   
						<#include "/apidocs/error_500.ftl" >	     
                    ]
                },
                {
                    "method": "POST",
                    "summary": "Creates an user",
                    "notes": "Creates an user <a href='http://api.toxbank.net/index.php/User' target='opentox'>User API</a>",
                    "type": "Task",
                    "nickname": "createUser",
					"consumes": [
		                       "application/x-www-form-urlencoded"
		                ],			                    
     				 <#include "/apidocs/authz.ftl" >
                    "parameters": [
							{
							    "name": "username",
							    "description": "username",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false
							},	
							{
							    "name": "title",
							    "description": "title",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false
							},
							{
							    "name": "firstname",
							    "description": "firstname",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": true
							},
							{
							    "name": "lastname",
							    "description": "lastname",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": true
							},
							{
							    "name": "institute",
							    "description": "institute",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false
							},		
							{
							    "name": "weblog",
							    "description": "weblog",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": true
							},	
							{
							    "name": "homepage",
							    "description": "homepage",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false
							},	
							{
							    "name": "email",
							    "description": "email",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false
							}														
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Task not found"
                        },
                        <#include "/apidocs/error_task.ftl" >,                                   
						<#include "/apidocs/error_500.ftl" >	     
                    ]
                }                
            ]
        }        
    ],
    "models" : {
      "User" : <#include "/apidocs/json_schema_user.ftl" >  ,
      "Task" : <#include "/apidocs/json_schema_task.ftl" >  	  
    },    
    <#include "/apidocs/profile/${menu_profile}/info.ftl" >  
}