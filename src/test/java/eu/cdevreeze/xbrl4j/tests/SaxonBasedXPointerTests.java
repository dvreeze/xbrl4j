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

import com.google.common.collect.ImmutableList;
import eu.cdevreeze.xbrl4j.common.dom.saxon.SaxonDocument;
import eu.cdevreeze.xbrl4j.common.xpointer.XPointer;
import eu.cdevreeze.xbrl4j.common.xpointer.XPointers;
import eu.cdevreeze.xbrl4j.model.XmlElement;
import eu.cdevreeze.xbrl4j.model.factory.SchemaContext;
import eu.cdevreeze.xbrl4j.model.factory.XmlElementFactory;
import eu.cdevreeze.xbrl4j.model.internal.xs.SchemaImpl;
import eu.cdevreeze.xbrl4j.model.link.Linkbase;
import eu.cdevreeze.xbrl4j.model.link.Loc;
import eu.cdevreeze.xbrl4j.model.xs.ItemDeclaration;
import eu.cdevreeze.xbrl4j.model.xs.Schema;
import eu.cdevreeze.yaidom4j.queryapi.ElementApi;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import org.junit.jupiter.api.Test;

import javax.xml.transform.stream.StreamSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests related to XPointer, using Saxon. Not a unit test. Inspired by and using the XBRL conformance suite.
 *
 * @author Chris de Vreeze
 */
public class SaxonBasedXPointerTests {

    private static final Processor processor = new Processor(false);

    private static final URI confSuiteRootDir;

    static {
        try {
            confSuiteRootDir =
                    Objects.requireNonNull(SaxonBasedXPointerTests.class.getResource(
                            "/conformancesuite/unzipped/XBRL-CONF-2014-12-10/")).toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testIdPointerUse() throws SaxonApiException {
        DocumentBuilder docBuilder = processor.newDocumentBuilder();

        URI linkbaseUri = confSuiteRootDir.resolve("Common/200-linkbase/202-05-ElementLocatorExample-label.xml");
        SaxonDocument linkbaseDoc = new SaxonDocument(docBuilder.build(new StreamSource(linkbaseUri.toString())))
                .withUri(linkbaseUri);

        URI schemaUri = confSuiteRootDir.resolve("Common/200-linkbase/202-05-ElementLocatorExample.xsd");
        SaxonDocument schemaDoc = new SaxonDocument(docBuilder.build(new StreamSource(schemaUri.toString())))
                .withUri(schemaUri);

        SchemaContext schemaContext = SchemaContext.defaultInstance();
        XmlElementFactory elementFactory = new XmlElementFactory(schemaContext);

        Linkbase linkbase = elementFactory.optionallyCreateLinkbase(
                linkbaseDoc.documentElement()).orElseThrow();
        Schema schema = elementFactory.optionallyCreateSchema(
                schemaDoc.documentElement()).orElseThrow();

        Optional<Loc> locOption = linkbase.descendantElementStream(Loc.class, loc -> loc.xlinkLabel().equals("aaa2")).findFirst();

        assertTrue(locOption.isPresent());
        Loc loc = locOption.get();

        URI locUri = loc.xlinkHref();

        assertEquals("element(aaa)", locUri.getFragment());

        XPointer xpointer = XPointers.parseXPointer(locUri.getFragment());

        assertEquals("202-05-ElementLocatorExample.xsd", locUri.getSchemeSpecificPart());
        assertEquals(schemaDoc.uriOption().orElseThrow(), linkbaseUri.resolve(locUri.getSchemeSpecificPart()));

        Optional<XmlElement> foundElementOption = XPointers.findElement(schema, xpointer);

        assertEquals(
                ((SchemaImpl) schema).underlyingElement().findElement(xpointer).map(ElementApi::elementName),
                XPointers.findElement(schema, xpointer).map(ElementApi::elementName)
        );

        assertTrue(foundElementOption.isPresent());
        assertInstanceOf(ItemDeclaration.class, foundElementOption.get());

        ItemDeclaration itemDeclaration = (ItemDeclaration) foundElementOption.get();
        assertEquals(Optional.of("aaa"), itemDeclaration.nameOption());
    }

    @Test
    public void testChildSequencePointerUse() throws SaxonApiException {
        DocumentBuilder docBuilder = processor.newDocumentBuilder();

        URI linkbaseUri = confSuiteRootDir.resolve("Common/200-linkbase/202-09-ElementSchemeXPointerLocatorExample-label.xml");
        SaxonDocument linkbaseDoc = new SaxonDocument(docBuilder.build(new StreamSource(linkbaseUri.toString())))
                .withUri(linkbaseUri);

        URI schemaUri = confSuiteRootDir.resolve("Common/200-linkbase/202-09-ElementSchemeXPointerLocatorExample.xsd");
        SaxonDocument schemaDoc = new SaxonDocument(docBuilder.build(new StreamSource(schemaUri.toString())))
                .withUri(schemaUri);

        SchemaContext schemaContext = SchemaContext.defaultInstance();
        XmlElementFactory elementFactory = new XmlElementFactory(schemaContext);

        Linkbase linkbase = elementFactory.optionallyCreateLinkbase(
                linkbaseDoc.documentElement()).orElseThrow();
        Schema schema = elementFactory.optionallyCreateSchema(
                schemaDoc.documentElement()).orElseThrow();

        Optional<Loc> locOption = linkbase.descendantElementStream(Loc.class).findFirst();

        assertTrue(locOption.isPresent());
        Loc loc = locOption.get();

        URI locUri = loc.xlinkHref();

        assertEquals("element(/1/3)", locUri.getFragment());

        XPointer xpointer = XPointers.parseXPointer(locUri.getFragment());

        assertEquals("202-09-ElementSchemeXPointerLocatorExample.xsd", locUri.getSchemeSpecificPart());
        assertEquals(schemaDoc.uriOption().orElseThrow(), linkbaseUri.resolve(locUri.getSchemeSpecificPart()));

        Optional<XmlElement> foundElementOption = XPointers.findElement(schema, xpointer);

        assertEquals(
                ((SchemaImpl) schema).underlyingElement().findElement(xpointer).map(ElementApi::elementName),
                XPointers.findElement(schema, xpointer).map(ElementApi::elementName)
        );

        assertTrue(foundElementOption.isPresent());
        assertInstanceOf(ItemDeclaration.class, foundElementOption.get());

        ItemDeclaration itemDeclaration = (ItemDeclaration) foundElementOption.get();
        assertEquals(Optional.of("aaa"), itemDeclaration.nameOption());
    }

    @Test
    public void testMultiplePointersUse() throws SaxonApiException {
        DocumentBuilder docBuilder = processor.newDocumentBuilder();

        URI linkbaseUri = confSuiteRootDir.resolve("Common/200-linkbase/202-10-ElementSchemeXPointerLocatorExample-label.xml");
        SaxonDocument linkbaseDoc = new SaxonDocument(docBuilder.build(new StreamSource(linkbaseUri.toString())))
                .withUri(linkbaseUri);

        URI schemaUri = confSuiteRootDir.resolve("Common/200-linkbase/202-10-ElementSchemeXPointerLocatorExample.xsd");
        SaxonDocument schemaDoc = new SaxonDocument(docBuilder.build(new StreamSource(schemaUri.toString())))
                .withUri(schemaUri);

        SchemaContext schemaContext = SchemaContext.defaultInstance();
        XmlElementFactory elementFactory = new XmlElementFactory(schemaContext);

        Linkbase linkbase = elementFactory.optionallyCreateLinkbase(
                linkbaseDoc.documentElement()).orElseThrow();
        Schema schema = elementFactory.optionallyCreateSchema(
                schemaDoc.documentElement()).orElseThrow();

        Optional<Loc> locOption = linkbase.descendantElementStream(Loc.class).findFirst();

        assertTrue(locOption.isPresent());
        Loc loc = locOption.get();

        URI locUri = loc.xlinkHref();

        assertEquals("element(/1/17)element(/1/3)", locUri.getFragment());

        ImmutableList<XPointer> xpointers = XPointers.parseElementSchemePointers(locUri.getFragment());

        assertEquals("202-10-ElementSchemeXPointerLocatorExample.xsd", locUri.getSchemeSpecificPart());
        assertEquals(schemaDoc.uriOption().orElseThrow(), linkbaseUri.resolve(locUri.getSchemeSpecificPart()));

        Optional<XmlElement> foundElementOption = XPointers.findElement(schema, xpointers);

        assertEquals(
                ((SchemaImpl) schema).underlyingElement().findElement(xpointers).map(ElementApi::elementName),
                XPointers.findElement(schema, xpointers).map(ElementApi::elementName)
        );

        assertTrue(foundElementOption.isPresent());
        assertInstanceOf(ItemDeclaration.class, foundElementOption.get());

        ItemDeclaration itemDeclaration = (ItemDeclaration) foundElementOption.get();
        assertEquals(Optional.of("aaa"), itemDeclaration.nameOption());
    }
}
