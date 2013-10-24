package info.archinnov.achilles.entity.metadata;

import java.lang.reflect.Method;
import java.util.List;
import info.archinnov.achilles.validation.Validator;

public class PartitionComponents extends AbstractComponentProperties {

	public PartitionComponents(List<Class<?>> componentClasses, List<String> componentNames,
                               List<Method> componentGetters,
                               List<Method> componentSetters) {
		super(componentClasses, componentNames, componentGetters, componentSetters);
	}

    void validatePartitionComponents(String className, List<Object> partitionComponents) {
        Validator.validateNotNull(partitionComponents,
                                  "There should be at least one partition key component provided for querying on " +
                                          "entity '%s'", className);
        Validator.validateTrue(partitionComponents.size() > 0,
                               "There should be at least one partition key component provided for querying on entity '%s'", className);

        Validator.validateTrue(partitionComponents.size() == componentClasses.size(),
                               "There should be exactly '%s' partition components for for querying on entity '%s'",
                               componentClasses.size(), className);

        for (int i = 0; i < partitionComponents.size(); i++) {
            Object partitionKeyComponent = partitionComponents.get(i);
            Class<?> currentPartitionComponentType = partitionKeyComponent.getClass();
            Class<?> expectedPartitionComponentType = componentClasses.get(i);

            Validator.validateNotNull(partitionKeyComponent, "The '%s' partition component should not be null", i);

            Validator
                    .validateTrue(
                            currentPartitionComponentType.equals(expectedPartitionComponentType),
                            "The type '%s' of partition key component '%s' for querying on entity '%s' is not valid. It should be '%s'",
                            currentPartitionComponentType.getCanonicalName(), partitionKeyComponent, className,
                            expectedPartitionComponentType.getCanonicalName());
        }
    }
	boolean isComposite() {
		return this.componentClasses.size() > 1;
	}

	Class<?> getPartitionKeyClass() {
		return componentClasses.get(0);
	}

	String getPartitionKeyName() {
		return componentNames.get(0);
	}

	Method getPartitionKeyGetter() {
		return componentGetters.get(0);
	}

	Method getPartitionKeySetter() {
		return componentSetters.get(0);
	}
}
