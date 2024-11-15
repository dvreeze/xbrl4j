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

package eu.cdevreeze.xbrl4j.common.dom.defaultimpl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

/**
 * XML document.
 *
 * @author Chris de Vreeze
 */
public record Document(
        Optional<URI> uriOption,
        ImmutableList<CanBeDocumentChild> children
) {

    public Document {
        Objects.requireNonNull(uriOption);
        Objects.requireNonNull(children);

        Preconditions.checkArgument(
                children.stream().filter(n -> n instanceof ElementTree.Element).count() == 1
        );
    }

    public ElementTree.Element documentElement() {
        return children.stream()
                .filter(n -> n instanceof ElementTree.Element)
                .map(n -> (ElementTree.Element) n)
                .findFirst()
                .orElseThrow();
    }

    public eu.cdevreeze.yaidom4j.dom.immutabledom.Document underlyingDocument() {
        return new eu.cdevreeze.yaidom4j.dom.immutabledom.Document(
                uriOption,
                children.stream().map(CanBeDocumentChild::underlyingNode).collect(ImmutableList.toImmutableList())
        );
    }

    public static Document from(eu.cdevreeze.yaidom4j.dom.immutabledom.Document underlyingDocument) {
        return new Document(
                underlyingDocument.uriOption(),
                underlyingDocument.children().stream()
                        .map(n -> {
                            if (n instanceof eu.cdevreeze.yaidom4j.dom.immutabledom.Element e) {
                                return ElementTree.create(underlyingDocument.uriOption(), e).rootElement();
                            } else if (n instanceof eu.cdevreeze.yaidom4j.dom.immutabledom.Comment c) {
                                return new Comment(c.value());
                            } else if (n instanceof eu.cdevreeze.yaidom4j.dom.immutabledom.ProcessingInstruction pi) {
                                return new ProcessingInstruction(pi.target(), pi.data());
                            } else {
                                throw new RuntimeException("Unsupported document child node");
                            }
                        })
                        .collect(ImmutableList.toImmutableList())
        );
    }
}
