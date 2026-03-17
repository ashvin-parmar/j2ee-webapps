package com.ashvin.web.rock.model;

import com.ashvin.web.rock.pojo.*;
import java.util.*;

public class WebRockModel
{
private HashMap<String,Service> GETpathServices;
private HashMap<String,Service> POSTpathServices;
private static final WebRockModel webRockModel=new WebRockModel();
private WebRockModel()
{
GETpathServices=new HashMap<>();
POSTpathServices=new HashMap<>();
}
public static final WebRockModel getWebRockModel()
{
return WebRockModel.webRockModel;
}
public void setPathService(String path,Service service,String type)
{
if(path.isBlank() || service==null) return;
if(type.equals("GET")) GETpathServices.put(path,service);
else if(type.equals("POST")) POSTpathServices.put(path,service);
}
public Service getPathService(String path,String type)
{
if(path.isBlank()) return null;
if(type.equals("GET")) return GETpathServices.get(path);
else if(type.equals("POST")) return POSTpathServices.get(path);
return null;
}
}
