/* Copyright 2012 SpringSource.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grails.plugin.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 * Changes the bean class of the org.springframework.cache.annotation.AnnotationCacheOperationSource#0
 * bean to a custom subclass.
 *
 * @author Burt Beckwith
 */
public class CacheBeanPostProcessor implements BeanDefinitionRegistryPostProcessor {

	protected Logger log = LoggerFactory.getLogger(getClass());

	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
		log.info("postProcessBeanDefinitionRegistry start");

		AbstractBeanDefinition beanDef = (AbstractBeanDefinition)registry.getBeanDefinition(
				"org.springframework.cache.annotation.AnnotationCacheOperationSource#0");

		// change the class to the plugin's subclass
		beanDef.setBeanClass(GrailsAnnotationCacheOperationSource.class);

		// wire in the dependency for the grailsApplication
		MutablePropertyValues props = beanDef.getPropertyValues();
		if (props == null) {
			props = new MutablePropertyValues();
			beanDef.setPropertyValues(props);
		}
		props.addPropertyValue("grailsApplication", new RuntimeBeanReference("grailsApplication", true));

		log.debug("updated {}", beanDef);
	}

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		log.info("postProcessBeanFactory");
	}
}
