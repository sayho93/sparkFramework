package databases.mybatis.mapper;

import org.apache.ibatis.annotations.Param;
import server.comm.DataMap;

/**
 * Created by p on 2017-10-20.
 */
public interface AdminMapper {

    DataMap getAdmin(@Param("no") int no);

    DataMap AdminLogin(@Param("id") String id, @Param("pwd") String pwd);

}
