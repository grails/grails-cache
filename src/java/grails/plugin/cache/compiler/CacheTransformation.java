package grails.plugin.cache.compiler;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class CacheTransformation implements ASTTransformation {

	@SuppressWarnings("serial")
	private static final Map<ClassNode, Class<? extends Annotation>> GRAILS_ANNOTATION_CLASS_NODE_TO_SPRING_ANNOTATION = new HashMap<ClassNode, Class<? extends Annotation>>(){{
		put(new ClassNode(grails.plugin.cache.Cacheable.class), org.springframework.cache.annotation.Cacheable.class);
		put(new ClassNode(grails.plugin.cache.CachePut.class), org.springframework.cache.annotation.CachePut.class);
		put(new ClassNode(grails.plugin.cache.CacheEvict.class), org.springframework.cache.annotation.CacheEvict.class);
	}};

    public void visit(final ASTNode[] astNodes, final SourceUnit sourceUnit) {
        final ASTNode firstNode = astNodes[0];
        final ASTNode secondNode = astNodes[1];
        if (!(firstNode instanceof AnnotationNode) || !(secondNode instanceof AnnotatedNode)) {
            throw new RuntimeException("Internal error: wrong types: " + firstNode.getClass().getName() + " / " + secondNode.getClass().getName());
        }

        final AnnotationNode grailsCacheAnnotationNode = (AnnotationNode) firstNode;
        final AnnotatedNode parent = (AnnotatedNode) secondNode;
        final AnnotationNode springCacheableAnnotationNode = getCorrespondingSpringAnnotation(grailsCacheAnnotationNode);
        parent.addAnnotation(springCacheableAnnotationNode);
    }

	protected AnnotationNode getCorrespondingSpringAnnotation(final AnnotationNode grailsCacheAnnotationNode) {
		final Map<String, Expression> grailsAnnotationMembers = grailsCacheAnnotationNode.getMembers();
        
		final Class<? extends Annotation> springAnnotationClass = GRAILS_ANNOTATION_CLASS_NODE_TO_SPRING_ANNOTATION.get(grailsCacheAnnotationNode.getClassNode());
		final AnnotationNode springCacheableAnnotationNode = new AnnotationNode(new ClassNode(springAnnotationClass));
        for(Map.Entry<String, Expression> entry : grailsAnnotationMembers.entrySet()) {
            springCacheableAnnotationNode.addMember(entry.getKey(), entry.getValue());
        }
		return springCacheableAnnotationNode;
	}
}
