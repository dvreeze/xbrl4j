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

package eu.cdevreeze.xbrl4j.model;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

/**
 * QName constants for element and attribute names, etc.
 *
 * @author Chris de Vreeze
 */
public class Names {

    // Namespaces

    public static final String XML_NS = XMLConstants.XML_NS_URI;
    public static final String LINK_NS = "http://www.xbrl.org/2003/linkbase";
    public static final String XL_NS = "http://www.xbrl.org/2003/XLink";
    public static final String XLINK_NS = "http://www.w3.org/1999/xlink";
    public static final String XBRLI_NS = "http://www.xbrl.org/2003/instance";
    public static final String XBRLDI_NS = "http://xbrl.org/2006/xbrldi";
    public static final String XBRLDT_NS = "http://xbrl.org/2005/xbrldt";
    public static final String XS_NS = "http://www.w3.org/2001/XMLSchema"; // Schema 1.0 and 1.1
    public static final String GEN_NS = "http://xbrl.org/2008/generic";
    public static final String LABEL_NS = "http://xbrl.org/2008/label";
    public static final String REFERENCE_NS = "http://xbrl.org/2008/reference";
    public static final String REF_NS = "http://www.xbrl.org/2006/ref";

    // Element names (including substitution groups, which normally are abstract elements)

    public static final QName XS_ELEMENT_QNAME = new QName(XS_NS, "element");
    public static final QName XS_ATTRIBUTE_QNAME = new QName(XS_NS, "attribute");
    public static final QName XS_GROUP_QNAME = new QName(XS_NS, "group");
    public static final QName XS_ATTRIBUTE_GROUP_QNAME = new QName(XS_NS, "attributeGroup");
    public static final QName XS_APPINFO_QNAME = new QName(XS_NS, "appinfo");
    public static final QName XS_ANNOTATION_QNAME = new QName(XS_NS, "annotation");
    public static final QName XS_COMPLEX_TYPE_QNAME = new QName(XS_NS, "complexType");
    public static final QName XS_SIMPLE_TYPE_QNAME = new QName(XS_NS, "simpleType");
    public static final QName XS_SCHEMA_QNAME = new QName(XS_NS, "schema");
    public static final QName XS_IMPORT_QNAME = new QName(XS_NS, "import");
    public static final QName XS_INCLUDE_QNAME = new QName(XS_NS, "include");

    public static final QName XBRLI_ITEM_QNAME = new QName(XBRLI_NS, "item");
    public static final QName XBRLI_TUPLE_QNAME = new QName(XBRLI_NS, "tuple");

    public static final QName XBRLDT_HYPERCUBE_ITEM_QNAME = new QName(XBRLDT_NS, "hypercubeItem");
    public static final QName XBRLDT_DIMENSION_ITEM_QNAME = new QName(XBRLDT_NS, "dimensionItem");

    public static final QName LINK_ARCROLE_REF_QNAME = new QName(LINK_NS, "arcroleRef");
    public static final QName LINK_ARCROLE_TYPE_QNAME = new QName(LINK_NS, "arcroleType");
    public static final QName LINK_CALCULATION_ARC_QNAME = new QName(LINK_NS, "calculationArc");
    public static final QName LINK_CALCULATION_LINK_QNAME = new QName(LINK_NS, "calculationLink");
    public static final QName LINK_DEFINITION_QNAME = new QName(LINK_NS, "definition");
    public static final QName LINK_DEFINITION_ARC_QNAME = new QName(LINK_NS, "definitionArc");
    public static final QName LINK_DEFINITION_LINK_QNAME = new QName(LINK_NS, "definitionLink");
    public static final QName LINK_FOOTNOTE_QNAME = new QName(LINK_NS, "footnote");
    public static final QName LINK_FOOTNOTE_ARC_QNAME = new QName(LINK_NS, "footnoteArc");
    public static final QName LINK_FOOTNOTE_LINK_QNAME = new QName(LINK_NS, "footnoteLink");
    public static final QName LINK_LABEL_QNAME = new QName(LINK_NS, "label");
    public static final QName LINK_LABEL_ARC_QNAME = new QName(LINK_NS, "labelArc");
    public static final QName LINK_LABEL_LINK_QNAME = new QName(LINK_NS, "labelLink");
    public static final QName LINK_LINKBASE_QNAME = new QName(LINK_NS, "linkbase");
    public static final QName LINK_LINKBASE_REF_QNAME = new QName(LINK_NS, "linkbaseRef");
    public static final QName LINK_LOC_QNAME = new QName(LINK_NS, "loc");
    public static final QName LINK_PART_QNAME = new QName(LINK_NS, "part");
    public static final QName LINK_PRESENTATION_ARC_QNAME = new QName(LINK_NS, "presentationArc");
    public static final QName LINK_PRESENTATION_LINK_QNAME = new QName(LINK_NS, "presentationLink");
    public static final QName LINK_REFERENCE_QNAME = new QName(LINK_NS, "reference");
    public static final QName LINK_REFERENCE_ARC_QNAME = new QName(LINK_NS, "referenceArc");
    public static final QName LINK_REFERENCE_LINK_QNAME = new QName(LINK_NS, "referenceLink");
    public static final QName LINK_ROLE_REF_QNAME = new QName(LINK_NS, "roleRef");
    public static final QName LINK_ROLE_TYPE_QNAME = new QName(LINK_NS, "roleType");
    public static final QName LINK_SCHEMA_REF_QNAME = new QName(LINK_NS, "schemaRef");
    public static final QName LINK_USED_ON_QNAME = new QName(LINK_NS, "usedOn");

    public static final QName GEN_ARC_QNAME = new QName(GEN_NS, "arc");
    public static final QName GEN_LINK_QNAME = new QName(GEN_NS, "link");

    public static final QName LABEL_LABEL_QNAME = new QName(LABEL_NS, "label");

    public static final QName REFERENCE_REFERENCE_QNAME = new QName(REFERENCE_NS, "reference");

    public static final QName XL_ARC_QNAME = new QName(XL_NS, "arc");
    public static final QName XL_EXTENDED_QNAME = new QName(XL_NS, "extended");
    public static final QName XL_RESOURCE_QNAME = new QName(XL_NS, "resource");

    public static final QName REF_PUBLISHER_QNAME = new QName(REF_NS, "Publisher");
    public static final QName REF_NAME_QNAME = new QName(REF_NS, "Name");
    public static final QName REF_NUMBER_QNAME = new QName(REF_NS, "Number");
    public static final QName REF_ISSUE_DATE_QNAME = new QName(REF_NS, "IssueDate");
    public static final QName REF_CHAPTER_QNAME = new QName(REF_NS, "Chapter");
    public static final QName REF_ARTICLE_QNAME = new QName(REF_NS, "Article");
    public static final QName REF_NOTE_QNAME = new QName(REF_NS, "Note");
    public static final QName REF_SECTION_QNAME = new QName(REF_NS, "Section");
    public static final QName REF_SUBSECTION_QNAME = new QName(REF_NS, "Subsection");
    public static final QName REF_PARAGRAPH_QNAME = new QName(REF_NS, "Paragraph");
    public static final QName REF_SUBPARAGRAPH_QNAME = new QName(REF_NS, "Subparagraph");
    public static final QName REF_CLAUSE_QNAME = new QName(REF_NS, "Clause");
    public static final QName REF_SUBCLAUSE_QNAME = new QName(REF_NS, "Subclause");
    public static final QName REF_APPENDIX_QNAME = new QName(REF_NS, "Appendix");
    public static final QName REF_EXAMPLE_QNAME = new QName(REF_NS, "Example");
    public static final QName REF_PAGE_QNAME = new QName(REF_NS, "Page");
    public static final QName REF_EXHIBIT_QNAME = new QName(REF_NS, "Exhibit");
    public static final QName REF_FOOTNOTE_QNAME = new QName(REF_NS, "Footnote");
    public static final QName REF_SENTENCE_QNAME = new QName(REF_NS, "Sentence");
    public static final QName REF_URI_QNAME = new QName(REF_NS, "URI");
    public static final QName REF_URI_DATE_QNAME = new QName(REF_NS, "URIDate");

    // Attribute names

    public static final QName ARCROLE_URI_QNAME = new QName("arcroleURI");
    public static final QName CYCLES_ALLOWED_QNAME = new QName("cyclesAllowed");
    public static final QName ID_QNAME = new QName("id");
    public static final QName NAME_QNAME = new QName("name");
    public static final QName ORDER_QNAME = new QName("order");
    public static final QName PREFERRED_LABEL_QNAME = new QName("preferredLabel");
    public static final QName PRIORITY_QNAME = new QName("priority");
    public static final QName ROLE_URI_QNAME = new QName("roleURI");
    public static final QName USE_QNAME = new QName("use");
    public static final QName WEIGHT_QNAME = new QName("weight");
    public static final QName SUBSTITUTION_GROUP_QNAME = new QName("substitutionGroup");

    public static final QName XLINK_ACTUATE_QNAME = new QName(XLINK_NS, "actuate");
    public static final QName XLINK_ARCROLE_QNAME = new QName(XLINK_NS, "arcrole");
    public static final QName XLINK_FROM_QNAME = new QName(XLINK_NS, "from");
    public static final QName XLINK_HREF_QNAME = new QName(XLINK_NS, "href");
    public static final QName XLINK_LABEL_QNAME = new QName(XLINK_NS, "label");
    public static final QName XLINK_ROLE_QNAME = new QName(XLINK_NS, "role");
    public static final QName XLINK_SHOW_QNAME = new QName(XLINK_NS, "show");
    public static final QName XLINK_TITLE_QNAME = new QName(XLINK_NS, "title");
    public static final QName XLINK_TO_QNAME = new QName(XLINK_NS, "to");
    public static final QName XLINK_TYPE_QNAME = new QName(XLINK_NS, "type");

    public static final QName XML_BASE_QNAME = new QName(XML_NS, "base");
}
