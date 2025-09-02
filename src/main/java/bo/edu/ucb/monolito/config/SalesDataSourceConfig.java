package bo.edu.ucb.monolito.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    entityManagerFactoryRef = "salesEntityManagerFactory",
    transactionManagerRef = "salesTransactionManager",
    basePackages = {"bo.edu.ucb.monolito.sales.repository"}
)
public class SalesDataSourceConfig {

    @Bean(name = "salesDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.sales")
    public DataSource salesDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "salesEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean salesEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("salesDataSource") DataSource dataSource) {
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.format_sql", true);

        return builder
                .dataSource(dataSource)
                .packages("bo.edu.ucb.monolito.sales.entity")
                .persistenceUnit("sales")
                .properties(properties)
                .build();
    }

    @Bean(name = "salesTransactionManager")
    public PlatformTransactionManager salesTransactionManager(
            @Qualifier("salesEntityManagerFactory") EntityManagerFactory salesEntityManagerFactory) {
        return new JpaTransactionManager(salesEntityManagerFactory);
    }
}
