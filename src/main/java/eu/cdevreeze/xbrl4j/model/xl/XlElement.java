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

package eu.cdevreeze.xbrl4j.model.xl;

import eu.cdevreeze.xbrl4j.model.xlink.XLinkElement;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Any XLink element in the context of XBRL. See schema xl-2003-12-31.xsd, which has target namespace
 * "http://www.xbrl.org/2003/XLink".
 *
 * @author Chris de Vreeze
 */
public interface XlElement extends XLinkElement {

    Optional<XLinkType> xlinkTypeOption();

    String XL_NS = "http://www.xbrl.org/2003/XLink";

    enum Use {
        OPTIONAL("optional"), PROHIBITED("prohibited");

        private final String value;

        Use(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static Use parse(String s) {
            return Stream.of(values()).filter(v -> v.toString().equals(s)).findFirst().orElseThrow();
        }
    }
}
