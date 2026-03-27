package bobby.test;
import com.ashvin.web.rock.annotations.*;

@GET
@PATH("/runOnStartupTesting")
public class RunOnStartupTesting
{
@ONSTARTUP(priority=3)
@PATH("/testing1")
public void testing1()
{
System.out.println("Testing 1 with priority 3");
}
@PATH("/testing2")
@ONSTARTUP(priority=1)
@POST
public void testing2()
{
System.out.println("Testing 2 with priority 1");
}
@PATH("/testing3")
@ONSTARTUP(priority=0)
public void testing3()
{
System.out.println("Testing 3 with priority 0");
}
@PATH("/testing4")
@ONSTARTUP(priority=-1)
public void testing4()
{
System.out.println("Testing 4 with priority -1");
}
}
