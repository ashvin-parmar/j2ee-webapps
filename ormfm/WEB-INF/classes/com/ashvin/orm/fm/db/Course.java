import com.ashvin.orm.fm.annotations.*;

@Table(name="course")
class Course
{
  @PrimaryKey
    @AutoIncrement
    @Column(name="code")
    public java.lang.Integer code;
  @Column(name="title")
    public java.lang.String title;
}
