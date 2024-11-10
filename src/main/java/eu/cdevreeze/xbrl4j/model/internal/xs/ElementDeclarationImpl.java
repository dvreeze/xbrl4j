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

package eu.cdevreeze.xbrl4j.model.internal.xs;

import eu.cdevreeze.xbrl4j.model.XmlElement;
import eu.cdevreeze.xbrl4j.model.internal.XmlElementImpl;
import eu.cdevreeze.xbrl4j.model.xs.ElementDeclaration;
import eu.cdevreeze.yaidom4j.queryapi.AncestryAwareElementApi;

import javax.xml.namespace.QName;
import java.util.Optional;
import java.util.function.Function;

import static eu.cdevreeze.xbrl4j.model.Names.SUBSTITUTION_GROUP_QNAME;

/**
 * Implementation of ElementDeclaration.
 *
 * @author Chris de Vreeze
 */
public class ElementDeclarationImpl extends XmlElementImpl implements ElementDeclaration {

    public ElementDeclarationImpl(
            AncestryAwareElementApi<?> underlyingElement,
            Function<AncestryAwareElementApi<?>, XmlElement> xmlElementCreator
    ) {
        super(underlyingElement, xmlElementCreator);
    }

    @Override
    public Optional<QName> substitutionGroupOption() {
        Optional<String> syntacticQNameOption = attributeOption(SUBSTITUTION_GROUP_QNAME);
        return syntacticQNameOption.map(n -> namespaceScope().resolveSyntacticElementQName(n));
    }
}
