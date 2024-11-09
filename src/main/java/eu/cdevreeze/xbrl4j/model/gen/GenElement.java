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

package eu.cdevreeze.xbrl4j.model.gen;

import eu.cdevreeze.xbrl4j.model.XmlElement;

/**
 * Any element in the context of XBRL in the "http://xbrl.org/2008/generic" namespace, either
 * directly or via a substitution group in that namespace. See schema http://www.xbrl.org/2008/generic-link.xsd.
 *
 * @author Chris de Vreeze
 */
public interface GenElement extends XmlElement {
}
