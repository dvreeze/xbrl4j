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

package eu.cdevreeze.xbrl4j.common.dom.saxon;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.sf.saxon.s9api.Axis;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmNodeKind;

import java.util.stream.Stream;

/**
 * Saxon document wrapper.
 *
 * @author Chris de Vreeze
 */
public final class SaxonDocument {

    private final XdmNode xdmNode;

    public SaxonDocument(XdmNode docNode) {
        Preconditions.checkArgument(docNode.getNodeKind().equals(XdmNodeKind.DOCUMENT));
        this.xdmNode = docNode;
    }

    public XdmNode xdmNode() {
        return xdmNode;
    }

    public SaxonNodes.Element documentElement() {
        return xdmNode.axisIterator(Axis.CHILD)
                .stream()
                .filter(n -> n.getNodeKind().equals(XdmNodeKind.ELEMENT))
                .findFirst()
                .map(SaxonNodes.Element::new)
                .orElseThrow();
    }

    public ImmutableList<SaxonNodes.CanBeDocumentChild> children() {
        return xdmNode.axisIterator(Axis.CHILD)
                .stream()
                .flatMap(n -> switch (n.getNodeKind()) {
                    case ELEMENT -> Stream.of(new SaxonNodes.Element(n));
                    case COMMENT -> Stream.of(new SaxonNodes.Comment(n));
                    case PROCESSING_INSTRUCTION -> Stream.of(new SaxonNodes.ProcessingInstruction(n));
                    default -> Stream.empty();
                })
                .collect(ImmutableList.toImmutableList());
    }
}
