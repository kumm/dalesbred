/*
 * Copyright (c) 2015 Evident Solutions Oy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.dalesbred.integration.spring;

import org.dalesbred.Database;
import org.dalesbred.TestDatabaseProvider;
import org.dalesbred.transaction.Propagation;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SpringConfigurationTest {

    @Test
    public void dalesbredUsesConnectionBoundToSpringTransactions() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(SimpleConfiguration.class);
        DataSource dataSource = ctx.getBean(DataSource.class);
        Database db = ctx.getBean(Database.class);

        new TransactionTemplate(new DataSourceTransactionManager(dataSource)).execute(status ->
                db.withTransaction(Propagation.MANDATORY, tx -> {
                    assertThat(tx.getConnection(), is(DataSourceUtils.getConnection(dataSource)));
                    return "ok";
                }));
    }

    @Test
    public void rollbackForSpringTransactionDiscardsChangesOfDalesbred() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(SimpleConfiguration.class);
        DataSource dataSource = ctx.getBean(DataSource.class);
        Database db = ctx.getBean(Database.class);

        db.update("drop table if exists spring_tx_test");
        db.update("create table spring_tx_test (id int)");

        new TransactionTemplate(new DataSourceTransactionManager(dataSource)).execute(status -> {
            db.update("insert into spring_tx_test (id) values (1)");
            status.setRollbackOnly();
            return "";
        });

        assertThat(db.findUniqueInt("select count(*) from spring_tx_test"), is(0));
    }

    @Configuration
    public static class SimpleConfiguration extends DalesbredConfigurationSupport {

        @Bean
        public DataSource dataSource() {
            return TestDatabaseProvider.createInMemoryHSQLDataSource();
        }

        @Bean
        public PlatformTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }
    }
}
