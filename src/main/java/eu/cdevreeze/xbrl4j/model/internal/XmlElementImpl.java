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

package eu.cdevreeze.xbrl4j.model.internal;

import com.google.common.collect.ImmutableMap;
import eu.cdevreeze.xbrl4j.model.XmlElement;
import eu.cdevreeze.xbrl4j.model.factory.SchemaContext;
import eu.cdevreeze.yaidom4j.queryapi.AncestryAwareElementApi;

import javax.xml.namespace.QName;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Implementation of XmlElement.
 *
 * @author Chris de Vreeze
 */
public abstract class XmlElementImpl implements XmlElement {

    private final AncestryAwareElementApi<?> underlyingElement;

    private final SchemaContext schemaContext;

    // Must be very fast!
    private final BiFunction<AncestryAwareElementApi<?>, SchemaContext, XmlElement> xmlElementCreator;

    public XmlElementImpl(
            AncestryAwareElementApi<?> underlyingElement,
            SchemaContext schemaContext,
            BiFunction<AncestryAwareElementApi<?>, SchemaContext, XmlElement> xmlElementCreator
    ) {
        this.underlyingElement = underlyingElement;
        this.schemaContext = schemaContext;
        this.xmlElementCreator = xmlElementCreator;
    }

    @Override
    public AncestryAwareElementApi<?> underlyingElement() {
        return underlyingElement;
    }

    @Override
    public <T extends XmlElement> Stream<T> elementStream(Class<T> cls) {
        @SuppressWarnings("unchecked")
        var resultStream = elementStream(cls::isInstance).map(e -> (T) e);
        return resultStream;
    }

    @Override
    public <T extends XmlElement> Stream<T> elementStream(Class<T> cls, Predicate<T> predicate) {
        return elementStream(cls).filter(predicate);
    }

    @Override
    public <T extends XmlElement> Stream<T> topmostElementStream(Class<T> cls, Predicate<T> predicate) {
        @SuppressWarnings("unchecked")
        var resultStream = topmostElementStream(e -> cls.isInstance(e) && predicate.test((T) e))
                .map(e -> (T) e);
        return resultStream;
    }

    @Override
    public <T extends XmlElement> Stream<T> childElementStream(Class<T> cls) {
        @SuppressWarnings("unchecked")
        var resultStream = childElementStream(cls::isInstance).map(e -> (T) e);
        return resultStream;
    }

    @Override
    public <T extends XmlElement> Stream<T> childElementStream(Class<T> cls, Predicate<T> predicate) {
        return childElementStream(cls).filter(predicate);
    }

    @Override
    public <T extends XmlElement> Stream<T> descendantElementOrSelfStream(Class<T> cls) {
        @SuppressWarnings("unchecked")
        var resultStream = descendantElementOrSelfStream(cls::isInstance).map(e -> (T) e);
        return resultStream;
    }

    @Override
    public <T extends XmlElement> Stream<T> descendantElementOrSelfStream(Class<T> cls, Predicate<T> predicate) {
        return descendantElementOrSelfStream(cls).filter(predicate);
    }

    @Override
    public <T extends XmlElement> Stream<T> descendantElementStream(Class<T> cls) {
        @SuppressWarnings("unchecked")
        var resultStream = descendantElementStream(cls::isInstance).map(e -> (T) e);
        return resultStream;
    }

    @Override
    public <T extends XmlElement> Stream<T> descendantElementStream(Class<T> cls, Predicate<T> predicate) {
        return descendantElementStream(cls).filter(predicate);
    }

    @Override
    public <T extends XmlElement> Stream<T> topmostDescendantElementOrSelfStream(Class<T> cls, Predicate<T> predicate) {
        @SuppressWarnings("unchecked")
        var resultStream = topmostDescendantElementOrSelfStream(e -> cls.isInstance(e) && predicate.test((T) e))
                .map(e -> (T) e);
        return resultStream;
    }

    @Override
    public <T extends XmlElement> Stream<T> topmostDescendantElementStream(Class<T> cls, Predicate<T> predicate) {
        @SuppressWarnings("unchecked")
        var resultStream = topmostDescendantElementStream(e -> cls.isInstance(e) && predicate.test((T) e))
                .map(e -> (T) e);
        return resultStream;
    }

    @Override
    public QName elementName() {
        return underlyingElement.elementName();
    }

    @Override
    public ImmutableMap<QName, String> attributes() {
        return underlyingElement.attributes();
    }

    @Override
    public Optional<String> attributeOption(QName attrName) {
        return underlyingElement.attributeOption(attrName);
    }

    @Override
    public String attribute(QName attrName) {
        return underlyingElement.attribute(attrName);
    }

    @Override
    public String text() {
        return underlyingElement.text();
    }

    @Override
    public Stream<XmlElement> elementStream() {
        return underlyingElement.elementStream()
                .map(e -> xmlElementCreator.apply(e, schemaContext));
    }

    @Override
    public Stream<XmlElement> elementStream(Predicate<? super XmlElement> predicate) {
        return elementStream().filter(predicate);
    }

    @Override
    public Stream<XmlElement> topmostElementStream(Predicate<? super XmlElement> predicate) {
        return underlyingElement
                .topmostElementStream(e -> predicate.test(xmlElementCreator.apply(e, schemaContext)))
                .map(e -> xmlElementCreator.apply(e, schemaContext));
    }

    @Override
    public Stream<XmlElement> childElementStream() {
        return underlyingElement.childElementStream()
                .map(e -> xmlElementCreator.apply(e, schemaContext));
    }

    @Override
    public Stream<XmlElement> childElementStream(Predicate<? super XmlElement> predicate) {
        return childElementStream().filter(predicate);
    }

    @Override
    public Stream<XmlElement> descendantElementOrSelfStream() {
        return underlyingElement.descendantElementOrSelfStream()
                .map(e -> xmlElementCreator.apply(e, schemaContext));
    }

    @Override
    public Stream<XmlElement> descendantElementOrSelfStream(Predicate<? super XmlElement> predicate) {
        return descendantElementOrSelfStream().filter(predicate);
    }

    @Override
    public Stream<XmlElement> descendantElementStream() {
        return underlyingElement.descendantElementStream()
                .map(e -> xmlElementCreator.apply(e, schemaContext));
    }

    @Override
    public Stream<XmlElement> descendantElementStream(Predicate<? super XmlElement> predicate) {
        return descendantElementStream().filter(predicate);
    }

    @Override
    public Stream<XmlElement> topmostDescendantElementOrSelfStream(Predicate<? super XmlElement> predicate) {
        return underlyingElement
                .topmostDescendantElementOrSelfStream(e -> predicate.test(xmlElementCreator.apply(e, schemaContext)))
                .map(e -> xmlElementCreator.apply(e, schemaContext));
    }

    @Override
    public Stream<XmlElement> topmostDescendantElementStream(Predicate<? super XmlElement> predicate) {
        return underlyingElement
                .topmostDescendantElementStream(e -> predicate.test(xmlElementCreator.apply(e, schemaContext)))
                .map(e -> xmlElementCreator.apply(e, schemaContext));
    }
}
