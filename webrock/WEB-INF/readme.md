## [/WEB-INF/js] folder
Here, [/WEB-INF/js] folder is auto generated, which store generated java-script file(s) using WebRock framework. These (.js) file(s) contains pojo and service classes. All service(s) class's methods return a Promise. These Promise has been used to manage request and response by providing resolve and reject at the client-end.

### configuration(s)
To provide a specific file name to generated js-file, we have to provide the given configuration in web.xml. 
```
<context-param>
<param-name>JS_FILE_NAME</param-name>
<param-value>pojo_service.js</param-value>
</context-param>
```

### To create JS files for POJO classes
We have to set @POJO annotation on all those classes which are used as pojo classes. All DTO, Beans class are pojo classes. 
@POJO("something")  -> something.js is created
@POJO               -> class_name.js is created (: where class_name is that pojo class name)

### To create JS files for Service classes
We have to set @PATH annotation on all those service(s) classes. That's mandatory. 
@PATH("/something") -> something.js is created


### POST and GET method selection
If Query String available and GET allowed then GET method is applied on given service in service class.
otherwise, If JSON data arrived and one parameter is of any type and POST method allowed then POST method is applied on given service in service class.
Convering all situation, all services are fulfilled. if anything that not rely on those two conditions, then respective service is skiped.

