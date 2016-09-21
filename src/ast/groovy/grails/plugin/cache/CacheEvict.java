/* Copyright 2012-2013 SpringSource.
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

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

import java.lang.annotation.*;

/**
 * Indicates that a method (or all methods on a class) trigger(s)
 * a cache invalidate operation.
 *
 * @author Jeff Brown
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@GroovyASTTransformationClass("org.grails.plugin.cache.compiler.CacheEvictTransformation")
public @interface CacheEvict {

	/*
	 * Qualifier value for the specified cached operation.
	 * <p>May be used to determine the target cache (or caches), matching the qualifier
	 * value (or the bean name(s)) of (a) specific bean definition.
	 */
	String[] value();
}
