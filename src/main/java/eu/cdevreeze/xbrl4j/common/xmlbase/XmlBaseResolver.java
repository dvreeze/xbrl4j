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

package eu.cdevreeze.xbrl4j.common.xmlbase;

import eu.cdevreeze.yaidom4j.queryapi.AncestryAwareElementApi;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Resolver of xml:base attributes in an (ancestry-aware) element node.
 *
 * @author Chris de Vreeze
 */
public final class XmlBaseResolver {

    private static final QName XML_BASE_QNAME = new QName(XMLConstants.XML_NS_URI, "base");

    /**
     * BiFunction taking an optional base URI and a URI to resolve against it (if present), returning the result URI
     */
    private final BiFunction<Optional<URI>, URI, URI> uriResolver;

    public XmlBaseResolver(BiFunction<Optional<URI>, URI, URI> uriResolver) {
        this.uriResolver = uriResolver;
    }

    /**
     * Creates a default XmlBaseResolver, which is not RFC 3986 compliant.
     */
    public XmlBaseResolver() {
        // See http://stackoverflow.com/questions/22203111/is-javas-uri-resolve-incompatible-with-rfc-3986-when-the-relative-uri-contains
        this((baseUriOption, uri) -> baseUriOption.map(b -> b.resolve(uri)).orElse(uri));
    }

    public BiFunction<Optional<URI>, URI, URI> uriResolver() {
        return uriResolver;
    }

    public <E extends AncestryAwareElementApi<E>> Optional<URI> findBaseUri(E element, Optional<URI> docUriOption) {
        Optional<E> currentElementOption = Optional.of(element);
        List<URI> xmlBaseUris = new ArrayList<>();

        while (currentElementOption.isPresent()) {
            E currentElement = currentElementOption.get();
            currentElement.attributeOption(XML_BASE_QNAME).ifPresent(u -> xmlBaseUris.add(URI.create(u)));
            currentElementOption = currentElement.parentElementOption();
        }

        Collections.reverse(xmlBaseUris); // Java 21 offers the more functional "reversed" method

        Optional<URI> uriOption = docUriOption;

        for (URI u : xmlBaseUris) {
            uriOption = Optional.of(uriResolver().apply(uriOption, u));
        }

        return uriOption;
    }
}
