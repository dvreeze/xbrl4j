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

import eu.cdevreeze.xbrl4j.common.dom.defaultimpl.Document;
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
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentParsers;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests related to xml:base. Not a unit test. Inspired by and using the XBRL conformance suite.
 *
 * @author Chris de Vreeze
 */
public class XmlBaseTests {

    private static final URI confSuiteRootDir;

    static {
        try {
            confSuiteRootDir =
                    Objects.requireNonNull(XmlBaseTests.class.getResource(
                            "/conformancesuite/unzipped/XBRL-CONF-2014-12-10/")).toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLinkbaseRefXmlBase() throws URISyntaxException {
        URI schemaUri = confSuiteRootDir.resolve("Common/200-linkbase/201-03-LinkbaseRefXMLBase.xsd");
        Document schemaDoc = Document.from(DocumentParsers.instance().parse(schemaUri))
                .withUri(schemaUri);

        URI linkbaseUri = confSuiteRootDir.resolve("Common/200-linkbase/base/201-03-LinkbaseRefXMLBase-label.xml");
        Document linkbaseDoc = Document.from(DocumentParsers.instance().parse(linkbaseUri))
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
                .findBaseUri(Optional.of(schemaUri))
                .orElseThrow();

        assertEquals(schemaUri.resolve("./base/"), baseUri);

        URI linkbaseRefUri = baseUri.resolve(rawLinkbaseRefUri);

        assertEquals(linkbaseUri, linkbaseRefUri);

        assertEquals(
                linkbaseUri.resolve("../"),
                ((LinkbaseImpl) linkbase).underlyingElement().findBaseUri(Optional.of(linkbaseUri)).orElseThrow()
        );

        Loc firstLocator =
                linkbase.elementStream(Loc.class, loc -> loc.xlinkLabel().equals("aaa"))
                        .findFirst()
                        .orElseThrow();

        URI locHrefUri = ((LocImpl) firstLocator).underlyingElement().findBaseUri(Optional.of(linkbaseUri))
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
}
