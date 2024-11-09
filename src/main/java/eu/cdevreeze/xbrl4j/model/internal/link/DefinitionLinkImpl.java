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
import eu.cdevreeze.xbrl4j.model.XmlElement;
import eu.cdevreeze.xbrl4j.model.internal.XmlElementImpl;
import eu.cdevreeze.xbrl4j.model.internal.xl.XLinkSupport;
import eu.cdevreeze.xbrl4j.model.link.DefinitionArc;
import eu.cdevreeze.xbrl4j.model.link.DefinitionLink;
import eu.cdevreeze.xbrl4j.model.link.Loc;
import eu.cdevreeze.xbrl4j.model.xl.XlResource;
import eu.cdevreeze.xbrl4j.model.xl.XlTitle;
import eu.cdevreeze.yaidom4j.queryapi.AncestryAwareElementApi;

import java.util.Optional;
import java.util.function.Function;

import static eu.cdevreeze.xbrl4j.model.Names.ID_QNAME;

/**
 * Implementation of DefinitionLink.
 *
 * @author Chris de Vreeze
 */
public class DefinitionLinkImpl extends XmlElementImpl implements DefinitionLink {

    public DefinitionLinkImpl(
            AncestryAwareElementApi<?> underlyingElement,
            Function<AncestryAwareElementApi<?>, XmlElement> xmlElementCreator
    ) {
        super(underlyingElement, xmlElementCreator);
    }

    @Override
    public XLinkType xlinkType() {
        return XLinkType.EXTENDED;
    }

    @Override
    public String role() {
        return XLinkSupport.roleOption(underlyingElement()).orElseThrow();
    }

    @Override
    public Optional<String> titleOption() {
        return XLinkSupport.titleOption(underlyingElement());
    }

    @Override
    public Optional<String> idOption() {
        return attributeOption(ID_QNAME);
    }

    @Override
    public ImmutableList<? extends XlTitle> titleElements() {
        return childElementStream(XlTitle.class).collect(ImmutableList.toImmutableList());
    }

    @Override
    public ImmutableList<? extends Loc> locators() {
        return childElementStream(Loc.class).collect(ImmutableList.toImmutableList());
    }

    @Override
    public ImmutableList<? extends DefinitionArc> arcs() {
        return childElementStream(DefinitionArc.class).collect(ImmutableList.toImmutableList());
    }

    // Should be empty
    @Override
    public ImmutableList<? extends XlResource> resources() {
        return childElementStream(XlResource.class).collect(ImmutableList.toImmutableList());
    }

    @Override
    public Optional<XLinkType> xlinkTypeOption() {
        return Optional.of(xlinkType());
    }
}
