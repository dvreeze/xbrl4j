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

import javax.xml.namespace.QName;

/**
 * QName constants for element and attribute names, etc.
 *
 * @author Chris de Vreeze
 */
public class Names {

    // Namespaces

    public static final String LINK_NS = "http://www.xbrl.org/2003/linkbase";
    public static final String XL_NS = "http://www.xbrl.org/2003/XLink";
    public static final String XLINK_NS = "http://www.w3.org/1999/xlink";
    public static final String XS_NS = "http://www.w3.org/2001/XMLSchema"; // Schema 1.0 and 1.1
    public static final String GEN_NS = "http://xbrl.org/2008/generic";
    public static final String LABEL_NS = "http://xbrl.org/2008/label";
    public static final String REFERENCE_NS = "http://xbrl.org/2008/reference";
    public static final String REF_NS = "http://www.xbrl.org/2006/ref";

    // Element names (including substitution groups)

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

    public static final QName LINK_PART_QNAME = new QName(LINK_NS, "part");

    public static final QName GEN_ARC_QNAME = new QName(GEN_NS, "arc");
    public static final QName GEN_LINK_QNAME = new QName(GEN_NS, "link");

    public static final QName LABEL_LABEL_QNAME = new QName(LABEL_NS, "label");
    public static final QName REFERENCE_REFERENCE_QNAME = new QName(REFERENCE_NS, "reference");

    public static final QName XL_ARC_QNAME = new QName(XL_NS, "arc");
    public static final QName XL_EXTENDED_QNAME = new QName(XL_NS, "extended");
    public static final QName XL_RESOURCE_QNAME = new QName(XL_NS, "resource");

    // Attribute names

    public static final QName ARCROLE_URI_QNAME = new QName("arcroleURI");
    public static final QName CYCLES_ALLOWED_QNAME = new QName("cyclesAllowed");
    public static final QName ID_QNAME = new QName("id");
    public static final QName ORDER_QNAME = new QName("order");
    public static final QName PREFERRED_LABEL_QNAME = new QName("preferredLabel");
    public static final QName PRIORITY_QNAME = new QName("priority");
    public static final QName ROLE_URI_QNAME = new QName("roleURI");
    public static final QName USE_QNAME = new QName("use");
    public static final QName WEIGHT_QNAME = new QName("weight");

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
}