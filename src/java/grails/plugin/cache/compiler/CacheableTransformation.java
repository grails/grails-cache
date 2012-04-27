package grails.plugin.cache.compiler;

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
public class CacheableTransformation implements ASTTransformation{

    @Override
    public void visit(final ASTNode[] astNodes, final SourceUnit sourceUnit) {
        
        final ASTNode firstNode = astNodes[0];
        final ASTNode secondNode = astNodes[1];
        if (!(firstNode instanceof AnnotationNode) || !(secondNode instanceof AnnotatedNode)) {
            throw new RuntimeException("Internal error: wrong types: " + firstNode.getClass().getName() + " / " + secondNode.getClass().getName());
        }

        final AnnotationNode grailsCachableAnnotationNode = (AnnotationNode) firstNode;
        final AnnotatedNode parent = (AnnotatedNode) secondNode;
        final Map<String, Expression> grailsAnnotationMembers = grailsCachableAnnotationNode.getMembers();
        
        final AnnotationNode springCacheableAnnotationNode = new AnnotationNode(new ClassNode(org.springframework.cache.annotation.Cacheable.class));
        for(Map.Entry<String, Expression> entry : grailsAnnotationMembers.entrySet()) {
            springCacheableAnnotationNode.addMember(entry.getKey(), entry.getValue());
        }
        parent.addAnnotation(springCacheableAnnotationNode);
    }

}
