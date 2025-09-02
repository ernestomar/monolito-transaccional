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
    entityManagerFactoryRef = "accountingEntityManagerFactory",
    transactionManagerRef = "accountingTransactionManager",
    basePackages = {"bo.edu.ucb.monolito.accounting.repository"}
)
public class AccountingDataSourceConfig {

    @Bean(name = "accountingDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.accounting")
    public DataSource accountingDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "accountingEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean accountingEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("accountingDataSource") DataSource dataSource) {
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.format_sql", true);

        return builder
                .dataSource(dataSource)
                .packages("bo.edu.ucb.monolito.accounting.entity")
                .persistenceUnit("accounting")
                .properties(properties)
                .build();
    }

    @Bean(name = "accountingTransactionManager")
    public PlatformTransactionManager accountingTransactionManager(
            @Qualifier("accountingEntityManagerFactory") EntityManagerFactory accountingEntityManagerFactory) {
        return new JpaTransactionManager(accountingEntityManagerFactory);
    }
}
