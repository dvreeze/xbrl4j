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

package eu.cdevreeze.xbrl4j.model.internal.xl;

import eu.cdevreeze.xbrl4j.model.xl.XlElement;
import eu.cdevreeze.xbrl4j.model.xlink.XLinkElement;
import eu.cdevreeze.yaidom4j.queryapi.ElementApi;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Optional;
import java.util.OptionalInt;

import static eu.cdevreeze.xbrl4j.model.Names.*;

/**
 * Support for retrieving simple link attributes.
 *
 * @author Chris de Vreeze
 */
public class XLinkSupport {

    private XLinkSupport() {
    }

    // XLink attributes

    public static URI href(ElementApi<?> element) {
        return URI.create(element.attribute(XLINK_HREF_QNAME));
    }

    public static Optional<String> arcroleOption(ElementApi<?> element) {
        return element.attributeOption(XLINK_ARCROLE_QNAME);
    }

    public static Optional<String> roleOption(ElementApi<?> element) {
        return element.attributeOption(XLINK_ROLE_QNAME);
    }

    public static Optional<String> titleOption(ElementApi<?> element) {
        return element.attributeOption(XLINK_TITLE_QNAME);
    }

    public static Optional<XLinkElement.Show> showOption(ElementApi<?> element) {
        return element.attributeOption(XLINK_SHOW_QNAME).map(XLinkElement.Show::parse);
    }

    public static Optional<XLinkElement.Actuate> actuateOption(ElementApi<?> element) {
        return element.attributeOption(XLINK_ACTUATE_QNAME).map(XLinkElement.Actuate::parse);
    }

    public static Optional<XLinkElement.XLinkType> xlinkTypeOption(ElementApi<?> element) {
        return element.attributeOption(XLINK_TYPE_QNAME).map(XLinkElement.XLinkType::parse);
    }

    public static String from(ElementApi<?> element) {
        return element.attribute(XLINK_FROM_QNAME);
    }

    public static String to(ElementApi<?> element) {
        return element.attribute(XLINK_TO_QNAME);
    }

    public static String label(ElementApi<?> element) {
        return element.attribute(XLINK_LABEL_QNAME);
    }

    // Non-XLink attributes

    public static Optional<BigDecimal> orderOption(ElementApi<?> element) {
        return element.attributeOption(ORDER_QNAME).map(BigDecimal::new);
    }

    public static Optional<XlElement.Use> useOption(ElementApi<?> element) {
        return element.attributeOption(USE_QNAME).map(XlElement.Use::parse);
    }

    public static OptionalInt priorityOption(ElementApi<?> element) {
        return element.attributeOption(PRIORITY_QNAME).stream().mapToInt(Integer::parseInt).findFirst();
    }
}
