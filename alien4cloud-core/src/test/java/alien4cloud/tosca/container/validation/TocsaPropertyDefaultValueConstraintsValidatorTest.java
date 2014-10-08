package alien4cloud.tosca.container.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Assert;
import org.junit.Test;

import alien4cloud.tosca.container.model.Definitions;
import alien4cloud.tosca.container.model.type.NodeType;
import alien4cloud.tosca.container.model.type.PropertyConstraint;
import alien4cloud.tosca.container.model.type.PropertyDefinition;
import alien4cloud.tosca.container.model.type.ToscaType;
import alien4cloud.tosca.container.validation.ToscaSequence;
import alien4cloud.tosca.properties.constraints.EqualConstraint;
import alien4cloud.tosca.properties.constraints.GreaterOrEqualConstraint;
import alien4cloud.tosca.properties.constraints.GreaterThanConstraint;
import alien4cloud.tosca.properties.constraints.InRangeConstraint;
import alien4cloud.tosca.properties.constraints.LengthConstraint;
import alien4cloud.tosca.properties.constraints.LessOrEqualConstraint;
import alien4cloud.tosca.properties.constraints.LessThanConstraint;
import alien4cloud.tosca.properties.constraints.MaxLengthConstraint;
import alien4cloud.tosca.properties.constraints.MinLengthConstraint;
import alien4cloud.tosca.properties.constraints.PatternConstraint;
import alien4cloud.tosca.properties.constraints.ValidValuesConstraint;

import com.google.common.collect.Lists;

public class TocsaPropertyDefaultValueConstraintsValidatorTest {
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();;

    private Definitions createDefinitions(String propertyType, PropertyConstraint constraint, String defaultValue) {
        Definitions definitions = new Definitions();
        NodeType nodeType = new NodeType();
        PropertyDefinition propertyDefinition = new PropertyDefinition();
        propertyDefinition.setType(propertyType);
        propertyDefinition.setDefault(defaultValue);
        propertyDefinition.setConstraints(Lists.newArrayList(constraint));
        nodeType.setProperties(new HashMap<String, PropertyDefinition>());
        nodeType.getProperties().put("propertyKey", propertyDefinition);
        definitions.getNodeTypes().put("nodeType", nodeType);
        return definitions;
    }

    private Definitions createEqualDefinition(String propertyType, String constraintValue, String defaultValue) {
        EqualConstraint constraint = new EqualConstraint();
        constraint.setEqual(constraintValue);
        return createDefinitions(propertyType, constraint, defaultValue);
    }

    private Definitions createGreaterOrEqualDefinition(String propertyType, Comparable<?> constraintValue, String defaultValue) {
        GreaterOrEqualConstraint constraint = new GreaterOrEqualConstraint();
        constraint.setGreaterOrEqual(String.valueOf(constraintValue));
        return createDefinitions(propertyType, constraint, defaultValue);
    }

    private Definitions createGreaterThanDefinition(String propertyType, Comparable<?> constraintValue, String defaultValue) {
        GreaterThanConstraint constraint = new GreaterThanConstraint();
        constraint.setGreaterThan(String.valueOf(constraintValue));
        return createDefinitions(propertyType, constraint, defaultValue);
    }

    private Definitions createInRangeDefinition(String propertyType, Comparable minConstraintValue, Comparable maxConstraintValue, String defaultValue) {
        InRangeConstraint constraint = new InRangeConstraint();
        constraint.setInRange(Lists.newArrayList(String.valueOf(minConstraintValue), String.valueOf(maxConstraintValue)));
        return createDefinitions(propertyType, constraint, defaultValue);
    }

    private Definitions createLenghtDefinition(String propertyType, int constraintValue, String defaultValue) {
        LengthConstraint constraint = new LengthConstraint();
        constraint.setLength(constraintValue);
        return createDefinitions(propertyType, constraint, defaultValue);
    }

    private Definitions createLessOrEqualDefinition(String propertyType, Comparable<?> constraintValue, String defaultValue) {
        LessOrEqualConstraint constraint = new LessOrEqualConstraint();
        constraint.setLessOrEqual(String.valueOf(constraintValue));
        return createDefinitions(propertyType, constraint, defaultValue);
    }

    private Definitions createLessDefinition(String propertyType, Comparable<?> constraintValue, String defaultValue) {
        LessThanConstraint constraint = new LessThanConstraint();
        constraint.setLessThan(String.valueOf(constraintValue));
        return createDefinitions(propertyType, constraint, defaultValue);
    }

    private Definitions createMaxLenghtDefinition(String propertyType, int constraintValue, String defaultValue) {
        MaxLengthConstraint constraint = new MaxLengthConstraint();
        constraint.setMaxLength(constraintValue);
        return createDefinitions(propertyType, constraint, defaultValue);
    }

    private Definitions createMinLenghtDefinition(String propertyType, int constraintValue, String defaultValue) {
        MinLengthConstraint constraint = new MinLengthConstraint();
        constraint.setMinLength(constraintValue);
        return createDefinitions(propertyType, constraint, defaultValue);
    }

    private Definitions createPatternDefinition(String propertyType, String constraintValue, String defaultValue) {
        PatternConstraint constraint = new PatternConstraint();
        constraint.setPattern(constraintValue);
        return createDefinitions(propertyType, constraint, defaultValue);
    }

    private Definitions createValidValuesDefinition(String propertyType, List<String> constraintValue, String defaultValue) {
        ValidValuesConstraint constraint = new ValidValuesConstraint();
        constraint.setValidValues(constraintValue);
        return createDefinitions(propertyType, constraint, defaultValue);
    }

    // Equals tests

    @Test
    public void validStringEqualsShouldNotCreateViolations() {
        Set<ConstraintViolation<Definitions>> violations = validator.validate(createEqualDefinition(ToscaType.STRING.toString(), "constrainted value",
                "constrainted value"), ToscaSequence.class);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void invalidStringEqualsShouldCreateViolations() {
        Set<ConstraintViolation<Definitions>> violations = validator.validate(createEqualDefinition(ToscaType.STRING.toString(), "constrainted value",
                "not matching value"), ToscaSequence.class);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void validIntegerEqualsShouldNotCreateViolations() {
        Set<ConstraintViolation<Definitions>> violations = validator.validate(createEqualDefinition(ToscaType.INTEGER.toString(), "10",
                "10"), ToscaSequence.class);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void invalidIntegerEqualsShouldCreateViolations() {
        Set<ConstraintViolation<Definitions>> violations = validator.validate(createEqualDefinition(ToscaType.INTEGER.toString(), "10",
                "40"), ToscaSequence.class);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void validFloatEqualsShouldNotCreateViolations() {
        Set<ConstraintViolation<Definitions>> violations = validator.validate(createEqualDefinition(ToscaType.FLOAT.toString(), "10.56",
                "10.56"), ToscaSequence.class);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void invalidFloatEqualsShouldCreateViolations() {
        Set<ConstraintViolation<Definitions>> violations = validator.validate(createEqualDefinition(ToscaType.FLOAT.toString(), "10.56",
                "40.56"), ToscaSequence.class);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void validBooleanEqualsShouldNotCreateViolations() {
        Set<ConstraintViolation<Definitions>> violations = validator.validate(createEqualDefinition(ToscaType.BOOLEAN.toString(), "true",
                "true"), ToscaSequence.class);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void invalidBooleanEqualsShouldCreateViolations() {
        Set<ConstraintViolation<Definitions>> violations = validator.validate(createEqualDefinition(ToscaType.BOOLEAN.toString(), "true",
                "false"), ToscaSequence.class);
        Assert.assertEquals(1, violations.size());
    }

    // Greater Or Equal

    @Test
    public void validIntegerGreaterOrEqualShouldNotCreateViolations() {
        Set<ConstraintViolation<Definitions>> violations = validator.validate(createGreaterOrEqualDefinition(ToscaType.INTEGER.toString(), 10l,
                "10"), ToscaSequence.class);
        Assert.assertEquals(0, violations.size());

        violations = validator.validate(createGreaterOrEqualDefinition(ToscaType.INTEGER.toString(), 10l,
                "20"), ToscaSequence.class);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void invalidIntegerGreaterOrEqualShouldCreateViolations() {
        Set<ConstraintViolation<Definitions>> violations = validator.validate(createGreaterOrEqualDefinition(ToscaType.INTEGER.toString(), 10l,
                "5"), ToscaSequence.class);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void validFloatGreaterOrEqualShouldNotCreateViolations() {
        Set<ConstraintViolation<Definitions>> violations = validator.validate(createGreaterOrEqualDefinition(ToscaType.FLOAT.toString(), 10.56d,
                "10.56"), ToscaSequence.class);
        Assert.assertEquals(0, violations.size());

        violations = validator.validate(createGreaterOrEqualDefinition(ToscaType.FLOAT.toString(), 10.56d,
                "20.56"), ToscaSequence.class);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void invalidFloatGreaterOrEqualShouldCreateViolations() {
        Set<ConstraintViolation<Definitions>> violations = validator.validate(createGreaterOrEqualDefinition(ToscaType.FLOAT.toString(), 10.56d,
                "5.56"), ToscaSequence.class);
        Assert.assertEquals(1, violations.size());
    }

    // Greater Than

    @Test
    public void validIntegerGreaterThanShouldNotCreateViolations() {
        Set<ConstraintViolation<Definitions>> violations = validator.validate(createGreaterThanDefinition(ToscaType.INTEGER.toString(), 10l,
                "20"), ToscaSequence.class);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void invalidIntegerGreaterThanShouldCreateViolations() {
        Set<ConstraintViolation<Definitions>> violations = validator.validate(createGreaterThanDefinition(ToscaType.INTEGER.toString(), 10l,
                "5"), ToscaSequence.class);
        Assert.assertEquals(1, violations.size());

        violations = validator.validate(createGreaterThanDefinition(ToscaType.INTEGER.toString(), 10l,
                "10"), ToscaSequence.class);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void validFloatGreaterThanShouldNotCreateViolations() {
        Set<ConstraintViolation<Definitions>> violations = validator.validate(createGreaterThanDefinition(ToscaType.FLOAT.toString(), 10.56d,
                "20.56"), ToscaSequence.class);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void invalidFloatGreaterThanShouldCreateViolations() {
        Set<ConstraintViolation<Definitions>> violations = validator.validate(createGreaterThanDefinition(ToscaType.FLOAT.toString(), 10.56d,
                "5.56"), ToscaSequence.class);
        Assert.assertEquals(1, violations.size());

        violations = validator.validate(createGreaterThanDefinition(ToscaType.FLOAT.toString(), 10.56d,
                "10.56"), ToscaSequence.class);
        Assert.assertEquals(1, violations.size());
    }
}