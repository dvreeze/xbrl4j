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

package eu.cdevreeze.xbrl4j.console;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import eu.cdevreeze.xbrl4j.model.XmlElement;
import eu.cdevreeze.xbrl4j.model.factory.SchemaContext;
import eu.cdevreeze.xbrl4j.model.factory.XmlElementFactory;
import eu.cdevreeze.yaidom4j.core.NamespaceScope;
import eu.cdevreeze.yaidom4j.dom.ancestryaware.Document;
import eu.cdevreeze.yaidom4j.dom.immutabledom.Element;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentParsers;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentPrinters;
import eu.cdevreeze.yaidom4j.examples.scripts.PushUpNamespaceDeclarations;

import javax.xml.namespace.QName;
import java.net.URI;
import java.util.Objects;

import static eu.cdevreeze.xbrl4j.model.Names.*;

/**
 * Shows the structure of the given taxonomy document, given as URI program argument.
 * One use case is to check that the model is as expected regarding the structure of the model
 * type hierarchy.
 *
 * @author Chris de Vreeze
 */
public class ShowDocumentStructure {

    public static void main(String[] args) {
        Objects.checkIndex(0, args.length);
        URI docUri = URI.create(args[0]);

        Document doc = Document.from(DocumentParsers.instance().parse(docUri));
        SchemaContext schemaContext = createSchemaContext();
        XmlElementFactory xmlElementFactory = new XmlElementFactory(schemaContext);
        XmlElement xmlElement = xmlElementFactory.createXmlElement(doc.documentElement());

        NamespaceScope namespaceScope =
                PushUpNamespaceDeclarations.pushUpNamespaceDeclarations(
                        doc.documentElement().underlyingElement()
                ).namespaceScope();
        Element structure = extractStructure(xmlElement, namespaceScope);

        String structureXmlString = DocumentPrinters.instance().print(structure);
        System.out.println(structureXmlString);
    }

    public static Element extractStructure(XmlElement xmlElement, NamespaceScope namespaceScope) {
        // Recursive
        return new Element(
                xmlElement.elementName(),
                addClassNameAttribute(xmlElement.attributes(), xmlElement.getClass().getSimpleName()),
                namespaceScope,
                xmlElement.childElementStream()
                        .map(e -> extractStructure(e, namespaceScope))
                        .collect(ImmutableList.toImmutableList())
        );
    }

    private static ImmutableMap<QName, String> addClassNameAttribute(ImmutableMap<QName, String> attrs, String className) {
        ImmutableMap.Builder<QName, String> builder = ImmutableMap.builder();
        builder.put(new QName("className"), className);
        builder.putAll(attrs);
        return builder.build();
    }

    private static SchemaContext createSchemaContext() {
        ImmutableMap.Builder<QName, QName> schemaContextBuilder = ImmutableMap.builder();
        schemaContextBuilder.put(GEN_ARC_QNAME, XL_ARC_QNAME);
        schemaContextBuilder.put(GEN_LINK_QNAME, XL_EXTENDED_QNAME);
        schemaContextBuilder.put(LABEL_LABEL_QNAME, XL_RESOURCE_QNAME);
        schemaContextBuilder.put(REFERENCE_REFERENCE_QNAME, XL_RESOURCE_QNAME);
        schemaContextBuilder.put(new QName(REF_NS, "Publisher"), LINK_PART_QNAME);
        schemaContextBuilder.put(new QName(REF_NS, "Name"), LINK_PART_QNAME);
        schemaContextBuilder.put(new QName(REF_NS, "Number"), LINK_PART_QNAME);
        schemaContextBuilder.put(new QName(REF_NS, "IssueDate"), LINK_PART_QNAME);
        schemaContextBuilder.put(new QName(REF_NS, "Chapter"), LINK_PART_QNAME);
        schemaContextBuilder.put(new QName(REF_NS, "Article"), LINK_PART_QNAME);
        schemaContextBuilder.put(new QName(REF_NS, "Note"), LINK_PART_QNAME);
        schemaContextBuilder.put(new QName(REF_NS, "Section"), LINK_PART_QNAME);
        schemaContextBuilder.put(new QName(REF_NS, "Subsection"), LINK_PART_QNAME);
        schemaContextBuilder.put(new QName(REF_NS, "Paragraph"), LINK_PART_QNAME);
        schemaContextBuilder.put(new QName(REF_NS, "Subparagraph"), LINK_PART_QNAME);
        schemaContextBuilder.put(new QName(REF_NS, "Clause"), LINK_PART_QNAME);
        schemaContextBuilder.put(new QName(REF_NS, "Subclause"), LINK_PART_QNAME);
        schemaContextBuilder.put(new QName(REF_NS, "Appendix"), LINK_PART_QNAME);
        schemaContextBuilder.put(new QName(REF_NS, "Example"), LINK_PART_QNAME);
        schemaContextBuilder.put(new QName(REF_NS, "Page"), LINK_PART_QNAME);
        schemaContextBuilder.put(new QName(REF_NS, "Exhibit"), LINK_PART_QNAME);
        schemaContextBuilder.put(new QName(REF_NS, "Footnote"), LINK_PART_QNAME);
        schemaContextBuilder.put(new QName(REF_NS, "Sentence"), LINK_PART_QNAME);
        schemaContextBuilder.put(new QName(REF_NS, "URI"), LINK_PART_QNAME);
        schemaContextBuilder.put(new QName(REF_NS, "URIDate"), LINK_PART_QNAME);
        schemaContextBuilder.put(new QName("http://www.nltaxonomie.nl/2011/xbrl/xbrl-syntax-extension", "linkroleOrder"), XL_RESOURCE_QNAME);
        return new SchemaContext(schemaContextBuilder.build());
    }
}
