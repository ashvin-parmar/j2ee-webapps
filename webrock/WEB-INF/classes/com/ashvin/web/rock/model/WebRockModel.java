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
pathServices.put(path,service);
}
public Service getPathService(String path)
{
if(path.isBlank()) return null;
return pathServices.get(path);
}

}
