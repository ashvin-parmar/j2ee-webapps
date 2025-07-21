import com.ashvin.hr.nexus.dl.*;
import java.util.*;
class test
{
public static void main(String gg[])
{
try
{
DesignationDAO dao=DesignationDAO.getDesignationDAO();
List<DesignationDTO> list=dao.getAllDesignations();
for(DesignationDTO dto:list)
{
System.out.println(dto.getCode());
System.out.println(dto.getTitle());
}
}catch(DAOException daoException)
{
System.out.println(daoException);
}
}
}
