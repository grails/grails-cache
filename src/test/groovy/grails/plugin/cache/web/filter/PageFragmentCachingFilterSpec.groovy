package grails.plugin.cache.web.filter

import grails.core.GrailsApplication
import grails.plugin.cache.Cacheable
import grails.plugin.cache.GrailsAnnotationCacheOperationSource
import grails.plugin.cache.web.ContentCacheParameters
import grails.plugin.cache.web.PageInfo
import grails.util.Holders
import org.grails.plugins.testing.GrailsMockHttpServletRequest
import org.grails.plugins.testing.GrailsMockHttpServletResponse
import org.grails.web.mapping.mvc.UrlMappingsHandlerMapping
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.cache.Cache
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.cache.interceptor.CacheEvictOperation
import org.springframework.cache.interceptor.CachePutOperation
import org.springframework.cache.interceptor.CacheableOperation
import org.springframework.expression.EvaluationContext
import org.springframework.mock.web.MockFilterChain
import org.springframework.web.context.request.RequestContextHolder
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Method

class PageFragmentCachingFilterSpec extends Specification {

	GrailsApplication grailsApplication
	GrailsMockHttpServletRequest grailsMockHttpServletRequest
	GrailsMockHttpServletResponse grailsMockHttpServletResponse
	GrailsWebRequest requestAttributes
	MockFilterChain mockFilterChain
	ContentCacheParameters contentCacheParameters
	GrailsAnnotationCacheOperationSource grailsAnnotationCacheOperationSource
	ExpressionEvaluator expressionEvaluator
	EvaluationContext evalContext
	WebKeyGenerator keyGenerator
	UrlMappingsHandlerMapping urlMappingsHandlerMapping

	def setup() {
		grailsMockHttpServletRequest = new GrailsMockHttpServletRequest()
		grailsMockHttpServletResponse = new GrailsMockHttpServletResponse()
		mockFilterChain = new MockFilterChain()
		grailsApplication = Mock(GrailsApplication)
		requestAttributes = Mock(GrailsWebRequest)
		expressionEvaluator = Mock(ExpressionEvaluator)
		contentCacheParameters = Mock(ContentCacheParameters)
		grailsAnnotationCacheOperationSource = Mock(GrailsAnnotationCacheOperationSource)
		evalContext = Mock(EvaluationContext)
		keyGenerator = Mock(WebKeyGenerator)
		urlMappingsHandlerMapping = Mock(UrlMappingsHandlerMapping)
	}

	@Unroll
	def "test normal flow through a cache filter #cacheType"() {
		given:
		GrailsFilter filter = new GrailsFilter()
		filter.cacheOperationSource = grailsAnnotationCacheOperationSource
		filter.expressionEvaluator = expressionEvaluator
		filter.keyGenerator = keyGenerator
		RequestContextHolder.setRequestAttributes(requestAttributes)
		Holders.setGrailsApplication(grailsApplication)

		when:
		filter.doFilter(grailsMockHttpServletRequest, grailsMockHttpServletResponse, mockFilterChain)

		then:
		1 * requestAttributes.getControllerName() >> "test"
		1 * grailsApplication.getArtefactForFeature('Controller', '/test')
		1 * contentCacheParameters.getMethod() >> MockController.getMethod('cacheMethod')
		1 * grailsAnnotationCacheOperationSource.getCacheOperations(_ as Method, _, true) >> [cacheType]
		1 * expressionEvaluator.createEvaluationContext(_, _, _, _) >> evalContext
		3 * keyGenerator.generate(_) >> "key"
//		1 * contentCacheParameters.getControllerName() >> "mock"
//		1 * contentCacheParameters.getActionName() >> "cacheMethod"

		where:
		cacheType << [new CachePutOperation(name: 'CachePutOperation'), new CacheableOperation(name: 'CacheableOperation')]
	}

	def "test normal flow through a cache filter CacheEvictOperation"() {
		given:
		GrailsFilter filter = new GrailsFilter()
		filter.cacheOperationSource = grailsAnnotationCacheOperationSource
		filter.expressionEvaluator = expressionEvaluator
		filter.keyGenerator = keyGenerator
		RequestContextHolder.setRequestAttributes(requestAttributes)
		Holders.setGrailsApplication(grailsApplication)

		when:
		filter.doFilter(grailsMockHttpServletRequest, grailsMockHttpServletResponse, mockFilterChain)

		then:
		1 * requestAttributes.getControllerName() >> "test"
		1 * grailsApplication.getArtefactForFeature('Controller', '/test')
		1 * contentCacheParameters.getMethod() >> MockController.getMethod('cacheMethod')
		1 * grailsAnnotationCacheOperationSource.getCacheOperations(_ as Method, _, true) >> [new CacheEvictOperation(name: 'CacheEvictOperation')]
		1 * expressionEvaluator.createEvaluationContext(_, _, _, _) >> evalContext
		1 * keyGenerator.generate(_) >> "key"
		1 * contentCacheParameters.getControllerName() >> "mock"
		1 * contentCacheParameters.getActionName() >> "cacheMethod"
		1 * contentCacheParameters.getControllerClass() >> MockController.class
		0 * _._
	}

	def "test normal flow through a cache filter lookup controller fails"() {
		given:
		GrailsFilterNullController filter = new GrailsFilterNullController()
		filter.cacheOperationSource = grailsAnnotationCacheOperationSource
		filter.expressionEvaluator = expressionEvaluator
		filter.keyGenerator = keyGenerator
		RequestContextHolder.setRequestAttributes(requestAttributes)
		Holders.setGrailsApplication(grailsApplication)

		when:
		filter.doFilter(grailsMockHttpServletRequest, grailsMockHttpServletResponse, mockFilterChain)

		then:
		1 * requestAttributes.getControllerName() >> "test"
		1 * grailsApplication.getArtefactForFeature('Controller', '/test')
		1 * contentCacheParameters.getControllerClass() >> null
		0 * _._
	}

	def "test normal flow through a cache filter lookup method fails"() {
		given:
		GrailsFilter filter = new GrailsFilter()
		filter.cacheOperationSource = grailsAnnotationCacheOperationSource
		filter.expressionEvaluator = expressionEvaluator
		filter.keyGenerator = keyGenerator
		RequestContextHolder.setRequestAttributes(requestAttributes)
		Holders.setGrailsApplication(grailsApplication)

		when:
		filter.doFilter(grailsMockHttpServletRequest, grailsMockHttpServletResponse, mockFilterChain)

		then:
		1 * requestAttributes.getControllerName() >> "test"
		1 * grailsApplication.getArtefactForFeature('Controller', '/test')
		1 * contentCacheParameters.getControllerClass() >> MockController.class
		1 * contentCacheParameters.getMethod() >> null
		0 * _._
	}

	def "test no cache things found"() {
		given:
		GrailsFilter filter = new GrailsFilter()
		filter.cacheOperationSource = grailsAnnotationCacheOperationSource
		filter.expressionEvaluator = expressionEvaluator
		filter.keyGenerator = keyGenerator
		RequestContextHolder.setRequestAttributes(requestAttributes)
		Holders.setGrailsApplication(grailsApplication)

		when:
		filter.doFilter(grailsMockHttpServletRequest, grailsMockHttpServletResponse, mockFilterChain)

		then:
		1 * requestAttributes.getControllerName() >> "test"
		1 * grailsApplication.getArtefactForFeature('Controller', '/test')
		1 * contentCacheParameters.getMethod() >> MockController.getMethod('cacheMethod')
		1 * grailsAnnotationCacheOperationSource.getCacheOperations(_ as Method, _, true) >> []
//		1 * contentCacheParameters.toString()
		1 * contentCacheParameters.getControllerClass()
		0 * _._
	}

	def "throw exception in urlMappingsHandlerMapping"() {
		given:
		GrailsFilter filter = new GrailsFilter()
		filter.cacheOperationSource = grailsAnnotationCacheOperationSource
		filter.expressionEvaluator = expressionEvaluator
		filter.keyGenerator = keyGenerator
		filter.urlMappingsHandlerMapping = urlMappingsHandlerMapping
		RequestContextHolder.setRequestAttributes(requestAttributes)
		Holders.setGrailsApplication(grailsApplication)

		when:
		filter.doFilter(grailsMockHttpServletRequest, grailsMockHttpServletResponse, mockFilterChain)

		then:
		1 * urlMappingsHandlerMapping.getHandlerInternal(_) >> { throw new Exception("I hate doing work") }
		thrown(Exception)
		0 * _._
	}

	def "throw exception in initContext"() {
		given:
		GrailsFilter filter = new GrailsFilter()
		filter.cacheOperationSource = grailsAnnotationCacheOperationSource
		filter.expressionEvaluator = expressionEvaluator
		filter.keyGenerator = keyGenerator
		filter.urlMappingsHandlerMapping = urlMappingsHandlerMapping
		RequestContextHolder.setRequestAttributes(requestAttributes)
		Holders.setGrailsApplication(grailsApplication)

		when:
		filter.doFilter(grailsMockHttpServletRequest, grailsMockHttpServletResponse, mockFilterChain)

		then:
		1 * urlMappingsHandlerMapping.getHandlerInternal(_) >> { throw new Exception("I hate doing work") }
		thrown(Exception)
		0 * _._
	}

	class GrailsFilter extends PageFragmentCachingFilter {
		@Override
		protected int getTimeToLive(Cache.ValueWrapper wrapper) {
			// not applicable
			return Integer.MAX_VALUE;
		}

		@Override
		protected ConcurrentMapCacheManager getNativeCacheManager() {
			return (ConcurrentMapCacheManager) super.getNativeCacheManager();
		}

		@Override
		protected void put(Cache cache, String key, PageInfo pageInfo, Integer timeToLiveSeconds) {
			cache.put(key, pageInfo);
		}

		protected ContentCacheParameters getContext() {
			synchronized (this.getClass()) {
				return contentCacheParameters
			}
		}

		protected Object lookupController(Class<?> controllerClass) {
			return new MockController()
		}
	}

	class GrailsFilterNullController extends GrailsFilter {
		protected Object lookupController(Class<?> controllerClass) {
			return null
		}
	}

	class MockController {
		@Cacheable('cacheMethod')
		def cacheMethod() {
		}
	}
}