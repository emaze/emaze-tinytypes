package net.emaze.tinytypes;

import java.io.IOException;
import java.util.Properties;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.sql.DataSource;
import net.emaze.tinytypes.HibernateTest.Configuration;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.DefaultComponentSafeNamingStrategy;
import org.hsqldb.jdbcDriver;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author rferranti
 */
@ContextConfiguration(classes = Configuration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class HibernateTest {

    @Autowired
    private SessionFactory hibernate;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    public void canSerializeAndDeserializeALongTinyType() {
        transactionTemplate.execute((status) -> {
            final LongBean mb = new LongBean();
            mb.setId(new SampleLongTinyType(123));
            hibernate.getCurrentSession().merge(mb);
            return null;
        });

        final SampleLongTinyType got = transactionTemplate.execute((status) -> {
            final LongBean b = (LongBean) hibernate.getCurrentSession().load(LongBean.class, new SampleLongTinyType(123));
            return b.getId();
        });

        Assert.assertEquals(123, got.value);
    }

    @Test
    public void canSerializeAndDeserializeAFWLongTinyType() {
        transactionTemplate.execute((status) -> {
            final FlyWeightedLongBean mb = new FlyWeightedLongBean();
            mb.setId(new SampleFlyweightedLongTinyType(123));
            hibernate.getCurrentSession().merge(mb);
            return null;
        });

        final SampleFlyweightedLongTinyType got = transactionTemplate.execute((status) -> {
            final FlyWeightedLongBean b = (FlyWeightedLongBean) hibernate.getCurrentSession().load(FlyWeightedLongBean.class, new SampleFlyweightedLongTinyType(123));
            return b.getId();
        });

        Assert.assertEquals(123, got.value);
    }

    @Test
    public void canSerializeAndDeserializeAnIntTinyType() {
        transactionTemplate.execute((status) -> {
            final IntBean mb = new IntBean();
            mb.setId(new SampleIntTinyType(123));
            hibernate.getCurrentSession().merge(mb);
            return null;
        });

        final SampleIntTinyType got = transactionTemplate.execute((status) -> {
            final IntBean b = (IntBean) hibernate.getCurrentSession().load(IntBean.class, new SampleIntTinyType(123));
            return b.getId();
        });

        Assert.assertEquals(123, got.value);
    }

    @Test
    public void canSerializeAndDeserializeAShortTinyType() {
        transactionTemplate.execute((status) -> {
            final ShortBean mb = new ShortBean();
            mb.setId(new SampleShortTinyType((short) 123));
            hibernate.getCurrentSession().merge(mb);
            return null;
        });

        final SampleShortTinyType got = transactionTemplate.execute((status) -> {
            final ShortBean b = (ShortBean) hibernate.getCurrentSession().load(ShortBean.class, new SampleShortTinyType((short) 123));
            return b.getId();
        });

        Assert.assertEquals(123, got.value);
    }

    @Test
    public void canSerializeAndDeserializeAByteTinyType() {
        transactionTemplate.execute((status) -> {
            final ByteBean mb = new ByteBean();
            mb.setId(new SampleByteTinyType((byte) 123));
            hibernate.getCurrentSession().merge(mb);
            return null;
        });

        final SampleByteTinyType got = transactionTemplate.execute((status) -> {
            final ByteBean b = (ByteBean) hibernate.getCurrentSession().load(ByteBean.class, new SampleByteTinyType((byte) 123));
            return b.getId();
        });

        Assert.assertEquals(123, got.value);
    }

    @Test
    public void canSerializeAndDeserializeABooleanTinyType() {
        transactionTemplate.execute((status) -> {
            final BooleanBean mb = new BooleanBean();
            mb.setId(new SampleBooleanTinyType(true));
            hibernate.getCurrentSession().merge(mb);
            return null;
        });

        final SampleBooleanTinyType got = transactionTemplate.execute((status) -> {
            final BooleanBean b = (BooleanBean) hibernate.getCurrentSession().load(BooleanBean.class, new SampleBooleanTinyType(true));
            return b.getId();
        });

        Assert.assertEquals(true, got.value);
    }

    @Test
    public void canSerializeAndDeserializeAStringTinyType() {
        transactionTemplate.execute((status) -> {
            final StringBean mb = new StringBean();
            mb.setId(new SampleStringTinyType("123"));
            hibernate.getCurrentSession().merge(mb);
            return null;
        });

        final SampleStringTinyType got = transactionTemplate.execute((status) -> {
            final StringBean b = (StringBean) hibernate.getCurrentSession().load(StringBean.class, new SampleStringTinyType("123"));
            return b.getId();
        });

        Assert.assertEquals("123", got.value);
    }

    @Entity
    @Table(name = "intbean")
    public static class IntBean {

        @Id
        private SampleIntTinyType id;

        public SampleIntTinyType getId() {
            return id;
        }

        public void setId(SampleIntTinyType id) {
            this.id = id;
        }

    }

    @Entity
    @Table(name = "bytebean")
    public static class ByteBean {

        @Id
        private SampleByteTinyType id;

        public SampleByteTinyType getId() {
            return id;
        }

        public void setId(SampleByteTinyType id) {
            this.id = id;
        }

    }

    @Entity
    @Table(name = "booleanbean")
    public static class BooleanBean {

        @Id
        private SampleBooleanTinyType id;

        public SampleBooleanTinyType getId() {
            return id;
        }

        public void setId(SampleBooleanTinyType id) {
            this.id = id;
        }

    }

    @Entity
    @Table(name = "shortbean")
    public static class ShortBean {

        @Id
        private SampleShortTinyType id;

        public SampleShortTinyType getId() {
            return id;
        }

        public void setId(SampleShortTinyType id) {
            this.id = id;
        }

    }

    @Entity
    @Table(name = "longbean")
    public static class LongBean {

        @Id
        private SampleLongTinyType id;

        public SampleLongTinyType getId() {
            return id;
        }

        public void setId(SampleLongTinyType id) {
            this.id = id;
        }

    }

    @Entity
    @Table(name = "fwlongbean")
    public static class FlyWeightedLongBean {

        @Id
        private SampleFlyweightedLongTinyType id;

        public SampleFlyweightedLongTinyType getId() {
            return id;
        }

        public void setId(SampleFlyweightedLongTinyType id) {
            this.id = id;
        }

    }

    @Entity
    @Table(name = "stringbean")
    public static class StringBean {

        @Id
        private SampleStringTinyType id;

        public SampleStringTinyType getId() {
            return id;
        }

        public void setId(SampleStringTinyType id) {
            this.id = id;
        }

    }

    public static class Configuration {

        @Bean
        public DataSource dataSource() {
            jdbcDriver driver = new org.hsqldb.jdbcDriver();
            return new SimpleDriverDataSource(driver, "jdbc:hsqldb:mem:tinytypes-test", "sa", "");
        }

        @Bean
        public PlatformTransactionManager txManager(SessionFactory sessionFatory) {
            final HibernateTransactionManager bean = new HibernateTransactionManager();
            bean.setSessionFactory(sessionFatory);
            return bean;
        }

        @Bean
        public TransactionTemplate transactionTemplate(PlatformTransactionManager ptm) {
            return new TransactionTemplate(ptm);
        }

        @Bean
        public LocalSessionFactoryBean localSessionFactoryBean(final DataSource dataSource) throws IOException, Exception {
            final Properties hibernateProperties = new Properties();
            hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
            hibernateProperties.put("hibernate.hbm2ddl.auto", "create-drop");
            hibernateProperties.put("hibernate.default_batch_fetch_size", "200");
            hibernateProperties.put("hibernate.tinytypes.location.pattern", "classpath*:/net/emaze/**/*.class");
            final LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
            factoryBean.setDataSource(dataSource);
            factoryBean.setMappingLocations(new org.springframework.core.io.Resource[0]);
            factoryBean.setPackagesToScan(new String[]{"net.emaze.tinytypes"});
            factoryBean.setNamingStrategy(new DefaultComponentSafeNamingStrategy());
            factoryBean.setHibernateProperties(hibernateProperties);
            return factoryBean;
        }

    }

}
