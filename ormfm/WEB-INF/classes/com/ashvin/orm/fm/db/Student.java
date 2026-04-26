import com.ashvin.orm.fm.annotations.*;

@Table(name="student")
class Student
{
@PrimaryKey
@Column(name="rollNumber")
public java.lang.Integer rollNumber;
@Column(name="firstName")
public java.lang.String firstName;
@Column(name="lastName")
public java.lang.String lastName;
@Column(name="aadharCardNumber")
public java.lang.String aadharCardNumber;
@Column(name="courseCode")
public java.lang.Integer courseCode;
@Column(name="gender")
public java.lang.String gender;
@Column(name="dateOfBirth")
public java.util.Date dateOfBirth;
}
