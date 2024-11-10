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

package eu.cdevreeze.xbrl4j.model;

import eu.cdevreeze.yaidom4j.core.NamespaceScope;
import eu.cdevreeze.yaidom4j.queryapi.AncestryAwareElementApi;
import eu.cdevreeze.yaidom4j.queryapi.ElementApi;

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Any XML element in the context of XBRL. The idea is that construction of an XmlELement
 * (or typically creating an instance of a subtype) should never fail, and that the
 * ElementApi methods of XmlElement should never fail either. As a consequence, the XmlElement
 * API is always useful while validating a potentially incorrect instance or taxonomy document, as long
 * as we stay away from some methods outside the ElementApi part.
 * <p>
 * Implementations should be immutable and thread-safe.
 *
 * @author Chris de Vreeze
 */
public interface XmlElement extends ElementApi<XmlElement> {

    AncestryAwareElementApi<?> underlyingElement();

    NamespaceScope namespaceScope();

    /**
     * Alias of descendantElementOrSelfStream
     */
    <T extends XmlElement> Stream<T> elementStream(Class<T> cls);

    /**
     * Alias of descendantElementOrSelfStream
     */
    <T extends XmlElement> Stream<T> elementStream(Class<T> cls, Predicate<T> predicate);

    /**
     * Alias of topmostDescendantElementOrSelfStream
     */
    <T extends XmlElement> Stream<T> topmostElementStream(Class<T> cls, Predicate<T> predicate);

    <T extends XmlElement> Stream<T> childElementStream(Class<T> cls);

    <T extends XmlElement> Stream<T> childElementStream(Class<T> cls, Predicate<T> predicate);

    <T extends XmlElement> Stream<T> descendantElementOrSelfStream(Class<T> cls);

    <T extends XmlElement> Stream<T> descendantElementOrSelfStream(Class<T> cls, Predicate<T> predicate);

    <T extends XmlElement> Stream<T> descendantElementStream(Class<T> cls);

    <T extends XmlElement> Stream<T> descendantElementStream(Class<T> cls, Predicate<T> predicate);

    <T extends XmlElement> Stream<T> topmostDescendantElementOrSelfStream(Class<T> cls, Predicate<T> predicate);

    <T extends XmlElement> Stream<T> topmostDescendantElementStream(Class<T> cls, Predicate<T> predicate);
}
