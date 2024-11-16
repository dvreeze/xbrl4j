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
import eu.cdevreeze.xbrl4j.common.xpointer.XPointer;
import eu.cdevreeze.xbrl4j.common.xpointer.XPointers;
import eu.cdevreeze.xbrl4j.model.XmlElement;
import eu.cdevreeze.xbrl4j.model.internal.xs.SchemaImpl;
import eu.cdevreeze.xbrl4j.model.link.Linkbase;
import eu.cdevreeze.xbrl4j.model.link.Loc;
import eu.cdevreeze.xbrl4j.model.xs.ItemDeclaration;
import eu.cdevreeze.xbrl4j.model.xs.Schema;
import eu.cdevreeze.xbrl4j.tests.support.SimpleTaxonomy;
import eu.cdevreeze.xbrl4j.tests.support.SimpleTaxonomyFactory;
import eu.cdevreeze.yaidom4j.queryapi.ElementApi;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests related to XPointer. Not a unit test. Inspired by and using the XBRL conformance suite.
 *
 * @author Chris de Vreeze
 */
public class XPointerTests {

    private static final URI confSuiteRootDir;

    static {
        try {
            confSuiteRootDir =
                    Objects.requireNonNull(XPointerTests.class.getResource(
                            "/conformancesuite/unzipped/XBRL-CONF-2014-12-10/")).toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testIdPointerUse() {
        SimpleTaxonomy taxo = createSimpleTaxonomy(List.of(
                "Common/200-linkbase/202-05-ElementLocatorExample-label.xml",
                "Common/200-linkbase/202-05-ElementLocatorExample.xsd"
        ));

        Schema schema = taxo.schemas().get(0);
        Linkbase linkbase = taxo.linkbases().get(0);

        Optional<Loc> locOption =
                linkbase.descendantElementStream(Loc.class, loc -> loc.xlinkLabel().equals("aaa2")).findFirst();

        assertTrue(locOption.isPresent());
        Loc loc = locOption.get();

        URI locUri = loc.xlinkHref();

        assertEquals("element(aaa)", locUri.getFragment());

        XPointer xpointer = XPointers.parseXPointer(locUri.getFragment());

        assertEquals("202-05-ElementLocatorExample.xsd", locUri.getSchemeSpecificPart());
        assertEquals(schema.docUriOption().orElseThrow(), linkbase.docUriOption().orElseThrow().resolve(locUri.getSchemeSpecificPart()));

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
    public void testChildSequencePointerUse() {
        SimpleTaxonomy taxo = createSimpleTaxonomy(List.of(
                "Common/200-linkbase/202-09-ElementSchemeXPointerLocatorExample-label.xml",
                "Common/200-linkbase/202-09-ElementSchemeXPointerLocatorExample.xsd"
        ));

        Schema schema = taxo.schemas().get(0);
        Linkbase linkbase = taxo.linkbases().get(0);

        Optional<Loc> locOption = linkbase.descendantElementStream(Loc.class).findFirst();

        assertTrue(locOption.isPresent());
        Loc loc = locOption.get();

        URI locUri = loc.xlinkHref();

        assertEquals("element(/1/3)", locUri.getFragment());

        XPointer xpointer = XPointers.parseXPointer(locUri.getFragment());

        assertEquals("202-09-ElementSchemeXPointerLocatorExample.xsd", locUri.getSchemeSpecificPart());
        assertEquals(schema.docUriOption().orElseThrow(), linkbase.docUriOption().orElseThrow().resolve(locUri.getSchemeSpecificPart()));

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
    public void testMultiplePointersUse() {
        SimpleTaxonomy taxo = createSimpleTaxonomy(List.of(
                "Common/200-linkbase/202-10-ElementSchemeXPointerLocatorExample-label.xml",
                "Common/200-linkbase/202-10-ElementSchemeXPointerLocatorExample.xsd"
        ));

        Schema schema = taxo.schemas().get(0);
        Linkbase linkbase = taxo.linkbases().get(0);

        Optional<Loc> locOption = linkbase.descendantElementStream(Loc.class).findFirst();

        assertTrue(locOption.isPresent());
        Loc loc = locOption.get();

        URI locUri = loc.xlinkHref();

        assertEquals("element(/1/17)element(/1/3)", locUri.getFragment());

        ImmutableList<XPointer> xpointers = XPointers.parseElementSchemePointers(locUri.getFragment());

        assertEquals("202-10-ElementSchemeXPointerLocatorExample.xsd", locUri.getSchemeSpecificPart());
        assertEquals(schema.docUriOption().orElseThrow(), linkbase.docUriOption().orElseThrow().resolve(locUri.getSchemeSpecificPart()));

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

    @Test
    public void testMultiplePointersUseAgain() {
        SimpleTaxonomy taxo = createSimpleTaxonomy(List.of(
                "Common/200-linkbase/202-10-ElementSchemeXPointerLocatorExample-label.xml",
                "Common/200-linkbase/202-10-ElementSchemeXPointerLocatorExample.xsd"
        ));

        Linkbase linkbase = taxo.linkbases().get(0);

        Optional<Loc> locOption = linkbase.descendantElementStream(Loc.class).findFirst();

        assertTrue(locOption.isPresent());
        Loc loc = locOption.get();

        URI locUri = loc.xlinkHref();

        assertEquals("element(/1/17)element(/1/3)", locUri.getFragment());

        Optional<XmlElement> foundElementOption = taxo.resolve(loc);

        assertTrue(foundElementOption.isPresent());
        assertInstanceOf(ItemDeclaration.class, foundElementOption.get());

        ItemDeclaration itemDeclaration = (ItemDeclaration) foundElementOption.get();
        assertEquals(Optional.of("aaa"), itemDeclaration.nameOption());
    }

    private SimpleTaxonomy createSimpleTaxonomy(List<String> relativeUris) {
        var taxoFactory = new SimpleTaxonomyFactory(confSuiteRootDir);
        return taxoFactory.createSimpleTaxonomy(relativeUris);
    }
}
