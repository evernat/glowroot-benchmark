/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.helloworld;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import com.example.helloworld.core.Person;
import com.example.helloworld.db.PersonDAO;
import com.example.helloworld.resources.PeopleResource;
import com.example.helloworld.resources.PersonResource;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.bull.javamelody.MonitoringFilter;

public class HelloJavaMelodyApplication extends Application<HelloWorldConfiguration> {

	public static void main(String[] args) throws Exception {
		new HelloJavaMelodyApplication().run(args);
	}

	private final HibernateBundle<HelloWorldConfiguration> hibernateBundle = new HibernateBundle<HelloWorldConfiguration>(
			Person.class) {
		@Override
		public DataSourceFactory getDataSourceFactory(HelloWorldConfiguration configuration) {
			return configuration.getDataSourceFactory();
		}
	};

	@Override
	public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
		bootstrap.addBundle(hibernateBundle);
	}

	@Override
	public void run(HelloWorldConfiguration configuration, Environment environment) {
		PersonDAO dao = new PersonDAO(hibernateBundle.getSessionFactory());
		environment.jersey().register(new PeopleResource(dao));
		environment.jersey().register(new PersonResource(dao));
		environment.servlets().addFilter("javamelody", new MonitoringFilter())
				.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
	}
}
