package alien4cloud.tosca.parser.impl.advanced;

import java.util.Map;

import org.elasticsearch.common.collect.Maps;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

import alien4cloud.paas.plan.PlanGeneratorConstants;
import alien4cloud.tosca.model.Interface;
import alien4cloud.tosca.parser.INodeParser;
import alien4cloud.tosca.parser.ParserUtils;
import alien4cloud.tosca.parser.ParsingContextExecution;
import alien4cloud.tosca.parser.impl.base.MapParser;

public class InterfacesParser extends MapParser<Interface> {
    public InterfacesParser(INodeParser<Interface> valueParser, String toscaType) {
        super(valueParser, toscaType);
    }

    @Override
    public Map<String, Interface> parse(Node node, ParsingContextExecution context) {
        if (node instanceof MappingNode) {
            Map<String, Interface> interfaces = super.parse(node, context);
            Map<String, Interface> cleanedInterfaces = Maps.newHashMap();
            for (Map.Entry<String, Interface> entry : interfaces.entrySet()) {
                String interfaceType = getInterfaceType(entry.getKey());
                cleanedInterfaces.put(interfaceType, entry.getValue());
            }
            return cleanedInterfaces;
        }
        // Specific for interfaces node can define or only reference interfaces
        Map<String, Interface> interfaces = Maps.newHashMap();
        if (node instanceof SequenceNode) {
            for (Node interfaceTypeNode : ((SequenceNode) node).getValue()) {
                if (interfaceTypeNode instanceof ScalarNode) {
                    addInterfaceFromType((ScalarNode) interfaceTypeNode, interfaces, context);
                } else {
                    ParserUtils.addTypeError(interfaceTypeNode, context.getParsingErrors(), "interface");
                }
            }
        } else if (node instanceof ScalarNode) {
            addInterfaceFromType((ScalarNode) node, interfaces, context);
        } else {
            // add an error
            ParserUtils.addTypeError(node, context.getParsingErrors(), "Interfaces");
        }
        return interfaces;
    }

    private void addInterfaceFromType(ScalarNode node, Map<String, Interface> interfaces, ParsingContextExecution context) {
        String interfaceType = getInterfaceType(((ScalarNode) node).getValue());
        interfaces.put(interfaceType, new Interface());
    }

    public String getInterfaceType(String interfaceType) {
        if (PlanGeneratorConstants.NODE_LIFECYCLE_INTERFACE_NAME_SHORT.equalsIgnoreCase(interfaceType)) {
            return PlanGeneratorConstants.NODE_LIFECYCLE_INTERFACE_NAME;
        } else if (PlanGeneratorConstants.RELATIONSHIP_LIFECYCLE_INTERFACE_NAME_SHORT.equalsIgnoreCase(interfaceType)) {
            return PlanGeneratorConstants.RELATIONSHIP_LIFECYCLE_INTERFACE_NAME;
        }
        return interfaceType;
    }
}