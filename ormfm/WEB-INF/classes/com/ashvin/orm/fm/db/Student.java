import com.ashvin.orm.fm.annotations.*;

@Table(name="student")
class Student
{
@PrimaryKey
@Column(name="roll_number")
@SetterGetter
private java.lang.Integer rollNumber;
public void setRollNumber(java.lang.Integer rollNumber)
{
this.rollNumber=rollNumber;
}
public java.lang.Integer getRollNumber()
{
return this.rollNumber;
}
@Column(name="first_name")
@SetterGetter
private java.lang.String firstName;
public void setFirstName(java.lang.String firstName)
{
this.firstName=firstName;
}
public java.lang.String getFirstName()
{
return this.firstName;
}
@Column(name="last_name")
@SetterGetter
private java.lang.String lastName;
public void setLastName(java.lang.String lastName)
{
this.lastName=lastName;
}
public java.lang.String getLastName()
{
return this.lastName;
}
@Column(name="aadhar_card_number")
@SetterGetter
@Unique
private java.lang.String aadharCardNumber;
public void setAadharCardNumber(java.lang.String aadharCardNumber)
{
this.aadharCardNumber=aadharCardNumber;
}
public java.lang.String getAadharCardNumber()
{
return this.aadharCardNumber;
}
@ForeignKey(parent="course",column="code")
@Column(name="course_code")
@SetterGetter
private java.lang.Integer courseCode;
public void setCourseCode(java.lang.Integer courseCode)
{
this.courseCode=courseCode;
}
public java.lang.Integer getCourseCode()
{
return this.courseCode;
}
@Column(name="gender")
@SetterGetter
private java.lang.String gender;
public void setGender(java.lang.String gender)
{
this.gender=gender;
}
public java.lang.String getGender()
{
return this.gender;
}
@Column(name="date_of_birth")
@SetterGetter
private java.util.Date dateOfBirth;
public void setDateOfBirth(java.util.Date dateOfBirth)
{
this.dateOfBirth=dateOfBirth;
}
public java.util.Date getDateOfBirth()
{
return this.dateOfBirth;
}
}
