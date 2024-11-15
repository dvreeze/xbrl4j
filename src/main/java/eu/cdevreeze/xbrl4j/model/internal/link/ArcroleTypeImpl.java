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
import eu.cdevreeze.xbrl4j.model.link.ArcroleType;
import eu.cdevreeze.xbrl4j.model.link.Definition;
import eu.cdevreeze.xbrl4j.model.link.UsedOn;

import java.util.Optional;
import java.util.function.Function;

import static eu.cdevreeze.xbrl4j.model.Names.ARCROLE_URI_QNAME;
import static eu.cdevreeze.xbrl4j.model.Names.CYCLES_ALLOWED_QNAME;

/**
 * Implementation of ArcroleType.
 *
 * @author Chris de Vreeze
 */
public class ArcroleTypeImpl extends XmlElementImpl implements ArcroleType {

    public ArcroleTypeImpl(
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
    public String arcroleUri() {
        return attribute(ARCROLE_URI_QNAME);
    }

    @Override
    public CyclesAllowed cyclesAllowed() {
        return CyclesAllowed.parse(attribute(CYCLES_ALLOWED_QNAME));
    }
}
