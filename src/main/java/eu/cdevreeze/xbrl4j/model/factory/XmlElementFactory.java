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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import eu.cdevreeze.xbrl4j.model.XmlElement;
import eu.cdevreeze.xbrl4j.model.gen.GenElement;
import eu.cdevreeze.xbrl4j.model.internal.OtherXmlElementImpl;
import eu.cdevreeze.xbrl4j.model.internal.gen.GenericArcImpl;
import eu.cdevreeze.xbrl4j.model.internal.gen.GenericLinkImpl;
import eu.cdevreeze.xbrl4j.model.internal.gen.OtherGenElementImpl;
import eu.cdevreeze.xbrl4j.model.internal.label.GenericLabelImpl;
import eu.cdevreeze.xbrl4j.model.internal.label.OtherLabelElementImpl;
import eu.cdevreeze.xbrl4j.model.internal.link.*;
import eu.cdevreeze.xbrl4j.model.internal.ref.*;
import eu.cdevreeze.xbrl4j.model.internal.reference.GenericReferenceImpl;
import eu.cdevreeze.xbrl4j.model.internal.reference.OtherReferenceElementImpl;
import eu.cdevreeze.xbrl4j.model.internal.xl.OtherXlArcImpl;
import eu.cdevreeze.xbrl4j.model.internal.xl.OtherXlExtendedLinkImpl;
import eu.cdevreeze.xbrl4j.model.internal.xl.OtherXlResourceImpl;
import eu.cdevreeze.xbrl4j.model.internal.xs.*;
import eu.cdevreeze.xbrl4j.model.label.LabelElement;
import eu.cdevreeze.xbrl4j.model.link.LinkElement;
import eu.cdevreeze.xbrl4j.model.link.Linkbase;
import eu.cdevreeze.xbrl4j.model.ref.RefElement;
import eu.cdevreeze.xbrl4j.model.reference.ReferenceElement;
import eu.cdevreeze.xbrl4j.model.xl.XlArc;
import eu.cdevreeze.xbrl4j.model.xl.XlExtendedLink;
import eu.cdevreeze.xbrl4j.model.xl.XlResource;
import eu.cdevreeze.xbrl4j.model.xs.ElementDeclaration;
import eu.cdevreeze.xbrl4j.model.xs.Schema;
import eu.cdevreeze.xbrl4j.model.xs.SchemaElement;
import eu.cdevreeze.yaidom4j.queryapi.AncestryAwareElementApi;

import javax.xml.namespace.QName;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static eu.cdevreeze.xbrl4j.model.Names.*;

/**
 * Factory of XmlElement instances. Must be very fast when used. It is a bit expensive to create,
 * but immutable and thread-safe, so an instance can be shared freely.
 *
 * @author Chris de Vreeze
 */
public class XmlElementFactory {

    private final SchemaContext schemaContext;

    private final ImmutableMap<QName, Function<AncestryAwareElementApi<?>, SchemaElement>> schemaElementCreators =
            createSchemaElementCreatorMap();

    private final ImmutableMap<QName, Function<AncestryAwareElementApi<?>, LinkElement>> linkElementCreators =
            createLinkElementCreatorMap();

    private final ImmutableMap<QName, Function<AncestryAwareElementApi<?>, RefElement>> refElementCreators =
            createRefElementCreatorMap();

    private final ImmutableMap<QName, Function<AncestryAwareElementApi<?>, GenElement>> genElementCreators =
            createGenElementCreatorMap();

    private final ImmutableMap<QName, Function<AncestryAwareElementApi<?>, LabelElement>> labelElementCreators =
            createLabelElementCreatorMap();

    private final ImmutableMap<QName, Function<AncestryAwareElementApi<?>, ReferenceElement>> referenceElementCreators =
            createReferenceElementCreatorMap();

    private final ImmutableMap<QName, Function<AncestryAwareElementApi<?>, XmlElement>> commonlyUsedElementCreators =
            createCommonlyUsedElementCreatorMap();

    public XmlElementFactory(SchemaContext schemaContext) {
        this.schemaContext = schemaContext;
    }

    public SchemaContext schemaContext() {
        return schemaContext;
    }

    public XmlElement createXmlElement(AncestryAwareElementApi<?> underlyingElement) {
        Optional<XmlElement> optionalCommonlyUsedElement =
                Optional.ofNullable(commonlyUsedElementCreators.get(underlyingElement.elementName()))
                        .map(f -> f.apply(underlyingElement));

        if (optionalCommonlyUsedElement.isPresent()) {
            return optionalCommonlyUsedElement.orElseThrow();
        }

        // Only now look at substitution groups

        Set<QName> sgsOrSelf = schemaContext().findSubstitutionGroupsOrSelf(underlyingElement.elementName());
        return createXmlElement(underlyingElement, sgsOrSelf);
    }

    public XmlElement createXmlElement(AncestryAwareElementApi<?> underlyingElement, Set<QName> substitutionGroupsOrSelf) {
        return optionallyCreateXmlElement(underlyingElement, substitutionGroupsOrSelf)
                .or(() -> optionallyCreateOtherXlArc(underlyingElement, substitutionGroupsOrSelf).map(e -> (XmlElement) e))
                .or(() -> optionallyCreateOtherXlLink(underlyingElement, substitutionGroupsOrSelf).map(e -> (XmlElement) e))
                .or(() -> optionallyCreateOtherXlResource(underlyingElement, substitutionGroupsOrSelf).map(e -> (XmlElement) e))
                .orElse(
                        new OtherXmlElementImpl(underlyingElement, this::createXmlElement)
                );
    }

    public Optional<XmlElement> optionallyCreateXmlElement(AncestryAwareElementApi<?> underlyingElement, Set<QName> substitutionGroupsOrSelf) {
        return optionallyCreateSchemaElement(underlyingElement, substitutionGroupsOrSelf).map(e -> (XmlElement) e)
                .or(() -> optionallyCreateRefElement(underlyingElement, substitutionGroupsOrSelf).map(e -> (XmlElement) e))
                .or(() -> optionallyCreateLabelElement(underlyingElement, substitutionGroupsOrSelf).map(e -> (XmlElement) e))
                .or(() -> optionallyCreateReferenceElement(underlyingElement, substitutionGroupsOrSelf).map(e -> (XmlElement) e))
                .or(() -> optionallyCreateGenElement(underlyingElement, substitutionGroupsOrSelf).map(e -> (XmlElement) e))
                .or(() -> optionallyCreateLinkElement(underlyingElement, substitutionGroupsOrSelf).map(e -> (XmlElement) e));
    }

    public Optional<SchemaElement> optionallyCreateSchemaElement(AncestryAwareElementApi<?> underlyingElement, Set<QName> substitutionGroupsOrSelf) {
        if (underlyingElement.elementName().getNamespaceURI().equals(XS_NS)) {
            return Optional.ofNullable(schemaElementCreators.get(underlyingElement.elementName()))
                    .map(f -> f.apply(underlyingElement))
                    .or(() -> Optional.of(new OtherSchemaElementImpl(underlyingElement, this::createXmlElement)));
        } else {
            return Optional.empty();
        }
    }

    public Optional<LinkElement> optionallyCreateLinkElement(AncestryAwareElementApi<?> underlyingElement, Set<QName> substitutionGroupsOrSelf) {
        return optionallyCreate(
                underlyingElement,
                substitutionGroupsOrSelf,
                LINK_NS,
                linkElementCreators,
                e -> new OtherLinkElementImpl(e, this::createXmlElement)
        );
    }

    public Optional<RefElement> optionallyCreateRefElement(AncestryAwareElementApi<?> underlyingElement, Set<QName> substitutionGroupsOrSelf) {
        return optionallyCreate(
                underlyingElement,
                substitutionGroupsOrSelf,
                REF_NS,
                refElementCreators,
                e -> new OtherRefElementImpl(e, this::createXmlElement)
        );
    }

    public Optional<GenElement> optionallyCreateGenElement(AncestryAwareElementApi<?> underlyingElement, Set<QName> substitutionGroupsOrSelf) {
        return optionallyCreate(
                underlyingElement,
                substitutionGroupsOrSelf,
                GEN_NS,
                genElementCreators,
                e -> new OtherGenElementImpl(e, this::createXmlElement)
        );
    }

    public Optional<LabelElement> optionallyCreateLabelElement(AncestryAwareElementApi<?> underlyingElement, Set<QName> substitutionGroupsOrSelf) {
        return optionallyCreate(
                underlyingElement,
                substitutionGroupsOrSelf,
                LABEL_NS,
                labelElementCreators,
                e -> new OtherLabelElementImpl(e, this::createXmlElement)
        );
    }

    public Optional<ReferenceElement> optionallyCreateReferenceElement(AncestryAwareElementApi<?> underlyingElement, Set<QName> substitutionGroupsOrSelf) {
        return optionallyCreate(
                underlyingElement,
                substitutionGroupsOrSelf,
                REFERENCE_NS,
                referenceElementCreators,
                e -> new OtherReferenceElementImpl(e, this::createXmlElement)
        );
    }

    public Optional<ElementDeclaration> optionallyCreateElementDeclaration(AncestryAwareElementApi<?> underlyingElement) {
        return Optional.of(underlyingElement)
                .filter(e -> e.elementName().equals(XS_ELEMENT_QNAME))
                .map(e -> {
                    // This time we look at the substitution groups of the element declaration's substitution group attribute, if any
                    // So here we do not look at the substitution groups of the element name, which is xs:element in any case
                    ImmutableSet<QName> substGroups =
                            substitutionGroupOption(underlyingElement)
                                    .map(schemaContext::findSubstitutionGroupsOrSelf)
                                    .orElse(ImmutableSet.of());

                    if (substGroups.contains(XBRLDT_HYPERCUBE_ITEM_QNAME)) {
                        return new HypercubeItemDeclarationImpl(underlyingElement, this::createXmlElement);
                    } else if (substGroups.contains(XBRLDT_DIMENSION_ITEM_QNAME)) {
                        return new DimensionItemDeclarationImpl(underlyingElement, this::createXmlElement);
                    } else if (substGroups.contains(XBRLI_ITEM_QNAME)) {
                        return new ItemDeclarationImpl(underlyingElement, this::createXmlElement);
                    } else if (substGroups.contains(XBRLI_TUPLE_QNAME)) {
                        return new TupleDeclarationImpl(underlyingElement, this::createXmlElement);
                    } else {
                        return new ElementDeclarationImpl(underlyingElement, this::createXmlElement);
                    }
                });
    }

    public Optional<Schema> optionallyCreateSchema(AncestryAwareElementApi<?> underlyingElement) {
        Set<QName> sgsOrSelf = schemaContext().findSubstitutionGroupsOrSelf(underlyingElement.elementName());
        return optionallyCreateSchema(underlyingElement, sgsOrSelf);
    }

    public Optional<Schema> optionallyCreateSchema(AncestryAwareElementApi<?> underlyingElement, Set<QName> substitutionGroupsOrSelf) {
        return optionallyCreateSchemaElement(underlyingElement, substitutionGroupsOrSelf)
                .filter(e -> e instanceof Schema)
                .map(e -> (Schema) e);
    }

    public Optional<Linkbase> optionallyCreateLinkbase(AncestryAwareElementApi<?> underlyingElement) {
        Set<QName> sgsOrSelf = schemaContext().findSubstitutionGroupsOrSelf(underlyingElement.elementName());
        return optionallyCreateLinkbase(underlyingElement, sgsOrSelf);
    }

    public Optional<Linkbase> optionallyCreateLinkbase(AncestryAwareElementApi<?> underlyingElement, Set<QName> substitutionGroupsOrSelf) {
        return optionallyCreateLinkElement(underlyingElement, substitutionGroupsOrSelf)
                .filter(e -> e instanceof Linkbase)
                .map(e -> (Linkbase) e);
    }

    private Optional<XlArc> optionallyCreateOtherXlArc(AncestryAwareElementApi<?> underlyingElement, Set<QName> substitutionGroupsOrSelf) {
        Optional<QName> sgOrSelfOption = substitutionGroupsOrSelf.stream().filter(n -> n.equals(XL_ARC_QNAME)).findFirst();

        if (sgOrSelfOption.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(
                    new OtherXlArcImpl(underlyingElement, this::createXmlElement)
            );
        }
    }

    private Optional<XlExtendedLink> optionallyCreateOtherXlLink(AncestryAwareElementApi<?> underlyingElement, Set<QName> substitutionGroupsOrSelf) {
        Optional<QName> sgOrSelfOption = substitutionGroupsOrSelf.stream().filter(n -> n.equals(XL_EXTENDED_QNAME)).findFirst();

        if (sgOrSelfOption.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(
                    new OtherXlExtendedLinkImpl(underlyingElement, this::createXmlElement)
            );
        }
    }

    private Optional<XlResource> optionallyCreateOtherXlResource(AncestryAwareElementApi<?> underlyingElement, Set<QName> substitutionGroupsOrSelf) {
        Optional<QName> sgOrSelfOption = substitutionGroupsOrSelf.stream().filter(n -> n.equals(XL_RESOURCE_QNAME)).findFirst();

        if (sgOrSelfOption.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(
                    new OtherXlResourceImpl(underlyingElement, this::createXmlElement)
            );
        }
    }

    private <T extends XmlElement> Optional<T> optionallyCreate(
            AncestryAwareElementApi<?> underlyingElement,
            Set<QName> substitutionGroupsOrSelf,
            String targetNamespace,
            ImmutableMap<QName, Function<AncestryAwareElementApi<?>, T>> elementCreatorMap,
            Function<AncestryAwareElementApi<?>, T> fallbackElementCreator
    ) {
        Optional<QName> sgOrSelfOption = substitutionGroupsOrSelf.stream().filter(n -> n.getNamespaceURI().equals(targetNamespace)).findFirst();

        if (sgOrSelfOption.isEmpty()) {
            return Optional.empty();
        } else {
            QName name = sgOrSelfOption.orElseThrow();

            return Optional.ofNullable(elementCreatorMap.get(name))
                    .map(f -> f.apply(underlyingElement))
                    .or(() -> Optional.of(fallbackElementCreator.apply(underlyingElement)));
        }
    }

    private ImmutableMap<QName, Function<AncestryAwareElementApi<?>, SchemaElement>> createSchemaElementCreatorMap() {
        ImmutableMap.Builder<QName, Function<AncestryAwareElementApi<?>, SchemaElement>> builder =
                new ImmutableMap.Builder<>();
        builder.put(XS_ELEMENT_QNAME, e -> optionallyCreateElementDeclaration(e).orElseThrow());
        builder.put(XS_ATTRIBUTE_QNAME, e -> new AttributeDeclarationImpl(e, this::createXmlElement));
        builder.put(XS_GROUP_QNAME, e -> new GroupImpl(e, this::createXmlElement));
        builder.put(XS_ATTRIBUTE_GROUP_QNAME, e -> new AttributeGroupImpl(e, this::createXmlElement));
        builder.put(XS_ANNOTATION_QNAME, e -> new AnnotationSchemaElementImpl(e, this::createXmlElement));
        builder.put(XS_APPINFO_QNAME, e -> new AppInfoImpl(e, this::createXmlElement));
        builder.put(XS_SCHEMA_QNAME, e -> new SchemaImpl(e, this::createXmlElement));
        builder.put(XS_COMPLEX_TYPE_QNAME, e -> new ComplexTypeImpl(e, this::createXmlElement));
        builder.put(XS_SIMPLE_TYPE_QNAME, e -> new SimpleTypeImpl(e, this::createXmlElement));
        builder.put(XS_IMPORT_QNAME, e -> new ImportImpl(e, this::createXmlElement));
        builder.put(XS_INCLUDE_QNAME, e -> new IncludeImpl(e, this::createXmlElement));
        return builder.build();
    }

    private ImmutableMap<QName, Function<AncestryAwareElementApi<?>, LinkElement>> createLinkElementCreatorMap() {
        ImmutableMap.Builder<QName, Function<AncestryAwareElementApi<?>, LinkElement>> builder =
                new ImmutableMap.Builder<>();
        builder.put(LINK_ARCROLE_REF_QNAME, e -> new ArcroleRefImpl(e, this::createXmlElement));
        builder.put(LINK_ARCROLE_TYPE_QNAME, e -> new ArcroleTypeImpl(e, this::createXmlElement));
        builder.put(LINK_CALCULATION_ARC_QNAME, e -> new CalculationArcImpl(e, this::createXmlElement));
        builder.put(LINK_CALCULATION_LINK_QNAME, e -> new CalculationLinkImpl(e, this::createXmlElement));
        builder.put(LINK_DEFINITION_QNAME, e -> new DefinitionImpl(e, this::createXmlElement));
        builder.put(LINK_DEFINITION_ARC_QNAME, e -> new DefinitionArcImpl(e, this::createXmlElement));
        builder.put(LINK_DEFINITION_LINK_QNAME, e -> new DefinitionLinkImpl(e, this::createXmlElement));
        builder.put(LINK_FOOTNOTE_QNAME, e -> new FootnoteImpl(e, this::createXmlElement));
        builder.put(LINK_FOOTNOTE_ARC_QNAME, e -> new FootnoteArcImpl(e, this::createXmlElement));
        builder.put(LINK_FOOTNOTE_LINK_QNAME, e -> new FootnoteLinkImpl(e, this::createXmlElement));
        builder.put(LINK_LABEL_QNAME, e -> new LabelImpl(e, this::createXmlElement));
        builder.put(LINK_LABEL_ARC_QNAME, e -> new LabelArcImpl(e, this::createXmlElement));
        builder.put(LINK_LABEL_LINK_QNAME, e -> new LabelLinkImpl(e, this::createXmlElement));
        builder.put(LINK_LINKBASE_QNAME, e -> new LinkbaseImpl(e, this::createXmlElement));
        builder.put(LINK_LINKBASE_REF_QNAME, e -> new LinkbaseRefImpl(e, this::createXmlElement));
        builder.put(LINK_LOC_QNAME, e -> new LocImpl(e, this::createXmlElement));
        builder.put(LINK_PART_QNAME, e -> new PartImpl(e, this::createXmlElement));
        builder.put(LINK_PRESENTATION_ARC_QNAME, e -> new PresentationArcImpl(e, this::createXmlElement));
        builder.put(LINK_PRESENTATION_LINK_QNAME, e -> new PresentationLinkImpl(e, this::createXmlElement));
        builder.put(LINK_REFERENCE_QNAME, e -> new ReferenceImpl(e, this::createXmlElement));
        builder.put(LINK_REFERENCE_ARC_QNAME, e -> new ReferenceArcImpl(e, this::createXmlElement));
        builder.put(LINK_REFERENCE_LINK_QNAME, e -> new ReferenceLinkImpl(e, this::createXmlElement));
        builder.put(LINK_ROLE_REF_QNAME, e -> new RoleRefImpl(e, this::createXmlElement));
        builder.put(LINK_ROLE_TYPE_QNAME, e -> new RoleTypeImpl(e, this::createXmlElement));
        builder.put(LINK_SCHEMA_REF_QNAME, e -> new SchemaRefImpl(e, this::createXmlElement));
        builder.put(LINK_USED_ON_QNAME, e -> new UsedOnImpl(e, this::createXmlElement));
        return builder.build();
    }

    private ImmutableMap<QName, Function<AncestryAwareElementApi<?>, RefElement>> createRefElementCreatorMap() {
        ImmutableMap.Builder<QName, Function<AncestryAwareElementApi<?>, RefElement>> builder =
                new ImmutableMap.Builder<>();
        builder.put(REF_APPENDIX_QNAME, e -> new RefAppendixImpl(e, this::createXmlElement));
        builder.put(REF_ARTICLE_QNAME, e -> new RefArticleImpl(e, this::createXmlElement));
        builder.put(REF_CHAPTER_QNAME, e -> new RefChapterImpl(e, this::createXmlElement));
        builder.put(REF_CLAUSE_QNAME, e -> new RefClauseImpl(e, this::createXmlElement));
        builder.put(REF_EXAMPLE_QNAME, e -> new RefExampleImpl(e, this::createXmlElement));
        builder.put(REF_EXHIBIT_QNAME, e -> new RefExhibitImpl(e, this::createXmlElement));
        builder.put(REF_FOOTNOTE_QNAME, e -> new RefFootnoteImpl(e, this::createXmlElement));
        builder.put(REF_ISSUE_DATE_QNAME, e -> new RefIssueDateImpl(e, this::createXmlElement));
        builder.put(REF_NAME_QNAME, e -> new RefNameImpl(e, this::createXmlElement));
        builder.put(REF_NOTE_QNAME, e -> new RefNoteImpl(e, this::createXmlElement));
        builder.put(REF_NUMBER_QNAME, e -> new RefNumberImpl(e, this::createXmlElement));
        builder.put(REF_PAGE_QNAME, e -> new RefPageImpl(e, this::createXmlElement));
        builder.put(REF_PARAGRAPH_QNAME, e -> new RefParagraphImpl(e, this::createXmlElement));
        builder.put(REF_PUBLISHER_QNAME, e -> new RefPublisherImpl(e, this::createXmlElement));
        builder.put(REF_SECTION_QNAME, e -> new RefSectionImpl(e, this::createXmlElement));
        builder.put(REF_SENTENCE_QNAME, e -> new RefSentenceImpl(e, this::createXmlElement));
        builder.put(REF_SUBCLAUSE_QNAME, e -> new RefSubclauseImpl(e, this::createXmlElement));
        builder.put(REF_SUBPARAGRAPH_QNAME, e -> new RefSubparagraphImpl(e, this::createXmlElement));
        builder.put(REF_SUBSECTION_QNAME, e -> new RefSubsectionImpl(e, this::createXmlElement));
        builder.put(REF_URI_QNAME, e -> new RefUriImpl(e, this::createXmlElement));
        builder.put(REF_URI_DATE_QNAME, e -> new RefUriDateImpl(e, this::createXmlElement));
        return builder.build();
    }

    private ImmutableMap<QName, Function<AncestryAwareElementApi<?>, GenElement>> createGenElementCreatorMap() {
        ImmutableMap.Builder<QName, Function<AncestryAwareElementApi<?>, GenElement>> builder =
                new ImmutableMap.Builder<>();
        builder.put(GEN_ARC_QNAME, e -> new GenericArcImpl(e, this::createXmlElement));
        builder.put(GEN_LINK_QNAME, e -> new GenericLinkImpl(e, this::createXmlElement));
        return builder.build();
    }

    private ImmutableMap<QName, Function<AncestryAwareElementApi<?>, LabelElement>> createLabelElementCreatorMap() {
        ImmutableMap.Builder<QName, Function<AncestryAwareElementApi<?>, LabelElement>> builder =
                new ImmutableMap.Builder<>();
        builder.put(LABEL_LABEL_QNAME, e -> new GenericLabelImpl(e, this::createXmlElement));
        return builder.build();
    }

    private ImmutableMap<QName, Function<AncestryAwareElementApi<?>, ReferenceElement>> createReferenceElementCreatorMap() {
        ImmutableMap.Builder<QName, Function<AncestryAwareElementApi<?>, ReferenceElement>> builder =
                new ImmutableMap.Builder<>();
        builder.put(REFERENCE_REFERENCE_QNAME, e -> new GenericReferenceImpl(e, this::createXmlElement));
        return builder.build();
    }

    private ImmutableMap<QName, Function<AncestryAwareElementApi<?>, XmlElement>> createCommonlyUsedElementCreatorMap() {
        ImmutableMap.Builder<QName, Function<AncestryAwareElementApi<?>, XmlElement>> builder =
                new ImmutableMap.Builder<>();
        builder.put(XS_ELEMENT_QNAME, e -> optionallyCreateElementDeclaration(e).orElseThrow());
        builder.put(XS_SCHEMA_QNAME, e -> new SchemaImpl(e, this::createXmlElement));
        builder.put(XS_ANNOTATION_QNAME, e -> new AnnotationSchemaElementImpl(e, this::createXmlElement));
        builder.put(XS_APPINFO_QNAME, e -> new AppInfoImpl(e, this::createXmlElement));
        builder.put(XS_IMPORT_QNAME, e -> new ImportImpl(e, this::createXmlElement));
        builder.put(LINK_LOC_QNAME, e -> new LocImpl(e, this::createXmlElement));
        builder.put(GEN_ARC_QNAME, e -> new GenericArcImpl(e, this::createXmlElement));
        builder.put(GEN_LINK_QNAME, e -> new GenericLinkImpl(e, this::createXmlElement));
        builder.put(LINK_LINKBASE_QNAME, e -> new LinkbaseImpl(e, this::createXmlElement));
        builder.put(LINK_LINKBASE_REF_QNAME, e -> new LinkbaseRefImpl(e, this::createXmlElement));
        builder.put(LINK_ROLE_REF_QNAME, e -> new RoleRefImpl(e, this::createXmlElement));
        builder.put(LINK_ARCROLE_REF_QNAME, e -> new ArcroleRefImpl(e, this::createXmlElement));
        builder.put(LABEL_LABEL_QNAME, e -> new GenericLabelImpl(e, this::createXmlElement));
        builder.put(REFERENCE_REFERENCE_QNAME, e -> new GenericReferenceImpl(e, this::createXmlElement));
        builder.put(LINK_PRESENTATION_ARC_QNAME, e -> new PresentationArcImpl(e, this::createXmlElement));
        builder.put(LINK_DEFINITION_ARC_QNAME, e -> new DefinitionArcImpl(e, this::createXmlElement));
        builder.put(LINK_CALCULATION_ARC_QNAME, e -> new CalculationArcImpl(e, this::createXmlElement));
        builder.put(LINK_LABEL_ARC_QNAME, e -> new LabelArcImpl(e, this::createXmlElement));
        builder.put(LINK_REFERENCE_ARC_QNAME, e -> new ReferenceArcImpl(e, this::createXmlElement));
        builder.put(LINK_PRESENTATION_LINK_QNAME, e -> new PresentationLinkImpl(e, this::createXmlElement));
        builder.put(LINK_DEFINITION_LINK_QNAME, e -> new DefinitionLinkImpl(e, this::createXmlElement));
        builder.put(LINK_CALCULATION_LINK_QNAME, e -> new CalculationLinkImpl(e, this::createXmlElement));
        builder.put(LINK_LABEL_LINK_QNAME, e -> new LabelLinkImpl(e, this::createXmlElement));
        builder.put(LINK_REFERENCE_LINK_QNAME, e -> new ReferenceLinkImpl(e, this::createXmlElement));
        builder.put(LINK_LABEL_QNAME, e -> new LabelImpl(e, this::createXmlElement));
        builder.put(LINK_REFERENCE_QNAME, e -> new ReferenceImpl(e, this::createXmlElement));
        return builder.build();
    }

    private static Optional<QName> substitutionGroupOption(AncestryAwareElementApi<?> element) {
        Optional<String> syntacticQNameOption = element.attributeOption(SUBSTITUTION_GROUP_QNAME);
        return syntacticQNameOption
                .map(n -> element.namespaceScopeOption().orElseThrow().resolveSyntacticElementQName(n));
    }
}
