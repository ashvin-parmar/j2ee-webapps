package com.smart.carbonix.beans.common;

public class MessageBean implements java.io.Serializable
{
private String message;
private String heading;
private Boolean generateButtons;
private Boolean generateTwoButtons;
private String buttonOneText;
private String buttonTwoText;
private String buttonOneAction;
private String buttonTwoAction;
public MessageBean()
{
this.message="";
this.heading="";
this.generateButtons=false;
this.generateTwoButtons=false;
this.buttonOneText="";
this.buttonTwoText="";
this.buttonOneAction="";
this.buttonTwoAction="";
}
public void setMessage(java.lang.String message)
{
this.message=message;
}
public java.lang.String getMessage()
{
return this.message;
}
public void setHeading(java.lang.String heading)
{
this.heading=heading;
}
public java.lang.String getHeading()
{
return this.heading;
}
public void setGenerateButtons(java.lang.Boolean generateButtons)
{
this.generateButtons=generateButtons;
}
public java.lang.Boolean getGenerateButtons()
{
return this.generateButtons;
}
public void setGenerateTwoButtons(java.lang.Boolean generateTwoButtons)
{
this.generateTwoButtons=generateTwoButtons;
}
public java.lang.Boolean getGenerateTwoButtons()
{
return this.generateTwoButtons;
}
public void setButtonOneText(java.lang.String buttonOneText)
{
this.buttonOneText=buttonOneText;
}
public java.lang.String getButtonOneText()
{
return this.buttonOneText;
}
public void setButtonTwoText(java.lang.String buttonTwoText)
{
this.buttonTwoText=buttonTwoText;
}
public java.lang.String getButtonTwoText()
{
return this.buttonTwoText;
}
public void setButtonOneAction(java.lang.String buttonOneAction)
{
this.buttonOneAction=buttonOneAction;
}
public java.lang.String getButtonOneAction()
{
return this.buttonOneAction;
}
public void setButtonTwoAction(java.lang.String buttonTwoAction)
{
this.buttonTwoAction=buttonTwoAction;
}
public java.lang.String getButtonTwoAction()
{
return this.buttonTwoAction;
}
}
