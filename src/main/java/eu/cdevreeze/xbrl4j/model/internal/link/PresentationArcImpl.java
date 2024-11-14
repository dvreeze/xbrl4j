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
import eu.cdevreeze.xbrl4j.model.internal.xl.XLinkSupport;
import eu.cdevreeze.xbrl4j.model.link.PresentationArc;
import eu.cdevreeze.xbrl4j.model.xl.XlTitle;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

import static eu.cdevreeze.xbrl4j.model.Names.PREFERRED_LABEL_QNAME;

/**
 * Implementation of PresentationArc.
 *
 * @author Chris de Vreeze
 */
public class PresentationArcImpl extends XmlElementImpl implements PresentationArc {

    public PresentationArcImpl(
            AncestryAwareElement<?> underlyingElement,
            Function<AncestryAwareElement<?>, XmlElement> xmlElementCreator
    ) {
        super(underlyingElement, xmlElementCreator);
    }

    @Override
    public XLinkType xlinkType() {
        return XLinkType.ARC;
    }

    @Override
    public Optional<String> preferredLabelOption() {
        return attributeOption(PREFERRED_LABEL_QNAME);
    }

    @Override
    public String from() {
        return XLinkSupport.from(underlyingElement());
    }

    @Override
    public String to() {
        return XLinkSupport.to(underlyingElement());
    }

    @Override
    public String arcrole() {
        return XLinkSupport.arcroleOption(underlyingElement()).orElseThrow();
    }

    @Override
    public Optional<String> titleOption() {
        return XLinkSupport.titleOption(underlyingElement());
    }

    @Override
    public Optional<Show> showOption() {
        return XLinkSupport.showOption(underlyingElement());
    }

    @Override
    public Optional<Actuate> actuateOption() {
        return XLinkSupport.actuateOption(underlyingElement());
    }

    @Override
    public Optional<BigDecimal> orderOption() {
        return XLinkSupport.orderOption(underlyingElement());
    }

    @Override
    public Optional<Use> useOption() {
        return XLinkSupport.useOption(underlyingElement());
    }

    @Override
    public OptionalInt priorityOption() {
        return XLinkSupport.priorityOption(underlyingElement());
    }

    @Override
    public ImmutableList<? extends XlTitle> titleElements() {
        return childElementStream(XlTitle.class).collect(ImmutableList.toImmutableList());
    }

    @Override
    public Optional<XLinkType> xlinkTypeOption() {
        return XLinkSupport.xlinkTypeOption(underlyingElement());
    }
}
