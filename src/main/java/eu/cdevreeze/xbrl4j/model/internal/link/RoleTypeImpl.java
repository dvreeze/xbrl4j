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

package eu.cdevreeze.xbrl4j.model.internal.link;

import com.google.common.collect.ImmutableList;
import eu.cdevreeze.xbrl4j.common.dom.AncestryAwareElement;
import eu.cdevreeze.xbrl4j.model.XmlElement;
import eu.cdevreeze.xbrl4j.model.internal.XmlElementImpl;
import eu.cdevreeze.xbrl4j.model.link.Definition;
import eu.cdevreeze.xbrl4j.model.link.RoleType;
import eu.cdevreeze.xbrl4j.model.link.UsedOn;

import java.util.Optional;
import java.util.function.Function;

import static eu.cdevreeze.xbrl4j.model.Names.ROLE_URI_QNAME;

/**
 * Implementation of ArcroleType.
 *
 * @author Chris de Vreeze
 */
public class RoleTypeImpl extends XmlElementImpl implements RoleType {

    public RoleTypeImpl(
            AncestryAwareElement<?> underlyingElement,
            Function<AncestryAwareElement<?>, XmlElement> xmlElementCreator
    ) {
        super(underlyingElement, xmlElementCreator);
    }

    @Override
    public Optional<Definition> definitionOption() {
        return childElementStream(Definition.class).findAny();
    }

    @Override
    public ImmutableList<? extends UsedOn> usedOn() {
        return childElementStream(UsedOn.class).collect(ImmutableList.toImmutableList());
    }

    @Override
    public String roleUri() {
        return attribute(ROLE_URI_QNAME);
    }
}
