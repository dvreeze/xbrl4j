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

import eu.cdevreeze.xbrl4j.model.XmlElement;
import eu.cdevreeze.xbrl4j.model.internal.XmlElementImpl;
import eu.cdevreeze.xbrl4j.model.internal.xl.XLinkSupport;
import eu.cdevreeze.xbrl4j.model.link.ArcroleRef;
import eu.cdevreeze.yaidom4j.queryapi.AncestryAwareElementApi;

import java.net.URI;
import java.util.Optional;
import java.util.function.Function;

import static eu.cdevreeze.xbrl4j.model.Names.ARCROLE_URI_QNAME;

/**
 * Implementation of ArcroleRef.
 *
 * @author Chris de Vreeze
 */
public class ArcroleRefImpl extends XmlElementImpl implements ArcroleRef {

    public ArcroleRefImpl(
            AncestryAwareElementApi<?> underlyingElement,
            Function<AncestryAwareElementApi<?>, XmlElement> xmlElementCreator
    ) {
        super(underlyingElement, xmlElementCreator);
    }

    @Override
    public XLinkType xlinkType() {
        return XLinkType.SIMPLE;
    }

    @Override
    public String arcroleUri() {
        return attribute(ARCROLE_URI_QNAME);
    }

    @Override
    public URI href() {
        return XLinkSupport.href(underlyingElement());
    }

    @Override
    public Optional<String> arcroleOption() {
        return XLinkSupport.arcroleOption(underlyingElement());
    }

    @Override
    public Optional<String> roleOption() {
        return XLinkSupport.roleOption(underlyingElement());
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
    public Optional<XLinkType> xlinkTypeOption() {
        return XLinkSupport.xlinkTypeOption(underlyingElement());
    }
}
