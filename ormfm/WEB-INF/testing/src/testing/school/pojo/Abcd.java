package testing.school.pojo;

import com.ashvin.orm.fm.annotations.*;

@Table(name="abcd")
public class Abcd
{
@Column(name="abcd")
@SetterGetter
private java.lang.String abcd;
public void setAbcd(java.lang.String abcd)
{
this.abcd=abcd;
}
public java.lang.String getAbcd()
{
return this.abcd;
}
@Column(name="pqr")
@SetterGetter
private java.lang.Integer pqr;
public void setPqr(java.lang.Integer pqr)
{
this.pqr=pqr;
}
public java.lang.Integer getPqr()
{
return this.pqr;
}
}
