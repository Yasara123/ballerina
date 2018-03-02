package org.ballerinalang.net.grpc.proto;

import org.ballerinalang.compiler.plugins.AbstractCompilerPlugin;
import org.ballerinalang.compiler.plugins.SupportedAnnotationPackages;
import org.ballerinalang.model.tree.AnnotationAttachmentNode;
import org.ballerinalang.model.tree.ServiceNode;
import org.ballerinalang.net.grpc.exception.GrpcServerException;
import org.ballerinalang.net.grpc.proto.definition.File;
import org.ballerinalang.util.diagnostic.Diagnostic;
import org.ballerinalang.util.diagnostic.DiagnosticLog;

import java.io.PrintStream;
import java.util.List;

/**
 * This class validates annotations attached to Ballerina service and resource nodes.
 *
 * @since 1.0
 */
@SupportedAnnotationPackages(
        value = "ballerina.net.grpc"
)
public class ServiceProtoBuilder extends AbstractCompilerPlugin {

    private DiagnosticLog dlog;
    private PrintStream out = System.out;

    @Override
    public void init(DiagnosticLog diagnosticLog) {
        this.dlog = diagnosticLog;
    }

    @Override
    public void process(ServiceNode serviceNode, List<AnnotationAttachmentNode> annotations) {
        out.println("service node: " + serviceNode.getName().getValue());
        try {
            File fileDefinition =  ServiceProtoUtils.generateProtoDefinition(serviceNode);
            ServiceProtoUtils.writeServiceFiles(fileDefinition, serviceNode.getName().getValue());
        } catch (GrpcServerException e) {
            dlog.logDiagnostic(Diagnostic.Kind.WARNING, serviceNode.getPosition(), e.getMessage());
        }
    }
}
