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

package eu.cdevreeze.xbrl4j.tests;

import eu.cdevreeze.xbrl4j.common.dom.saxon.SaxonDocument;
import eu.cdevreeze.xbrl4j.model.Names;
import eu.cdevreeze.xbrl4j.model.XmlElement;
import eu.cdevreeze.xbrl4j.model.factory.SchemaContext;
import eu.cdevreeze.xbrl4j.model.factory.XmlElementFactory;
import eu.cdevreeze.xbrl4j.model.internal.link.LinkbaseImpl;
import eu.cdevreeze.xbrl4j.model.internal.link.LinkbaseRefImpl;
import eu.cdevreeze.xbrl4j.model.internal.link.LocImpl;
import eu.cdevreeze.xbrl4j.model.link.Linkbase;
import eu.cdevreeze.xbrl4j.model.link.LinkbaseRef;
import eu.cdevreeze.xbrl4j.model.link.Loc;
import eu.cdevreeze.xbrl4j.model.xs.ConceptDeclaration;
import eu.cdevreeze.xbrl4j.model.xs.Schema;
import eu.cdevreeze.xbrl4j.tests.support.SimpleTaxonomy;
import eu.cdevreeze.xbrl4j.tests.support.SimpleTaxonomyFactoryUsingSaxon;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import org.junit.jupiter.api.Test;

import javax.xml.transform.stream.StreamSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static eu.cdevreeze.xbrl4j.model.Names.NAME_QNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests related to xml:base, using Saxon. Not a unit test. Inspired by and using the XBRL conformance suite.
 *
 * @author Chris de Vreeze
 */
public class SaxonBasedXmlBaseTests {

    private static final Processor processor = new Processor(false);

    private static final URI confSuiteRootDir;

    static {
        try {
            confSuiteRootDir =
                    Objects.requireNonNull(SaxonBasedXmlBaseTests.class.getResource(
                            "/conformancesuite/unzipped/XBRL-CONF-2014-12-10/")).toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLinkbaseRefXmlBase() throws URISyntaxException, SaxonApiException {
        DocumentBuilder docBuilder = processor.newDocumentBuilder();

        URI schemaUri = confSuiteRootDir.resolve("Common/200-linkbase/201-03-LinkbaseRefXMLBase.xsd");
        SaxonDocument schemaDoc = new SaxonDocument(docBuilder.build(new StreamSource(schemaUri.toString())))
                .withUri(schemaUri);

        URI linkbaseUri = confSuiteRootDir.resolve("Common/200-linkbase/base/201-03-LinkbaseRefXMLBase-label.xml");
        SaxonDocument linkbaseDoc = new SaxonDocument(docBuilder.build(new StreamSource(linkbaseUri.toString())))
                .withUri(linkbaseUri);

        SchemaContext schemaContext = SchemaContext.defaultInstance();
        XmlElementFactory elementFactory = new XmlElementFactory(schemaContext);

        Schema schema = elementFactory.optionallyCreateSchema(
                schemaDoc.documentElement()).orElseThrow();
        Linkbase linkbase = elementFactory.optionallyCreateLinkbase(
                linkbaseDoc.documentElement()).orElseThrow();

        Optional<LinkbaseRef> linkbaseRefOption =
                schema.descendantElementStream(LinkbaseRef.class).findFirst();

        assertTrue(linkbaseRefOption.isPresent());
        LinkbaseRef linkbaseRef = linkbaseRefOption.get();

        URI rawLinkbaseRefUri = linkbaseRef.href();
        assertEquals(URI.create("201-03-LinkbaseRefXMLBase-label.xml"), rawLinkbaseRefUri);

        URI baseUri = ((LinkbaseRefImpl) linkbaseRef).underlyingElement()
                .baseUriOption()
                .orElseThrow();

        assertEquals(schemaUri.resolve("./base/"), baseUri);

        URI linkbaseRefUri = baseUri.resolve(rawLinkbaseRefUri);

        assertEquals(linkbaseUri, linkbaseRefUri);

        assertEquals(
                linkbaseUri.resolve("../"),
                ((LinkbaseImpl) linkbase).underlyingElement().baseUriOption().orElseThrow()
        );

        Loc firstLocator =
                linkbase.elementStream(Loc.class, loc -> loc.xlinkLabel().equals("aaa"))
                        .findFirst()
                        .orElseThrow();

        URI locHrefUri = ((LocImpl) firstLocator).underlyingElement().baseUriOption()
                .orElseThrow().resolve(firstLocator.xlinkHref());

        assertEquals(
                new URI(schemaUri.getScheme(), schemaUri.getSchemeSpecificPart(), "fixedAssets"),
                locHrefUri
        );

        assertTrue(
                schema.childElementStream(
                        ConceptDeclaration.class,
                        e -> e.idOption().filter(v -> v.equals("fixedAssets")).isPresent()
                ).findFirst().isPresent()
        );
    }

    @Test
    public void testLinkbaseRefXmlBaseAgain() {
        SimpleTaxonomy taxo = createSimpleTaxonomy(List.of(
                "Common/200-linkbase/201-03-LinkbaseRefXMLBase.xsd",
                "Common/200-linkbase/base/201-03-LinkbaseRefXMLBase-label.xml"
        ));

        Schema schema = taxo.schemas().get(0);
        Linkbase linkbase = taxo.linkbases().get(0);

        LinkbaseRef linkbaseRef =
                schema.descendantElementStream(LinkbaseRef.class).findFirst().orElseThrow();

        Optional<XmlElement> linkbaseOption = taxo.resolveWithoutXPointer(linkbaseRef);
        assertTrue(linkbaseOption.isPresent());

        assertEquals(
                ((LinkbaseImpl) linkbase).underlyingElement().toClarkElement(),
                ((LinkbaseImpl) linkbaseOption.orElseThrow()).underlyingElement().toClarkElement()
        );

        Loc firstLocator =
                linkbase.elementStream(Loc.class, loc -> loc.xlinkLabel().equals("aaa"))
                        .findFirst()
                        .orElseThrow();

        Optional<XmlElement> elementOption = taxo.resolveWithoutXPointer(firstLocator);

        assertEquals(Optional.of(Names.XS_ELEMENT_QNAME), elementOption.map(XmlElement::elementName));
    }

    @Test
    public void testXmlBaseProcessing() {
        SimpleTaxonomy taxo = createSimpleTaxonomy(List.of(
                "Common/200-linkbase/202-03-HrefResolutionXMLBase.xsd",
                "Common/200-linkbase/base/202-03-HrefResolutionXMLBase-label.xml"
        ));

        Schema schema = taxo.schemas().get(0);
        Linkbase linkbase = taxo.linkbases().get(0);

        LinkbaseRef linkbaseRef =
                schema.descendantElementStream(LinkbaseRef.class).findFirst().orElseThrow();

        Optional<XmlElement> linkbaseOption = taxo.resolveWithoutXPointer(linkbaseRef);
        assertTrue(linkbaseOption.isPresent());

        assertEquals(
                ((LinkbaseImpl) linkbase).underlyingElement().toClarkElement(),
                ((LinkbaseImpl) linkbaseOption.orElseThrow()).underlyingElement().toClarkElement()
        );

        Loc firstLocator =
                linkbase.elementStream(Loc.class, loc -> loc.xlinkLabel().equals("aaa"))
                        .findFirst()
                        .orElseThrow();

        Optional<XmlElement> elementOption = taxo.resolveWithoutXPointer(firstLocator);

        assertEquals(Optional.of(Names.XS_ELEMENT_QNAME), elementOption.map(XmlElement::elementName));
        assertEquals(Optional.of("changeInRetainedEarnings"), elementOption.flatMap(e -> e.attributeOption(NAME_QNAME)));
    }

    @Test
    public void testXmlBaseProcessingAlternativeB() {
        SimpleTaxonomy taxo = createSimpleTaxonomy(List.of(
                "Common/200-linkbase/202-03b-HrefResolutionXMLBase.xsd",
                "Common/200-linkbase/base/202-03b-HrefResolutionXMLBase-label.xml"
        ));

        Schema schema = taxo.schemas().get(0);
        Linkbase linkbase = taxo.linkbases().get(0);

        LinkbaseRef linkbaseRef =
                schema.descendantElementStream(LinkbaseRef.class).findFirst().orElseThrow();

        Optional<XmlElement> linkbaseOption = taxo.resolveWithoutXPointer(linkbaseRef);
        assertTrue(linkbaseOption.isPresent());

        assertEquals(
                ((LinkbaseImpl) linkbase).underlyingElement().toClarkElement(),
                ((LinkbaseImpl) linkbaseOption.orElseThrow()).underlyingElement().toClarkElement()
        );

        Loc firstLocator =
                linkbase.elementStream(Loc.class, loc -> loc.xlinkLabel().equals("aaa"))
                        .findFirst()
                        .orElseThrow();

        Optional<XmlElement> elementOption = taxo.resolveWithoutXPointer(firstLocator);

        assertEquals(Optional.of(Names.XS_ELEMENT_QNAME), elementOption.map(XmlElement::elementName));
        assertEquals(Optional.of("changeInRetainedEarnings"), elementOption.flatMap(e -> e.attributeOption(NAME_QNAME)));
    }

    @Test
    public void testXmlBaseProcessingAlternativeC() {
        SimpleTaxonomy taxo = createSimpleTaxonomy(List.of(
                "Common/200-linkbase/202-03c-HrefResolutionXMLBase.xsd",
                "Common/200-linkbase/base/202-03c-HrefResolutionXMLBase-label.xml"
        ));

        Schema schema = taxo.schemas().get(0);

        LinkbaseRef linkbaseRef =
                schema.descendantElementStream(LinkbaseRef.class).findFirst().orElseThrow();

        Optional<XmlElement> linkbaseOption = taxo.resolveWithoutXPointer(linkbaseRef);
        assertTrue(linkbaseOption.isEmpty());
    }

    @Test
    public void testXmlBaseProcessingAlternativeD() {
        SimpleTaxonomy taxo = createSimpleTaxonomy(List.of(
                "Common/200-linkbase/202-03d-HrefResolutionXMLBase.xsd",
                "Common/200-linkbase/base/base/202-03d-HrefResolutionXMLBase-label.xml"
        ));

        Schema schema = taxo.schemas().get(0);
        Linkbase linkbase = taxo.linkbases().get(0);

        LinkbaseRef linkbaseRef =
                schema.descendantElementStream(LinkbaseRef.class).findFirst().orElseThrow();

        Optional<XmlElement> linkbaseOption = taxo.resolveWithoutXPointer(linkbaseRef);
        assertTrue(linkbaseOption.isPresent());

        assertEquals(
                ((LinkbaseImpl) linkbase).underlyingElement().toClarkElement(),
                ((LinkbaseImpl) linkbaseOption.orElseThrow()).underlyingElement().toClarkElement()
        );

        Loc firstLocator =
                linkbase.elementStream(Loc.class, loc -> loc.xlinkLabel().equals("aaa"))
                        .findFirst()
                        .orElseThrow();

        Optional<XmlElement> elementOption = taxo.resolveWithoutXPointer(firstLocator);

        assertEquals(Optional.of(Names.XS_ELEMENT_QNAME), elementOption.map(XmlElement::elementName));
        assertEquals(Optional.of("changeInRetainedEarnings"), elementOption.flatMap(e -> e.attributeOption(NAME_QNAME)));
    }

    @Test
    public void testXmlBaseProcessingAlternativeE() {
        SimpleTaxonomy taxo = createSimpleTaxonomy(List.of(
                "Common/200-linkbase/202-03e-HrefResolutionXMLBase.xsd",
                "Common/200-linkbase/base/base/202-03e-HrefResolutionXMLBase-label.xml"
        ));

        Schema schema = taxo.schemas().get(0);
        Linkbase linkbase = taxo.linkbases().get(0);

        LinkbaseRef linkbaseRef =
                schema.descendantElementStream(LinkbaseRef.class).findFirst().orElseThrow();

        Optional<XmlElement> linkbaseOption = taxo.resolveWithoutXPointer(linkbaseRef);
        assertTrue(linkbaseOption.isPresent());

        assertEquals(
                ((LinkbaseImpl) linkbase).underlyingElement().toClarkElement(),
                ((LinkbaseImpl) linkbaseOption.orElseThrow()).underlyingElement().toClarkElement()
        );

        Loc firstLocator =
                linkbase.elementStream(Loc.class, loc -> loc.xlinkLabel().equals("aaa"))
                        .findFirst()
                        .orElseThrow();

        Optional<XmlElement> elementOption = taxo.resolveWithoutXPointer(firstLocator);

        assertEquals(Optional.of(Names.XS_ELEMENT_QNAME), elementOption.map(XmlElement::elementName));
        assertEquals(Optional.of("changeInRetainedEarnings"), elementOption.flatMap(e -> e.attributeOption(NAME_QNAME)));
    }

    @Test
    public void testXmlBaseProcessingAlternativeF() {
        SimpleTaxonomy taxo = createSimpleTaxonomy(List.of(
                "Common/200-linkbase/base/base/202-03f-HrefResolutionXMLBase.xsd",
                "Common/200-linkbase/202-03f-HrefResolutionXMLBase-label.xml"
        ));

        Schema schema = taxo.schemas().get(0);
        Linkbase linkbase = taxo.linkbases().get(0);

        LinkbaseRef linkbaseRef =
                schema.descendantElementStream(LinkbaseRef.class).findFirst().orElseThrow();

        Optional<XmlElement> linkbaseOption = taxo.resolveWithoutXPointer(linkbaseRef);
        assertTrue(linkbaseOption.isPresent());

        assertEquals(
                ((LinkbaseImpl) linkbase).underlyingElement().toClarkElement(),
                ((LinkbaseImpl) linkbaseOption.orElseThrow()).underlyingElement().toClarkElement()
        );

        Loc firstLocator =
                linkbase.elementStream(Loc.class, loc -> loc.xlinkLabel().equals("aaa"))
                        .findFirst()
                        .orElseThrow();

        Optional<XmlElement> elementOption = taxo.resolveWithoutXPointer(firstLocator);

        assertEquals(Optional.of(Names.XS_ELEMENT_QNAME), elementOption.map(XmlElement::elementName));
        assertEquals(Optional.of("changeInRetainedEarnings"), elementOption.flatMap(e -> e.attributeOption(NAME_QNAME)));
    }

    @Test
    public void testXmlBaseProcessingAlternativeFAgain() {
        SimpleTaxonomy taxo = createSimpleTaxonomy(List.of(
                "Common/200-linkbase/base/base/202-03f-HrefResolutionXMLBase.xsd",
                "Common/200-linkbase/202-03f-HrefResolutionXMLBase-label.xml"
        ));

        Schema schema = taxo.schemas().get(0);
        Linkbase linkbase = taxo.linkbases().get(0);

        LinkbaseRef linkbaseRef =
                schema.descendantElementStream(LinkbaseRef.class).findFirst().orElseThrow();

        // Using method resolve instead of resolveWithoutXPointer (result is the same in this case)
        Optional<XmlElement> linkbaseOption = taxo.resolve(linkbaseRef);
        assertTrue(linkbaseOption.isPresent());

        assertEquals(
                ((LinkbaseImpl) linkbase).underlyingElement().toClarkElement(),
                ((LinkbaseImpl) linkbaseOption.orElseThrow()).underlyingElement().toClarkElement()
        );

        Loc firstLocator =
                linkbase.elementStream(Loc.class, loc -> loc.xlinkLabel().equals("aaa"))
                        .findFirst()
                        .orElseThrow();

        // Using method resolve instead of resolveWithoutXPointer (result is the same in this case)
        Optional<XmlElement> elementOption = taxo.resolve(firstLocator);

        assertEquals(Optional.of(Names.XS_ELEMENT_QNAME), elementOption.map(XmlElement::elementName));
        assertEquals(Optional.of("changeInRetainedEarnings"), elementOption.flatMap(e -> e.attributeOption(NAME_QNAME)));
    }

    private SimpleTaxonomy createSimpleTaxonomy(List<String> relativeUris) {
        var taxoFactory = new SimpleTaxonomyFactoryUsingSaxon(processor, confSuiteRootDir);
        return taxoFactory.createSimpleTaxonomy(relativeUris);
    }
}
