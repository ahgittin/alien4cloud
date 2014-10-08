package alien4cloud.it.plugin;

import java.util.List;
import java.util.Map;

import alien4cloud.component.model.Tag;
import alien4cloud.tosca.container.model.type.PropertyDefinition;
import alien4cloud.tosca.container.model.type.ToscaType;
import alien4cloud.ui.form.annotation.FormProperties;
import alien4cloud.ui.form.annotation.FormPropertyConstraint;
import alien4cloud.ui.form.annotation.FormPropertyDefinition;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@FormProperties({ "firstArgument", "secondArgument", "thirdArgument", "tags", "properties", "javaVersion" })
@SuppressWarnings("PMD.UnusedPrivateField")
public class ProviderConfig {

    private String firstArgument;

    private String secondArgument;

    private String thirdArgument;

    private List<Tag> tags;

    private Map<String, PropertyDefinition> properties;

    @FormPropertyDefinition(
            type = ToscaType.VERSION,
            defaultValue = "1.6",
            constraints = @FormPropertyConstraint(
                    greaterOrEqual = "1.6"
            ))
    private String javaVersion;
}