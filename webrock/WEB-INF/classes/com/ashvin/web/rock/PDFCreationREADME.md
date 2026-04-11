PDF Creation feature

1. iText pdf creation libraries are used to create PDF in Java.
2. We have created Nested table to store all data inside table(s) with specific format. Neat and clean way to store and show data.
3. PDF File name is based on site-name. Heading title is based on site-name.
4. Logo inside pdf has to been set from configuration [pending].
5. All package(s) which are used are stored inside [/WEB-INF/lib] folder.
----------------------------------------------------------------------------
6. Data stored format provided in image1 and image2.
Required fields: ==> 
  * PATH
  * Class
  * GET Allowed [Yes/No]
  * POST Allowed [Yes/No]
  * Method Name
  * Return Type
Changing fields: ==>
  * Parameter(s): if not available -> [void]
                  otherwise -> Nested Table
  * Error(s): if not available -> [--no exception--]
                  otherwise -> Nested Table
  * Is run on start-up: if not available -> [--lazy loading--]
                  otherwise -> [Start-up Priority is provided]
  * Request forwarding: if not available -> [--no-forwarding--]
                  otherwise -> [to 'forwarded-service-path']
  * Security Access: If not available -> [--no-security--]
                  otherwise -> if invalid security => [--invalid--]
                               otherwise => [checkpost and guard provided]
  * Auto Wired: if not available -> [--no auto wired fields--]
                  otherwise -> Nested Table
  * Inject Request parameter(s): if not available -> [--no injection for query-string --]
                  otherwise -> Nested Table
  * Injection of application scope: [Yes/No]
  * Injection of session scope: [Yes/No]
  * Injection of request scope: [Yes/No]
  * Injection of application directory: [Yes/No]
----------------------------------------------------------------------------

7. For Parameter(s) -> There are nested table is created with three columns. 
  * one for Parmeter number
  * Take data inputs from where? [if some name -> [RequestParameter],"--json data arrived--","--autofilled"]  
      * "--json data-arrived--": means data are arrived in json format, and fetched from request.
      * "--autofilled--" means data are of ApplicationScope, SessionScope, RequestScope or ApplicationDirectory type, which are auto initialized. 
      * if Some Name : Means data are arrived in query-string with specified name and has been initialized from there. 
8. Error(s): These are all the exceptions available on that method provided while writing this method.
