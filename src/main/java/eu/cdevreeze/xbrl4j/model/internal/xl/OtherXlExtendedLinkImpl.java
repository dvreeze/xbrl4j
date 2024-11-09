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

package eu.cdevreeze.xbrl4j.model.internal.xl;

import com.google.common.collect.ImmutableList;
import eu.cdevreeze.xbrl4j.model.XmlElement;
import eu.cdevreeze.xbrl4j.model.factory.SchemaContext;
import eu.cdevreeze.xbrl4j.model.internal.XmlElementImpl;
import eu.cdevreeze.xbrl4j.model.link.Loc;
import eu.cdevreeze.xbrl4j.model.xl.XlArc;
import eu.cdevreeze.xbrl4j.model.xl.XlExtendedLink;
import eu.cdevreeze.xbrl4j.model.xl.XlResource;
import eu.cdevreeze.xbrl4j.model.xl.XlTitle;
import eu.cdevreeze.yaidom4j.queryapi.AncestryAwareElementApi;

import java.util.Optional;
import java.util.function.BiFunction;

import static eu.cdevreeze.xbrl4j.model.Names.ID_QNAME;

/**
 * Implementation of "non-standard" XlExtendedLink.
 *
 * @author Chris de Vreeze
 */
public class OtherXlExtendedLinkImpl extends XmlElementImpl implements XlExtendedLink {

    public OtherXlExtendedLinkImpl(
            AncestryAwareElementApi<?> underlyingElement,
            SchemaContext schemaContext,
            BiFunction<AncestryAwareElementApi<?>, SchemaContext, XmlElement> xmlElementCreator
    ) {
        super(underlyingElement, schemaContext, xmlElementCreator);
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
    public ImmutableList<? extends XlArc> arcs() {
        return childElementStream(XlArc.class).collect(ImmutableList.toImmutableList());
    }

    @Override
    public ImmutableList<? extends XlResource> resources() {
        return childElementStream(XlResource.class).collect(ImmutableList.toImmutableList());
    }

    @Override
    public Optional<XLinkType> xlinkTypeOption() {
        return Optional.of(xlinkType());
    }
}
