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

import java.lang.reflect.Method;
import java.util.Collection;

import org.codehaus.groovy.grails.commons.ControllerArtefactHandler;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.cache.interceptor.CacheOperation;

/**
 * getCacheOperations is called when beans are initialized and also from
 * PageFragmentCachingFilter during requests; the filter needs annotations on
 * controllers but if the standard lookup includes controllers, the return
 * values from the controller method calls are cached unnecessarily.
 *
 * @author Burt Beckwith
 */
public class GrailsAnnotationCacheOperationSource extends AnnotationCacheOperationSource {

	private static final long serialVersionUID = 1;

	protected GrailsApplication application;

	public Collection<CacheOperation> getCacheOperations(Method method, Class<?> targetClass,
			boolean includeControllers) {

		if (!includeControllers && isControllerClass(targetClass)) {
			return null;
		}

		// will typically be called with includeControllers = true (i.e. from the filter)
		// so controller methods will be considered
		return super.getCacheOperations(method, targetClass);
	}

	@Override
	public Collection<CacheOperation> getCacheOperations(Method method, Class<?> targetClass) {

		// when called directly excluded controllers

		if (isControllerClass(targetClass)) {
			return null;
		}

		return super.getCacheOperations(method, targetClass);
	}

	protected boolean isControllerClass(Class<?> targetClass) {
		return application.isArtefactOfType(ControllerArtefactHandler.TYPE, targetClass);
	}

	/**
	 * Dependency injection for the grails application.
	 * @param grailsApplication the app
	 */
	public void setGrailsApplication(GrailsApplication grailsApplication) {
		application = grailsApplication;
	}
}
