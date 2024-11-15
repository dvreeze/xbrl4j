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
import eu.cdevreeze.xbrl4j.model.link.ArcroleRef;
import eu.cdevreeze.xbrl4j.model.link.Linkbase;
import eu.cdevreeze.xbrl4j.model.link.RoleRef;
import eu.cdevreeze.xbrl4j.model.xl.XlExtendedLink;

import java.util.function.Function;

/**
 * Implementation of Linkbase.
 *
 * @author Chris de Vreeze
 */
public class LinkbaseImpl extends XmlElementImpl implements Linkbase {

    public LinkbaseImpl(
            AncestryAwareElement<?> underlyingElement,
            Function<AncestryAwareElement<?>, XmlElement> xmlElementCreator
    ) {
        super(underlyingElement, xmlElementCreator);
    }

    @Override
    public ImmutableList<? extends RoleRef> roleRefs() {
        return childElementStream(RoleRef.class).collect(ImmutableList.toImmutableList());
    }

    @Override
    public ImmutableList<? extends ArcroleRef> arcroleRefs() {
        return childElementStream(ArcroleRef.class).collect(ImmutableList.toImmutableList());
    }

    @Override
    public ImmutableList<? extends XlExtendedLink> extendedLinks() {
        return childElementStream(XlExtendedLink.class).collect(ImmutableList.toImmutableList());
    }
}
