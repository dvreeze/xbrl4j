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
import eu.cdevreeze.yaidom4j.queryapi.AncestryAwareElementApi;

import java.net.URI;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Ancestry-aware element API with extra knowledge about xml:base and XPointer.
 * These elements are used as underlying elements of the XBRL model.
 * They can even be used with wildcards for the self type, without having to know the exact self type.
 * <p>
 * The extra element wrapping layer does increase memory footprint. On the other hand, as opposed to
 * plain "AncestryAwareElement", it offers xml:base support and XPointer support for "underlying"
 * elements without any knowledge of the exact element type.
 *
 * @author Chris de Vreeze
 */
public interface AncestryAwareElement<E extends AncestryAwareElementApi<E>> extends AncestryAwareElementApi<E> {

    AncestryAwareElementApi<?> underlyingElement();

    NamespaceScope namespaceScope();

    // xml:base (see XmlBaseResolver)

    Optional<URI> findBaseUri(Optional<URI> docUriOption, BiFunction<Optional<URI>, URI, URI> uriResolver);

    // XPointer

    Optional<E> findElement(ImmutableList<XPointer> xpointers);

    Optional<E> findElement(XPointer xpointer);
}
