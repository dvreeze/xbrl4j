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

package eu.cdevreeze.xbrl4j.common.dom;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import eu.cdevreeze.xbrl4j.common.xmlbase.XmlBaseResolver;
import eu.cdevreeze.xbrl4j.common.xpointer.XPointer;
import eu.cdevreeze.xbrl4j.common.xpointer.XPointers;
import eu.cdevreeze.yaidom4j.core.NamespaceScope;
import eu.cdevreeze.yaidom4j.dom.ancestryaware.ElementTree;
import eu.cdevreeze.yaidom4j.queryapi.AncestryAwareElementApi;

import javax.xml.namespace.QName;
import java.net.URI;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Default AncestryAwareElement implementation.
 *
 * @author Chris de Vreeze
 */
public class DefaultAncestryAwareElement implements AncestryAwareElement<DefaultAncestryAwareElement> {

    private final ElementTree.Element underlyingElement;

    public DefaultAncestryAwareElement(ElementTree.Element underlyingElement) {
        this.underlyingElement = underlyingElement;
    }

    @Override
    public AncestryAwareElementApi<ElementTree.Element> underlyingElement() {
        return underlyingElement;
    }

    @Override
    public NamespaceScope namespaceScope() {
        return underlyingElement.namespaceScopeOption().orElseThrow();
    }

    // Note that now we do not have a wildcard that cannot be captured in XmlBaseResolver

    @Override
    public Optional<URI> findBaseUri(Optional<URI> docUriOption, BiFunction<Optional<URI>, URI, URI> uriResolver) {
        return new XmlBaseResolver(uriResolver).findBaseUri(this, docUriOption);
    }

    // See comment above

    @Override
    public Optional<DefaultAncestryAwareElement> findElement(ImmutableList<XPointer> xpointers) {
        return XPointers.findElement(this, xpointers);
    }

    @Override
    public Optional<DefaultAncestryAwareElement> findElement(XPointer xpointer) {
        return XPointers.findElement(this, xpointer);
    }

    @Override
    public Optional<DefaultAncestryAwareElement> parentElementOption() {
        return underlyingElement.parentElementOption().map(DefaultAncestryAwareElement::new);
    }

    @Override
    public Stream<DefaultAncestryAwareElement> ancestorElementOrSelfStream() {
        return underlyingElement.ancestorElementOrSelfStream().map(DefaultAncestryAwareElement::new);
    }

    @Override
    public Stream<DefaultAncestryAwareElement> ancestorElementOrSelfStream(Predicate<? super DefaultAncestryAwareElement> predicate) {
        return underlyingElement
                .ancestorElementOrSelfStream(e -> predicate.test(new DefaultAncestryAwareElement(e)))
                .map(DefaultAncestryAwareElement::new);
    }

    @Override
    public Stream<DefaultAncestryAwareElement> ancestorElementStream() {
        return underlyingElement.ancestorElementStream().map(DefaultAncestryAwareElement::new);
    }

    @Override
    public Stream<DefaultAncestryAwareElement> ancestorElementStream(Predicate<? super DefaultAncestryAwareElement> predicate) {
        return underlyingElement
                .ancestorElementStream(e -> predicate.test(new DefaultAncestryAwareElement(e)))
                .map(DefaultAncestryAwareElement::new);
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
    public Optional<NamespaceScope> namespaceScopeOption() {
        return underlyingElement.namespaceScopeOption();
    }

    @Override
    public Stream<DefaultAncestryAwareElement> elementStream() {
        return underlyingElement.elementStream().map(DefaultAncestryAwareElement::new);
    }

    @Override
    public Stream<DefaultAncestryAwareElement> elementStream(Predicate<? super DefaultAncestryAwareElement> predicate) {
        return underlyingElement
                .elementStream(e -> predicate.test(new DefaultAncestryAwareElement(e)))
                .map(DefaultAncestryAwareElement::new);
    }

    @Override
    public Stream<DefaultAncestryAwareElement> topmostElementStream(Predicate<? super DefaultAncestryAwareElement> predicate) {
        return underlyingElement
                .topmostElementStream(e -> predicate.test(new DefaultAncestryAwareElement(e)))
                .map(DefaultAncestryAwareElement::new);
    }

    @Override
    public Stream<DefaultAncestryAwareElement> childElementStream() {
        return underlyingElement.childElementStream().map(DefaultAncestryAwareElement::new);
    }

    @Override
    public Stream<DefaultAncestryAwareElement> childElementStream(Predicate<? super DefaultAncestryAwareElement> predicate) {
        return underlyingElement
                .childElementStream(e -> predicate.test(new DefaultAncestryAwareElement(e)))
                .map(DefaultAncestryAwareElement::new);
    }

    @Override
    public Stream<DefaultAncestryAwareElement> descendantElementOrSelfStream() {
        return underlyingElement.descendantElementOrSelfStream().map(DefaultAncestryAwareElement::new);
    }

    @Override
    public Stream<DefaultAncestryAwareElement> descendantElementOrSelfStream(Predicate<? super DefaultAncestryAwareElement> predicate) {
        return underlyingElement
                .descendantElementOrSelfStream(e -> predicate.test(new DefaultAncestryAwareElement(e)))
                .map(DefaultAncestryAwareElement::new);
    }

    @Override
    public Stream<DefaultAncestryAwareElement> descendantElementStream() {
        return underlyingElement.descendantElementStream().map(DefaultAncestryAwareElement::new);
    }

    @Override
    public Stream<DefaultAncestryAwareElement> descendantElementStream(Predicate<? super DefaultAncestryAwareElement> predicate) {
        return underlyingElement
                .descendantElementStream(e -> predicate.test(new DefaultAncestryAwareElement(e)))
                .map(DefaultAncestryAwareElement::new);
    }

    @Override
    public Stream<DefaultAncestryAwareElement> topmostDescendantElementOrSelfStream(Predicate<? super DefaultAncestryAwareElement> predicate) {
        return underlyingElement
                .topmostDescendantElementOrSelfStream(e -> predicate.test(new DefaultAncestryAwareElement(e)))
                .map(DefaultAncestryAwareElement::new);
    }

    @Override
    public Stream<DefaultAncestryAwareElement> topmostDescendantElementStream(Predicate<? super DefaultAncestryAwareElement> predicate) {
        return underlyingElement
                .topmostDescendantElementStream(e -> predicate.test(new DefaultAncestryAwareElement(e)))
                .map(DefaultAncestryAwareElement::new);
    }
}
