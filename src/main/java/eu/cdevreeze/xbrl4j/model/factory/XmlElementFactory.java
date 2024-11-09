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
import eu.cdevreeze.xbrl4j.model.XmlElement;
import eu.cdevreeze.xbrl4j.model.internal.OtherXmlElementImpl;
import eu.cdevreeze.xbrl4j.model.internal.link.*;
import eu.cdevreeze.xbrl4j.model.internal.xl.OtherXlArcImpl;
import eu.cdevreeze.xbrl4j.model.internal.xl.OtherXlExtendedLinkImpl;
import eu.cdevreeze.xbrl4j.model.internal.xl.OtherXlResourceImpl;
import eu.cdevreeze.xbrl4j.model.internal.xs.*;
import eu.cdevreeze.xbrl4j.model.link.LinkElement;
import eu.cdevreeze.xbrl4j.model.link.Linkbase;
import eu.cdevreeze.xbrl4j.model.xl.XlArc;
import eu.cdevreeze.xbrl4j.model.xl.XlExtendedLink;
import eu.cdevreeze.xbrl4j.model.xl.XlResource;
import eu.cdevreeze.xbrl4j.model.xs.Schema;
import eu.cdevreeze.xbrl4j.model.xs.SchemaElement;
import eu.cdevreeze.yaidom4j.queryapi.AncestryAwareElementApi;

import javax.xml.namespace.QName;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static eu.cdevreeze.xbrl4j.model.Names.*;

/**
 * Factory of XmlElement instances. Must be very fast.
 *
 * @author Chris de Vreeze
 */
public class XmlElementFactory {

    private final SchemaContext schemaContext;

    private final ImmutableMap<QName, Function<AncestryAwareElementApi<?>, SchemaElement>> schemaElementCreators =
            createSchemaElementCreatorMap();

    private final ImmutableMap<QName, Function<AncestryAwareElementApi<?>, LinkElement>> linkElementCreators =
            createLinkElementCreatorMap();

    public XmlElementFactory(SchemaContext schemaContext) {
        this.schemaContext = schemaContext;
    }

    public SchemaContext schemaContext() {
        return schemaContext;
    }

    public XmlElement createXmlElement(AncestryAwareElementApi<?> underlyingElement) {
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
        Optional<QName> sgOrSelfOption = substitutionGroupsOrSelf.stream().filter(n -> n.getNamespaceURI().equals(LINK_NS)).findFirst();

        if (sgOrSelfOption.isEmpty()) {
            return Optional.empty();
        } else {
            QName name = sgOrSelfOption.orElseThrow();

            return Optional.ofNullable(linkElementCreators.get(name))
                    .map(f -> f.apply(underlyingElement))
                    .or(() -> Optional.of(new OtherLinkElementImpl(underlyingElement, this::createXmlElement)));
        }
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

    private ImmutableMap<QName, Function<AncestryAwareElementApi<?>, SchemaElement>> createSchemaElementCreatorMap() {
        ImmutableMap.Builder<QName, Function<AncestryAwareElementApi<?>, SchemaElement>> result =
                new ImmutableMap.Builder<>();
        result.put(XS_ELEMENT_QNAME, e -> new ElementDeclarationImpl(e, this::createXmlElement));
        result.put(XS_ATTRIBUTE_QNAME, e -> new AttributeDeclarationImpl(e, this::createXmlElement));
        result.put(XS_GROUP_QNAME, e -> new GroupImpl(e, this::createXmlElement));
        result.put(XS_ATTRIBUTE_GROUP_QNAME, e -> new AttributeGroupImpl(e, this::createXmlElement));
        result.put(XS_ANNOTATION_QNAME, e -> new AnnotationSchemaElementImpl(e, this::createXmlElement));
        result.put(XS_APPINFO_QNAME, e -> new AppInfoImpl(e, this::createXmlElement));
        result.put(XS_SCHEMA_QNAME, e -> new SchemaImpl(e, this::createXmlElement));
        result.put(XS_COMPLEX_TYPE_QNAME, e -> new ComplexTypeImpl(e, this::createXmlElement));
        result.put(XS_SIMPLE_TYPE_QNAME, e -> new SimpleTypeImpl(e, this::createXmlElement));
        result.put(XS_IMPORT_QNAME, e -> new ImportImpl(e, this::createXmlElement));
        result.put(XS_INCLUDE_QNAME, e -> new IncludeImpl(e, this::createXmlElement));
        return result.build();
    }

    private ImmutableMap<QName, Function<AncestryAwareElementApi<?>, LinkElement>> createLinkElementCreatorMap() {
        ImmutableMap.Builder<QName, Function<AncestryAwareElementApi<?>, LinkElement>> result =
                new ImmutableMap.Builder<>();
        result.put(LINK_ARCROLE_REF_QNAME, e -> new ArcroleRefImpl(e, this::createXmlElement));
        result.put(LINK_ARCROLE_TYPE_QNAME, e -> new ArcroleTypeImpl(e, this::createXmlElement));
        result.put(LINK_CALCULATION_ARC_QNAME, e -> new CalculationArcImpl(e, this::createXmlElement));
        result.put(LINK_CALCULATION_LINK_QNAME, e -> new CalculationLinkImpl(e, this::createXmlElement));
        result.put(LINK_DEFINITION_QNAME, e -> new DefinitionImpl(e, this::createXmlElement));
        result.put(LINK_DEFINITION_ARC_QNAME, e -> new DefinitionArcImpl(e, this::createXmlElement));
        result.put(LINK_DEFINITION_LINK_QNAME, e -> new DefinitionLinkImpl(e, this::createXmlElement));
        result.put(LINK_FOOTNOTE_QNAME, e -> new FootnoteImpl(e, this::createXmlElement));
        result.put(LINK_FOOTNOTE_ARC_QNAME, e -> new FootnoteArcImpl(e, this::createXmlElement));
        result.put(LINK_FOOTNOTE_LINK_QNAME, e -> new FootnoteLinkImpl(e, this::createXmlElement));
        result.put(LINK_LABEL_QNAME, e -> new LabelImpl(e, this::createXmlElement));
        result.put(LINK_LABEL_ARC_QNAME, e -> new LabelArcImpl(e, this::createXmlElement));
        result.put(LINK_LABEL_LINK_QNAME, e -> new LabelLinkImpl(e, this::createXmlElement));
        result.put(LINK_LINKBASE_QNAME, e -> new LinkbaseImpl(e, this::createXmlElement));
        result.put(LINK_LINKBASE_REF_QNAME, e -> new LinkbaseRefImpl(e, this::createXmlElement));
        result.put(LINK_LOC_QNAME, e -> new LocImpl(e, this::createXmlElement));
        result.put(LINK_PART_QNAME, e -> new PartImpl(e, this::createXmlElement));
        result.put(LINK_PRESENTATION_ARC_QNAME, e -> new PresentationArcImpl(e, this::createXmlElement));
        result.put(LINK_PRESENTATION_LINK_QNAME, e -> new PresentationLinkImpl(e, this::createXmlElement));
        result.put(LINK_REFERENCE_QNAME, e -> new ReferenceImpl(e, this::createXmlElement));
        result.put(LINK_REFERENCE_ARC_QNAME, e -> new ReferenceArcImpl(e, this::createXmlElement));
        result.put(LINK_REFERENCE_LINK_QNAME, e -> new ReferenceLinkImpl(e, this::createXmlElement));
        result.put(LINK_ROLE_REF_QNAME, e -> new RoleRefImpl(e, this::createXmlElement));
        result.put(LINK_ROLE_TYPE_QNAME, e -> new RoleTypeImpl(e, this::createXmlElement));
        result.put(LINK_SCHEMA_REF_QNAME, e -> new SchemaRefImpl(e, this::createXmlElement));
        result.put(LINK_USED_ON_QNAME, e -> new UsedOnImpl(e, this::createXmlElement));
        return result.build();
    }
}
