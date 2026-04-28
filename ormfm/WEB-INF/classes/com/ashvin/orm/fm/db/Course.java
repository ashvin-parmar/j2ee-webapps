import com.ashvin.orm.fm.annotations.*;

@Table(name="course")
class Course
{
@PrimaryKey
@AutoIncrement
@Column(name="code")
@SetterGetter
private java.lang.Integer code;
public void setCode(java.lang.Integer code)
{
this.code=code;
}
public java.lang.Integer getCode()
{
return this.code;
}
@Column(name="title")
@SetterGetter
private java.lang.String title;
public void setTitle(java.lang.String title)
{
this.title=title;
}
public java.lang.String getTitle()
{
return this.title;
}
}
