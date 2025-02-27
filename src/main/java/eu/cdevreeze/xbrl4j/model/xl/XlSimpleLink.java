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

import java.net.URI;
import java.util.Optional;

/**
 * Any XLink simple link element in the context of XBRL. See schema xl-2003-12-31.xsd, which has target namespace
 * "http://www.xbrl.org/2003/XLink". The "xlink:type" attribute has value "simple".
 *
 * @author Chris de Vreeze
 */
public interface XlSimpleLink extends XlElement {

    XLinkType xlinkType();

    URI href();

    Optional<String> arcroleOption();

    Optional<String> roleOption();

    Optional<String> titleOption();

    Optional<Show> showOption();

    Optional<Actuate> actuateOption();
}
