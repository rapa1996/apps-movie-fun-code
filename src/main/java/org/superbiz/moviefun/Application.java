package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.superbiz.moviefun.movies.MoviesBean;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class Application {

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ServletRegistrationBean actionServletRegistration(ActionServlet actionServlet) {
        return new ServletRegistrationBean(actionServlet, "/moviefun/*");
    }

    @Bean
    DatabaseServiceCredentials databaseServiceCredentials(@Value("${VCAP_SERVICES}") String vcapVariable){
        return new DatabaseServiceCredentials(vcapVariable);
    }

    @Bean
    public DataSource albumsDataSource(DatabaseServiceCredentials databaseServiceCredentials) {
        HikariConfig albumsHikariDataSource = new HikariConfig();

        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(databaseServiceCredentials.jdbcUrl("albums-mysql", "p-mysql"));

        albumsHikariDataSource.setDataSource(dataSource);

        return new HikariDataSource(albumsHikariDataSource);
    }

    @Bean
    public DataSource moviesDataSource(DatabaseServiceCredentials databaseServiceCredentials) {
        HikariConfig moviesHikariDataSource = new HikariConfig();

        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(databaseServiceCredentials.jdbcUrl("movies-mysql", "p-mysql"));

        moviesHikariDataSource.setDataSource(dataSource);

        return new HikariDataSource(moviesHikariDataSource);
    }

    @Bean
    HibernateJpaVendorAdapter jpaVendorAdapter(){
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setGenerateDdl(true);
        jpaVendorAdapter.setDatabase(Database.MYSQL);
        jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        return jpaVendorAdapter;
    }

    @Bean
    LocalContainerEntityManagerFactoryBean moviesEntityManagerFactory(DataSource moviesDataSource, HibernateJpaVendorAdapter jpaVendorAdapter){
        LocalContainerEntityManagerFactoryBean localContainer = new LocalContainerEntityManagerFactoryBean();
        localContainer.setDataSource(moviesDataSource);
        localContainer.setJpaVendorAdapter(jpaVendorAdapter);
        localContainer.setPackagesToScan(Application.class.getPackage().getName());
        localContainer.setPersistenceUnitName("movie");
        return localContainer;
    }

    @Bean
    LocalContainerEntityManagerFactoryBean albumsEntityManagerFactory(DataSource albumsDataSource, HibernateJpaVendorAdapter jpaVendorAdapter){
        LocalContainerEntityManagerFactoryBean localContainer = new LocalContainerEntityManagerFactoryBean();
        localContainer.setDataSource(albumsDataSource);
        localContainer.setJpaVendorAdapter(jpaVendorAdapter);
        localContainer.setPackagesToScan(Application.class.getPackage().getName());
        localContainer.setPersistenceUnitName("album");
        return localContainer;
    }

    @Bean
    PlatformTransactionManager moviesTransactionManager(EntityManagerFactory moviesEntityManagerFactory){
        return new JpaTransactionManager(moviesEntityManagerFactory);
    }

    @Bean
    PlatformTransactionManager albumsTransactionManager(EntityManagerFactory albumsEntityManagerFactory){
        return new JpaTransactionManager(albumsEntityManagerFactory);
    }
}
