package com.ashvin.web.rock.model;

import com.ashvin.web.rock.pojo.*;
import java.util.*;

public class WebRockModel
{
private HashMap<String,Service> pathServices;
private static final WebRockModel webRockModel=new WebRockModel();
private WebRockModel()
{
pathServices=new HashMap<>();
}
public static final WebRockModel getWebRockModel()
{
return WebRockModel.webRockModel;
}
public void setPathService(String path,Service service)
{
if(path.isBlank() || service==null) return;
//if(type.equals("GET")) GETpathServices.put(path,service);
//else if(type.equals("POST")) POSTpathServices.put(path,service);
pathServices.put(path,service);
}
public Service getPathService(String path)
{
if(path.isBlank()) return null;
//if(type.equals("GET")) return GETpathServices.get(path);
//else if(type.equals("POST")) return POSTpathServices.get(path);
return pathServices.get(path);
//return null;
}
public Service getPathService(String path,String type)
{
if(path.isBlank()) return null;
Service service;
if(type.equals("GET")) 
{
service=pathServices.get(path);
if(service!=null && service.isGetAllowed()) return service;
}
else if(type.equals("POST")) 
{
service=pathServices.get(path);
if(service!=null && service.isPostAllowed()) return service;
}
return null;
}
public List<Service> getServices()
{
List<Service> services=new ArrayList<>(pathServices.values());
return services;
}
}
