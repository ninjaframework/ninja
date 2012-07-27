package conf;

import models.CommentMapper;
import models.CommentMapperImpl;

import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.guice.MyBatisModule;
import org.mybatis.guice.datasource.builtin.PooledDataSourceProvider;
import org.mybatis.guice.datasource.helper.JdbcHelper;

public class NionjaMyBatisModule extends MyBatisModule {

    @Override
    protected void initialize() {
    	
        install(JdbcHelper.HSQLDB_Embedded);

        bindDataSourceProviderType(PooledDataSourceProvider.class);
        bindTransactionFactoryType(JdbcTransactionFactory.class);
        addMapperClass(CommentMapper.class);

        //Names.bindProperties(binder, createTestProperties());
        bind(CommentMapperImpl.class);
    }

}
