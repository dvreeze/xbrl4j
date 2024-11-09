/*
 * Copyright 2024-2024 Chris de Vreeze
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

package eu.cdevreeze.xbrl4j.model.factory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import javax.xml.namespace.QName;
import java.util.Objects;

/**
 * Needed context from the XML Schema in order to create XmlElement trees.
 *
 * @author Chris de Vreeze
 */
public record SchemaContext(ImmutableMap<QName, QName> substitutionGroups) {

    public ImmutableSet<QName> findSubstitutionGroups(QName elementName) {
        if (!substitutionGroups().containsKey(elementName)) {
            return ImmutableSet.of();
        } else {
            ImmutableSet.Builder<QName> setBuilder = ImmutableSet.builder();
            QName sg = Objects.requireNonNull(substitutionGroups().get(elementName));
            setBuilder.add(sg);
            // Recursion
            setBuilder.addAll(findSubstitutionGroups(sg));
            return setBuilder.build();
        }
    }

    public ImmutableSet<QName> findSubstitutionGroupsOrSelf(QName elementName) {
        ImmutableSet.Builder<QName> setBuilder = ImmutableSet.builder();
        setBuilder.add(elementName);
        setBuilder.addAll(findSubstitutionGroups(elementName));
        return setBuilder.build();
    }
}
