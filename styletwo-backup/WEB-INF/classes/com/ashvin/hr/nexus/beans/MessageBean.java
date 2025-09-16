package com.ashvin.hr.nexus.beans;

public class MessageBean implements java.io.Serializable
{
private String message;
private String heading;
private boolean hasToGenerateButtons;
private boolean hasToGenerateTwoButtons;
private String buttonOneText;
private String buttonOneAction;
private String buttonTwoText;
private String buttonTwoAction;
public MessageBean()
{
this.message="";
this.heading="";
this.hasToGenerateButtons=false;
this.hasToGenerateTwoButtons=false;
this.buttonOneText="";
this.buttonOneAction="";
this.buttonTwoText="";
this.buttonTwoAction="";
}
public void setMessage(String message)
{
this.message=message;
}
public String getMessage()
{
return this.message;
}
public void setHeading(String heading)
{
this.heading=heading;
}
public String getHeading()
{
return this.heading;
}
public void setHasToGenerateButtons(boolean hasToGenerateButtons)
{
this.hasToGenerateButtons=hasToGenerateButtons;
}
public boolean getHasToGenerateButtons()
{
return this.hasToGenerateButtons;
}
public void setHasToGenerateTwoButtons(boolean hasToGenerateTwoButtons)
{
this.hasToGenerateTwoButtons=hasToGenerateTwoButtons;
}
public boolean getHasToGenerateTwoButtons()
{
return this.hasToGenerateTwoButtons;
}
public void setButtonOneText(String buttonOneText)
{
this.buttonOneText=buttonOneText;
}
public String getButtonOneText()
{
return this.buttonOneText;
}
public void setButtonOneAction(String buttonOneAction)
{
this.buttonOneAction=buttonOneAction;
}
public String getButtonOneAction()
{
return this.buttonOneAction;
}
public void setButtonTwoText(String buttonTwoText)
{
this.buttonTwoText=buttonTwoText;
}
public String getButtonTwoText()
{
return this.buttonTwoText;
}
public void setButtonTwoAction(String buttonTwoAction)
{
this.buttonTwoAction=buttonTwoAction;
}
public String getButtonTwoAction()
{
return this.buttonTwoAction;
}
}
