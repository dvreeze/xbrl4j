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

import com.google.common.collect.ImmutableList;

import java.util.Optional;

/**
 * Any XLink extended link element in the context of XBRL. See schema xl-2003-12-31.xsd, which has target namespace
 * "http://www.xbrl.org/2003/XLink". The "xlink:type" attribute has value "extended".
 *
 * @author Chris de Vreeze
 */
public interface XlExtendedLink extends XlElement {

    XLinkType xlinkType();

    String role();

    Optional<String> titleOption();

    Optional<String> idOption();

    ImmutableList<? extends XlTitle> titleElements();

    ImmutableList<? extends XlLocator> locators();

    ImmutableList<? extends XlArc> arcs();

    ImmutableList<? extends XlResource> resources();
}
