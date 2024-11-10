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

package eu.cdevreeze.xbrl4j.model.factory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static eu.cdevreeze.xbrl4j.model.Names.*;

/**
 * Needed context from the XML Schema in order to create XmlElement trees.
 *
 * @author Chris de Vreeze
 */
public record SchemaContext(ImmutableMap<String, ImmutableMap<QName, QName>> substitutionGroupsByNamespace) {

    public SchemaContext {
        Preconditions.checkArgument(
                substitutionGroupsByNamespace.entrySet()
                        .stream()
                        .allMatch(
                                kv -> {
                                    String namespace = kv.getKey();
                                    Map<QName, QName> mappings = kv.getValue();
                                    return mappings.keySet().stream().allMatch(n -> n.getNamespaceURI().equals(namespace));
                                }
                        )
        );
    }

    public Optional<QName> findDirectSubstitutionGroup(QName elementName) {
        return Optional.ofNullable(substitutionGroupsByNamespace().get(elementName.getNamespaceURI()))
                .flatMap(kv -> Optional.ofNullable(kv.get(elementName)));
    }

    public ImmutableSet<QName> findSubstitutionGroups(QName elementName) {
        Optional<QName> optionalSubstGroup = findDirectSubstitutionGroup(elementName);

        if (optionalSubstGroup.isEmpty()) {
            return ImmutableSet.of();
        } else {
            ImmutableSet.Builder<QName> setBuilder = ImmutableSet.builder();
            QName sg = Objects.requireNonNull(optionalSubstGroup.orElseThrow());
            setBuilder.add(sg);
            // Recursion
            setBuilder.addAll(findSubstitutionGroups(sg));
            return setBuilder.build();
        }
    }

    public ImmutableSet<QName> findSubstitutionGroupsOrSelf(QName elementName) {
        ImmutableSet.Builder<QName> setBuilder = ImmutableSet.builder();
        setBuilder.add(elementName);
        setBuilder.addAll(findSubstitutionGroups(elementName));
        return setBuilder.build();
    }

    public ImmutableMap<QName, QName> allSubstitutionGroups() {
        Map<QName, QName> result = substitutionGroupsByNamespace()
                .values()
                .stream()
                .map(ImmutableMap::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return ImmutableMap.copyOf(result);
    }

    public SchemaContext plus(ImmutableMap<QName, QName> otherSubstitutionGroups) {
        ImmutableMap.Builder<QName, QName> builder = ImmutableMap.builder();
        builder.putAll(allSubstitutionGroups());
        builder.putAll(otherSubstitutionGroups);
        return SchemaContext.from(builder.build());
    }

    public SchemaContext plus(QName elementName, QName substitutionGroup) {
        return plus(ImmutableMap.of(elementName, substitutionGroup));
    }

    public static SchemaContext from(ImmutableMap<QName, QName> substitutionGroups) {
        Map<String, ImmutableMap<QName, QName>> substitutionGroupsByNamespace =
                substitutionGroups.entrySet()
                        .stream()
                        .collect(
                                Collectors.groupingBy(
                                        kv -> kv.getKey().getNamespaceURI(),
                                        Collectors.collectingAndThen(
                                                Collectors.toList(),
                                                ImmutableMap::copyOf
                                        )
                                )
                        );
        return new SchemaContext(ImmutableMap.copyOf(substitutionGroupsByNamespace));
    }

    public static SchemaContext defaultInstance() {
        return defaultInstance;
    }

    private static final SchemaContext defaultInstance = createDefaultInstance();

    private static SchemaContext createDefaultInstance() {
        ImmutableMap.Builder<QName, QName> schemaContextBuilder = ImmutableMap.builder();
        schemaContextBuilder.put(XBRLDT_HYPERCUBE_ITEM_QNAME, XBRLI_ITEM_QNAME);
        schemaContextBuilder.put(XBRLDT_DIMENSION_ITEM_QNAME, XBRLI_ITEM_QNAME);
        schemaContextBuilder.put(GEN_ARC_QNAME, XL_ARC_QNAME);
        schemaContextBuilder.put(GEN_LINK_QNAME, XL_EXTENDED_QNAME);
        schemaContextBuilder.put(LABEL_LABEL_QNAME, XL_RESOURCE_QNAME);
        schemaContextBuilder.put(REFERENCE_REFERENCE_QNAME, XL_RESOURCE_QNAME);
        schemaContextBuilder.put(REF_PUBLISHER_QNAME, LINK_PART_QNAME);
        schemaContextBuilder.put(REF_NAME_QNAME, LINK_PART_QNAME);
        schemaContextBuilder.put(REF_NUMBER_QNAME, LINK_PART_QNAME);
        schemaContextBuilder.put(REF_ISSUE_DATE_QNAME, LINK_PART_QNAME);
        schemaContextBuilder.put(REF_CHAPTER_QNAME, LINK_PART_QNAME);
        schemaContextBuilder.put(REF_ARTICLE_QNAME, LINK_PART_QNAME);
        schemaContextBuilder.put(REF_NOTE_QNAME, LINK_PART_QNAME);
        schemaContextBuilder.put(REF_SECTION_QNAME, LINK_PART_QNAME);
        schemaContextBuilder.put(REF_SUBSECTION_QNAME, LINK_PART_QNAME);
        schemaContextBuilder.put(REF_PARAGRAPH_QNAME, LINK_PART_QNAME);
        schemaContextBuilder.put(REF_SUBPARAGRAPH_QNAME, LINK_PART_QNAME);
        schemaContextBuilder.put(REF_CLAUSE_QNAME, LINK_PART_QNAME);
        schemaContextBuilder.put(REF_SUBCLAUSE_QNAME, LINK_PART_QNAME);
        schemaContextBuilder.put(REF_APPENDIX_QNAME, LINK_PART_QNAME);
        schemaContextBuilder.put(REF_EXAMPLE_QNAME, LINK_PART_QNAME);
        schemaContextBuilder.put(REF_PAGE_QNAME, LINK_PART_QNAME);
        schemaContextBuilder.put(REF_EXHIBIT_QNAME, LINK_PART_QNAME);
        schemaContextBuilder.put(REF_FOOTNOTE_QNAME, LINK_PART_QNAME);
        schemaContextBuilder.put(REF_SENTENCE_QNAME, LINK_PART_QNAME);
        schemaContextBuilder.put(REF_URI_QNAME, LINK_PART_QNAME);
        schemaContextBuilder.put(REF_URI_DATE_QNAME, LINK_PART_QNAME);
        return SchemaContext.from(schemaContextBuilder.build());
    }
}
