package bo.edu.ucb.monolito.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import jakarta.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "warehouseEntityManagerFactory",
    transactionManagerRef = "warehouseTransactionManager",
    basePackages = {"bo.edu.ucb.monolito.warehouse.repository"}
)
public class WarehouseDataSourceConfig {

    @Primary
    @Bean(name = "warehouseDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.warehouse")
    public DataSource warehouseDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "warehouseEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean warehouseEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("warehouseDataSource") DataSource dataSource) {
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.format_sql", true);

        return builder
                .dataSource(dataSource)
                .packages("bo.edu.ucb.monolito.warehouse.entity")
                .persistenceUnit("warehouse")
                .properties(properties)
                .build();
    }

    @Primary
    @Bean(name = "warehouseTransactionManager")
    public PlatformTransactionManager warehouseTransactionManager(
            @Qualifier("warehouseEntityManagerFactory") EntityManagerFactory warehouseEntityManagerFactory) {
        return new JpaTransactionManager(warehouseEntityManagerFactory);
    }
}
