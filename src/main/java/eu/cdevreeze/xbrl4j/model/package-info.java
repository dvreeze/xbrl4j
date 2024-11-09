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

/**
 * Any XML element in the context of XBRL. The idea is that construction of an XmlELement
 * (or typically creating an instance of a subtype) should never fail, and that the
 * ElementApi methods of XmlElement should never fail either. As a consequence, the XmlElement
 * API is always useful while validating a potentially incorrect instance or taxonomy document, as long
 * as (during validation) we stay away from some methods outside the ElementApi part.
 * <p>
 * Implementations should be immutable and thread-safe.
 * <p>
 * The XML element type hierarchy follows the corresponding "core" XML schemas, in particular with respect
 * to substitution groups. That also implies that there is no hard separation between taxonomy elements
 * and instance elements. After all, footnote links and presentation links are both XLink extended
 * links, so they share at least that "ancestry". The subpackage names are organized according to
 * preferred prefixes of the target namespaces of the corresponding core schemas. Substitution group membership
 * (within the core XBRL schemas) is represented by interface inheritance.
 *
 * @author Chris de Vreeze
 */
package eu.cdevreeze.xbrl4j.model;
