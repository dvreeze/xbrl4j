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
import eu.cdevreeze.xbrl4j.common.xpointer.XPointer;
import eu.cdevreeze.yaidom4j.core.NamespaceScope;
import eu.cdevreeze.yaidom4j.dom.clark.ClarkNodes;
import eu.cdevreeze.yaidom4j.queryapi.AncestryAwareElementApi;

import java.net.URI;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Ancestry-aware element API with extra knowledge about xml:base and XPointer.
 * These elements are used as underlying elements of the XBRL model.
 * <p>
 * The idea is that the XBRL model is implemented (as type hierarchy under "XmlElement", which itself
 * extends yaidom4j interface "ElementApi") as a wrapper around any underlying element type that implements
 * this "AncestryAwareElement" interface. In order not to leak the self type as type variable to the
 * implementation of "XmlElement" (and subtypes), the underlying element is stored as a "AncestryAwareElement" with
 * a wildcard for its self type. In order to make that work without needing any knowledge about specific
 * implementations of "AncestryAwareElement", we indeed need type "AncestryAwareElement" instead of
 * its supertype "AncestryAwareElementApi". The latter type is not aware of xml:base and XPointer,
 * whereas this type "AncestryAwareElement" is. Hence, this subtype of "AncestryAwareElementApi" was
 * introduced.
 *
 * @author Chris de Vreeze
 */
public interface AncestryAwareElement<E extends AncestryAwareElementApi<E>> extends AncestryAwareElementApi<E> {

    Optional<URI> docUriOption();

    NamespaceScope namespaceScope();

    // xml:base (see XmlBaseResolver)

    Optional<URI> baseUriOption();

    Optional<URI> findBaseUri(BiFunction<Optional<URI>, URI, URI> uriResolver);

    // XPointer

    Optional<E> findElement(ImmutableList<XPointer> xpointers);

    Optional<E> findElement(XPointer xpointer);

    ClarkNodes.Element toClarkElement();
}
