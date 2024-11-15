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
import eu.cdevreeze.xbrl4j.common.dom.saxon.SaxonDocument;
import eu.cdevreeze.xbrl4j.model.XmlElement;
import eu.cdevreeze.xbrl4j.model.factory.SchemaContext;
import eu.cdevreeze.xbrl4j.model.factory.XmlElementFactory;
import eu.cdevreeze.yaidom4j.core.NamespaceScope;
import eu.cdevreeze.yaidom4j.dom.immutabledom.Document;
import eu.cdevreeze.yaidom4j.dom.immutabledom.Element;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentPrinters;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.JaxpDomToImmutableDomConverter;
import eu.cdevreeze.yaidom4j.examples.scripts.PushUpNamespaceDeclarations;
import net.sf.saxon.dom.NodeOverNodeInfo;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import java.net.URI;
import java.util.Objects;

import static eu.cdevreeze.xbrl4j.model.Names.*;

/**
 * Shows the structure of the given taxonomy document, given as URI program argument.
 * One use case is to check that the model is as expected regarding the structure of the model
 * type hierarchy. The underlying element implementation wraps Saxon XDM nodes.
 *
 * @author Chris de Vreeze
 */
public class ShowDocumentStructureUsingSaxon {

    public static void main(String[] args) throws SaxonApiException, TransformerException {
        Objects.checkIndex(0, args.length);
        URI docUri = URI.create(args[0]);

        Processor processor = new Processor(false);
        DocumentBuilder docBuilder = processor.newDocumentBuilder();

        SaxonDocument doc = new SaxonDocument(docBuilder.build(new StreamSource(docUri.toString())));

        String sbrNs = "http://www.nltaxonomie.nl/2011/xbrl/xbrl-syntax-extension";
        SchemaContext schemaContext = SchemaContext.defaultInstance()
                .plus(new QName(sbrNs, "linkroleOrder"), XL_RESOURCE_QNAME)
                .plus(new QName(sbrNs, "domainItem"), XBRLI_ITEM_QNAME)
                .plus(new QName(sbrNs, "domainMemberItem"), XBRLI_ITEM_QNAME)
                .plus(new QName(sbrNs, "primaryDomainItem"), XBRLI_ITEM_QNAME)
                .plus(new QName(sbrNs, "presentationItem"), XBRLI_ITEM_QNAME)
                .plus(new QName(sbrNs, "presentationTuple"), XBRLI_TUPLE_QNAME)
                .plus(new QName(sbrNs, "specificationTuple"), XBRLI_TUPLE_QNAME);
        XmlElementFactory xmlElementFactory = new XmlElementFactory(schemaContext);
        XmlElement xmlElement = xmlElementFactory
                .createXmlElement(doc.documentElement());

        org.w3c.dom.Document w3cDomDoc = (org.w3c.dom.Document) NodeOverNodeInfo.wrap(doc.xdmNode().getUnderlyingNode());
        Document nativeDoc = JaxpDomToImmutableDomConverter.convertDocument(w3cDomDoc);

        NamespaceScope namespaceScope =
                PushUpNamespaceDeclarations.pushUpNamespaceDeclarations(
                        nativeDoc.documentElement()
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
}
