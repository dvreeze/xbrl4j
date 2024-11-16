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
import eu.cdevreeze.xbrl4j.common.dom.defaultimpl.Document;
import eu.cdevreeze.xbrl4j.model.XmlElement;
import eu.cdevreeze.xbrl4j.model.factory.SchemaContext;
import eu.cdevreeze.xbrl4j.model.factory.XmlElementFactory;
import eu.cdevreeze.xbrl4j.model.link.Linkbase;
import eu.cdevreeze.xbrl4j.model.xs.Schema;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentParsers;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Factory of SimpleTaxonomy objects for conformance suite files.
 *
 * @author Chris de Vreeze
 */
public class SimpleTaxonomyFactory {

    private final URI confSuiteRootDir;

    public SimpleTaxonomyFactory(URI confSuiteRootDir) {
        this.confSuiteRootDir = confSuiteRootDir;
    }

    public SimpleTaxonomy createSimpleTaxonomy(List<String> relativeUris) {
        SchemaContext schemaContext = SchemaContext.defaultInstance();
        XmlElementFactory elementFactory = new XmlElementFactory(schemaContext);

        List<URI> taxoDocUris = relativeUris.stream().map(confSuiteRootDir::resolve).toList();

        List<Map.Entry<URI, ? extends XmlElement>> uriDocPairs =
                taxoDocUris.stream()
                        .map(u -> {
                            Document doc = Document.from(DocumentParsers.instance().parse(u).withUri(u));
                            return Map.entry(u, doc);
                        })
                        .map(kv -> {
                            if (kv.getKey().getSchemeSpecificPart().endsWith(".xsd")) {
                                Schema schema = elementFactory.optionallyCreateSchema(
                                        kv.getValue().documentElement()).orElseThrow();
                                return Map.entry(kv.getKey(), schema);
                            } else {
                                Linkbase linkbase = elementFactory.optionallyCreateLinkbase(
                                        kv.getValue().documentElement()).orElseThrow();
                                return Map.entry(kv.getKey(), linkbase);
                            }
                        })
                        .toList();

        return new SimpleTaxonomy(ImmutableMap.copyOf(uriDocPairs));
    }
}
