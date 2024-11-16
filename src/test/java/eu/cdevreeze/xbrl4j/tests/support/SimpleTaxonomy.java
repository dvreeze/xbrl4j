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

package eu.cdevreeze.xbrl4j.tests.support;

import com.google.common.collect.ImmutableMap;
import eu.cdevreeze.xbrl4j.model.XmlElement;
import eu.cdevreeze.xbrl4j.model.internal.link.LinkbaseRefImpl;
import eu.cdevreeze.xbrl4j.model.internal.link.LocImpl;
import eu.cdevreeze.xbrl4j.model.link.LinkbaseRef;
import eu.cdevreeze.xbrl4j.model.link.Loc;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Very simple taxonomy for testing purposes.
 *
 * @author Chris de Vreeze
 */
public record SimpleTaxonomy(ImmutableMap<URI, XmlElement> documents) {

    public Optional<XmlElement> resolve(Loc loc) {
        URI uri = ((LocImpl) loc).underlyingElement().baseUriOption().orElseThrow().resolve(loc.xlinkHref());

        URI hrefWithoutFragment = withoutFragment(uri);
        Optional<String> fragmentOption = Optional.ofNullable(uri.getFragment());
        // No XPointer processing

        return Optional.ofNullable(documents.get(hrefWithoutFragment))
                .flatMap(d -> d.elementStream().filter(e -> e.idOption().equals(fragmentOption)).findFirst());
    }

    public Optional<XmlElement> resolve(LinkbaseRef linkbaseRef) {
        URI uri = ((LinkbaseRefImpl) linkbaseRef).underlyingElement()
                .baseUriOption()
                .orElseThrow()
                .resolve(linkbaseRef.href());

        URI hrefWithoutFragment = withoutFragment(uri);
        Optional<String> fragmentOption = Optional.ofNullable(uri.getFragment());
        // No XPointer processing

        return Optional.ofNullable(documents.get(hrefWithoutFragment))
                .flatMap(d -> d.elementStream().filter(e -> e.idOption().equals(fragmentOption)).findFirst());
    }

    private static URI withoutFragment(URI uri) {
        try {
            return new URI(uri.getScheme(), uri.getSchemeSpecificPart(), null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
