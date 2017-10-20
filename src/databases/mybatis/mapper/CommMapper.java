package databases.mybatis.mapper;

import org.apache.ibatis.annotations.Param;
import server.comm.DataMap;

public interface CommMapper {

    DataMap test(@Param("UUID") String UUID);

}
