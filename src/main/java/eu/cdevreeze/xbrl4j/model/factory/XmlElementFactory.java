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

import static eu.cdevreeze.xbrl4j.model.Names.*;

/**
 * Factory of XmlElement instances. Must be very fast. It is not aware of dimensions
 * (and generic links).
 *
 * @author Chris de Vreeze
 */
public class XmlElementFactory {

    private final SchemaContext schemaContext;

    public XmlElementFactory(SchemaContext schemaContext) {
        this.schemaContext = schemaContext;
    }

    public SchemaContext schemaContext() {
        return schemaContext;
    }

    // TODO Speed up. We don't have to re-compute substitution groups all the time.

    public XmlElement createXmlElement(AncestryAwareElementApi<?> underlyingElement) {
        return optionallyCreateXmlElement(underlyingElement)
                .or(() -> optionallyCreateOtherXlArc(underlyingElement).map(e -> (XmlElement) e))
                .or(() -> optionallyCreateOtherXlLink(underlyingElement).map(e -> (XmlElement) e))
                .or(() -> optionallyCreateOtherXlResource(underlyingElement).map(e -> (XmlElement) e))
                .orElse(
                        new OtherXmlElementImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
    }

    public Optional<XmlElement> optionallyCreateXmlElement(AncestryAwareElementApi<?> underlyingElement) {
        return optionallyCreateSchemaElement(underlyingElement).map(e -> (XmlElement) e)
                .or(() -> optionallyCreateLinkElement(underlyingElement).map(e -> (XmlElement) e));
    }

    public Optional<SchemaElement> optionallyCreateSchemaElement(AncestryAwareElementApi<?> underlyingElement) {
        if (underlyingElement.elementName().getNamespaceURI().equals(XS_NS)) {
            return switch (underlyingElement.elementName().getLocalPart()) {
                case "element" -> Optional.of(
                        new ElementDeclarationImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "attribute" -> Optional.of(
                        new AttributeDeclarationImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "group" -> Optional.of(
                        new GroupImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "attributeGroup" -> Optional.of(
                        new AttributeGroupImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "annotation" -> Optional.of(
                        new AnnotationSchemaElementImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "appinfo" -> Optional.of(
                        new AppInfoImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "schema" -> Optional.of(
                        new SchemaImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "complexType" -> Optional.of(
                        new ComplexTypeImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "simpleType" -> Optional.of(
                        new SimpleTypeImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "import" -> Optional.of(
                        new ImportImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "include" -> Optional.of(
                        new IncludeImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                default ->
                        Optional.of(new OtherSchemaElementImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement));
            };
        } else {
            return Optional.empty();
        }
    }

    public Optional<LinkElement> optionallyCreateLinkElement(AncestryAwareElementApi<?> underlyingElement) {
        Set<QName> sgsOrSelf = schemaContext().findSubstitutionGroupsOrSelf(underlyingElement.elementName());
        Optional<QName> sgOrSelfOption = sgsOrSelf.stream().filter(n -> n.getNamespaceURI().equals(LINK_NS)).findFirst();

        if (sgOrSelfOption.isEmpty()) {
            return Optional.empty();
        } else {
            QName name = sgOrSelfOption.orElseThrow();

            return switch (name.getLocalPart()) {
                case "loc" -> Optional.of(
                        new LocImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "definitionArc" -> Optional.of(
                        new DefinitionArcImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "presentationArc" -> Optional.of(
                        new PresentationArcImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "calculationArc" -> Optional.of(
                        new CalculationArcImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "labelArc" -> Optional.of(
                        new LabelArcImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "referenceArc" -> Optional.of(
                        new ReferenceArcImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "footnoteArc" -> Optional.of(
                        new FootnoteArcImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "definitionLink" -> Optional.of(
                        new DefinitionLinkImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "presentationLink" -> Optional.of(
                        new PresentationLinkImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "calculationLink" -> Optional.of(
                        new CalculationLinkImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "labelLink" -> Optional.of(
                        new LabelLinkImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "referenceLink" -> Optional.of(
                        new ReferenceLinkImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "footnoteLink" -> Optional.of(
                        new FootnoteLinkImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "label" -> Optional.of(
                        new LabelImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "reference" -> Optional.of(
                        new ReferenceImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "footnote" -> Optional.of(
                        new FootnoteImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "linkbase" -> Optional.of(
                        new LinkbaseImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "arcroleRef" -> Optional.of(
                        new ArcroleRefImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "roleRef" -> Optional.of(
                        new RoleRefImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "linkbaseRef" -> Optional.of(
                        new LinkbaseRefImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "schemaRef" -> Optional.of(
                        new SchemaRefImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "arcroleType" -> Optional.of(
                        new ArcroleTypeImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "roleType" -> Optional.of(
                        new RoleTypeImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "definition" -> Optional.of(
                        new DefinitionImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "part" -> Optional.of(
                        new PartImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                case "usedOn" -> Optional.of(
                        new UsedOnImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
                );
                default ->
                        Optional.of(new OtherLinkElementImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement));
            };
        }
    }

    public Optional<Schema> optionallyCreateSchema(AncestryAwareElementApi<?> underlyingElement) {
        return optionallyCreateSchemaElement(underlyingElement)
                .filter(e -> e instanceof Schema)
                .map(e -> (Schema) e);
    }

    public Optional<Linkbase> optionallyCreateLinkbase(AncestryAwareElementApi<?> underlyingElement) {
        return optionallyCreateLinkElement(underlyingElement)
                .filter(e -> e instanceof Linkbase)
                .map(e -> (Linkbase) e);
    }

    private Optional<XlArc> optionallyCreateOtherXlArc(AncestryAwareElementApi<?> underlyingElement) {
        Set<QName> sgsOrSelf = schemaContext().findSubstitutionGroupsOrSelf(underlyingElement.elementName());
        Optional<QName> sgOrSelfOption = sgsOrSelf.stream().filter(n -> n.equals(XL_ARC_QNAME)).findFirst();

        if (sgOrSelfOption.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(
                    new OtherXlArcImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
            );
        }
    }

    private Optional<XlExtendedLink> optionallyCreateOtherXlLink(AncestryAwareElementApi<?> underlyingElement) {
        Set<QName> sgsOrSelf = schemaContext().findSubstitutionGroupsOrSelf(underlyingElement.elementName());
        Optional<QName> sgOrSelfOption = sgsOrSelf.stream().filter(n -> n.equals(XL_EXTENDED_QNAME)).findFirst();

        if (sgOrSelfOption.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(
                    new OtherXlExtendedLinkImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
            );
        }
    }

    private Optional<XlResource> optionallyCreateOtherXlResource(AncestryAwareElementApi<?> underlyingElement) {
        Set<QName> sgsOrSelf = schemaContext().findSubstitutionGroupsOrSelf(underlyingElement.elementName());
        Optional<QName> sgOrSelfOption = sgsOrSelf.stream().filter(n -> n.equals(XL_RESOURCE_QNAME)).findFirst();

        if (sgOrSelfOption.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(
                    new OtherXlResourceImpl(underlyingElement, schemaContext, XmlElementFactory::createXmlElement)
            );
        }
    }

    // TODO Do we really need this? I don't think so. We can simplify the BiFunction to a Function.
    private static XmlElement createXmlElement(AncestryAwareElementApi<?> underlyingElement, SchemaContext schemaContext) {
        return new XmlElementFactory(schemaContext).createXmlElement(underlyingElement);
    }
}
