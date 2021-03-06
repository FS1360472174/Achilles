/*
 * Copyright (C) 2012-2016 DuyHai DOAN
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.archinnov.achilles.internals.strategy.types_nesting;

import static info.archinnov.achilles.internals.apt.AptUtils.containsAnnotation;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.type.TypeMirror;

import com.datastax.driver.core.UDTValue;
import com.squareup.javapoet.TypeName;

import info.archinnov.achilles.annotations.Frozen;
import info.archinnov.achilles.annotations.JSON;
import info.archinnov.achilles.annotations.UDT;
import info.archinnov.achilles.internals.apt.AptUtils;
import info.archinnov.achilles.internals.parser.AnnotationTree;
import info.archinnov.achilles.type.tuples.*;

public class FrozenNestedTypeStrategy implements NestedTypesStrategy {

    @Override
    public void validate(AptUtils aptUtils, AnnotationTree annotationTree, String fieldName, TypeName rawClass) {
        validateIndexAnnotation(aptUtils, annotationTree, fieldName, rawClass);

        final TypeMirror currentType = aptUtils.erasure(annotationTree.getCurrentType());

        if (aptUtils.isAssignableFrom(Tuple.class, currentType) || containsAnnotation(annotationTree, JSON.class)) {
            // Do not validate nested types for Tuples because
            // they are @Frozen by default
            // Do not validate nested types for JSON transformation
            return;
        } else if (aptUtils.isAssignableFrom(List.class, currentType)
                || aptUtils.isAssignableFrom(Set.class, currentType)) {
            validateNestedType(aptUtils, annotationTree, fieldName, rawClass);
        } else if (aptUtils.isAssignableFrom(Map.class, currentType)) {
            validateMapKeys(aptUtils, annotationTree, fieldName, rawClass);
            AnnotationTree next = annotationTree;
            for (int i = 0; i < 2; i++) {
                next = validateNestedType(aptUtils, next, fieldName, rawClass);
            }
        } else if (aptUtils.isAssignableFrom(UDTValue.class, currentType)) {
            aptUtils.validateTrue(containsAnnotation(annotationTree, Frozen.class),
                    "UDTValue in field '%s' of class '%s' should be annotated with @Frozen",
                    fieldName, rawClass);
        } else if (aptUtils.getAnnotationOnClass(currentType, UDT.class).isPresent()) {
            aptUtils.validateTrue(containsAnnotation(annotationTree, Frozen.class),
                    "UDT class '%s' in field '%s' of class '%s' should be annotated with @Frozen",
                    currentType, fieldName, rawClass);
        }
    }

    private AnnotationTree validateNestedType(AptUtils aptUtils, AnnotationTree annotationTree, String fieldName, TypeName rawClass) {
        final AnnotationTree next = annotationTree.next();
        final TypeMirror nextType = next.getCurrentType();
        if (aptUtils.isCompositeTypeForCassandra(nextType) && !containsAnnotation(next, JSON.class)) {
            aptUtils.validateTrue(containsAnnotation(next, Frozen.class),
                    "Nested collections/array type/UDT '%s' in '%s' of class '%s' should be annotated with @Frozen",
                    nextType, fieldName, rawClass);
        }
        return next;
    }
}
