## com.ashvin.web.rock: WebRock Framework Developer
  WebRock.java: WebRock servlet class used to manage all request arrived from client side. Fetch the RequestURI and based on path of request, respective service has been processed by searching it from WebRockModel. The respective method has been invoked using Reflection. No servlet classes has to being written.
  WebRockStarter.java: WebRockStarter servlet class used perform startup of WebRock Framework. It will load all necessary classes required and load all services with path in data-structure of WebRockModel class.

### .annotations
    There are all the annotations stored related to WebRock Framework.
    Path.java: It is annotation used to provided by the user to define the path on services that they provide. 
### .pojo
    There are all the Pojo classes required for WebRock Framework. 
    Service.java: It is Service classes which store class, method and its PATH in string form. 
### .model
    There are inner model related classes stored which has been used to create WebRock Framework
    WebRockModel.java: It is Model class for WebRock Framework to store path and thier services and manage them easily. 


## bobby.test: WebRock Framework User
  Student.java: There are nothing, Just a normal class.
  StudentManager.java: It is the class which used to written by user and used the provided PATH annotations by WebRock Framework. It will used by the framework to load the class services based on annotation value to WebRockModel data-structures. 
